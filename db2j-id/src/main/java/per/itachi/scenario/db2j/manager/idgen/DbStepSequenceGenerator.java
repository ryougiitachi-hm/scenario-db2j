package per.itachi.scenario.db2j.manager.idgen;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import per.itachi.scenario.db2j.entity.db.SeqIncrement;
import per.itachi.scenario.db2j.repository.rdbms.SeqIncrementMapper;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * need to consider the following limit:
 * limit of step size,
 * limit of sequence bits part,
 * */
@Component
public class DbStepSequenceGenerator implements SequenceGenerator{

    private static final int DEFAULT_STEP_SIZE = 20;

    /**
     * just disposable
     * */
    @Value("${db.id-gen.snowflake.sequence.step-size}")
    private int stepSize;

    @Autowired
    private SeqIncrementMapper seqIncrementMapper;

    private ConcurrentMap<List<Serializable>, DbStepSequenceValue> stepSequences;

    @PostConstruct
    public void init() {
        this.stepSequences = new ConcurrentHashMap<>();
    }

    @Transactional
    @Override
    public int generateNextSequence(String tableName, int tableNbr, String columnName) {
        Objects.requireNonNull(tableName, "tableName can not be null. ");
        Objects.requireNonNull(columnName, "columnName can not be null. ");
        // Problem may occur under high concurrency.
        ConcurrentMap<List<Serializable>, DbStepSequenceValue> stepSequences = this.stepSequences;
        List<Serializable> key = Arrays.asList(tableName, tableNbr, columnName);
        DbStepSequenceValue value = stepSequences.get(key);

        // load from db when the corresponding stepSequence hasn't initialized yet.
        if(value == null) {
            // may load repeatly under high concurrency.
            SeqIncrement seqIncrement = seqIncrementMapper
                    .findByTableNameAndNbrAndCol(tableName, tableNbr, columnName);
            if (seqIncrement == null) {
                seqIncrement = new SeqIncrement();
                seqIncrement.setTableName(tableName);
                seqIncrement.setTableNbr(tableNbr);
                seqIncrement.setColumnName(columnName);
                seqIncrement.setStepSize(DEFAULT_STEP_SIZE);
                seqIncrement.setCurrentPos(1);
                seqIncrement.setVersion(1);
                seqIncrement.setCdate(LocalDateTime.now());
                seqIncrement.setEdate(LocalDateTime.now());
                seqIncrementMapper.save(seqIncrement);
            }
            seqIncrementMapper.updateCurPosByTableNameAndNbrAndCol(seqIncrement); // TODO: lacks retry.
            value = DbStepSequenceValue.builder()
                    .increment(new AtomicInteger(seqIncrement.getCurrentPos()))
                    .currentPos(seqIncrement.getCurrentPos())
                    .stepSize(seqIncrement.getStepSize())
                    .build();
        }
        return 0;
    }
}
