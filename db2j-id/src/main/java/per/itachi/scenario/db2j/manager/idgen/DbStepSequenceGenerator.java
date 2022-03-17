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
 * need to consider solutions about the following limits:
 * limit of step size, reloading db;
 * limit of sequence bits part,
 *
 * Reset current position, may be optional, too complicated ( *_ *).
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
            // TODO: may load repeatly under high concurrency, local synchronized?
            // Considering about multiple operations in the same jvm process,
            // local synchronization may be feasible and operable, e.g. ReentrantLock or synchronized.
            SeqIncrement seqIncrement = generateNextStep(tableName, tableNbr, columnName);
            value = DbStepSequenceValue.builder()
                    .increment(new AtomicInteger(0))
                    .currentPos(seqIncrement.getCurrentPos())
                    .stepSize(seqIncrement.getStepSize())
                    .build();
            stepSequences.putIfAbsent(key, value);
        }

        // First get and then increment ? 2 threads may get the same value concurrently, unreasonable.
        // First increment and then get ? AtomicInteger must guarantee serializable increment.
        // Under over-counting, it is required to distinguish between bound case and outbound case.
        AtomicInteger increment = value.getIncrement();
        int tmpIncrement = increment.incrementAndGet();
//        -- tmpIncrement; // 0-based can avoid -- operation
        if (tmpIncrement <= value.getStepSize()) {
            return value.getCurrentPos() + tmpIncrement;
        }

        // TODO: local synchronization?
        // How to take a lock with granularity of the specific key.
        SeqIncrement seqIncrement = generateNextStep(tableName, tableNbr, columnName);
        value = DbStepSequenceValue.builder()
                .increment(new AtomicInteger(0))
                .currentPos(seqIncrement.getCurrentPos())
                .stepSize(seqIncrement.getStepSize())
                .build();
        stepSequences.put(key, value);

        // while loop may be better.
        increment = value.getIncrement();
        tmpIncrement = increment.incrementAndGet();
        return value.getCurrentPos() + tmpIncrement;
    }

    private SeqIncrement generateNextStep(String tableName, int tableNbr, String columnName) {
        SeqIncrement seqIncrement = seqIncrementMapper
                .findByTableNameAndNbrAndCol(tableName, tableNbr, columnName);
        if (seqIncrement == null) {
            seqIncrement = new SeqIncrement();
            seqIncrement.setTableName(tableName);
            seqIncrement.setTableNbr(tableNbr);
            seqIncrement.setColumnName(columnName);
            seqIncrement.setStepSize(DEFAULT_STEP_SIZE);
            seqIncrement.setCurrentPos(0); // 0-based, which can avoid -- operation, 1 is the first element.
            seqIncrement.setVersion(1);
            seqIncrement.setCdate(LocalDateTime.now());
            seqIncrement.setEdate(LocalDateTime.now());
            seqIncrementMapper.save(seqIncrement);
        }
        seqIncrementMapper.updateCurPosByTableNameAndNbrAndCol(seqIncrement); // TODO: lacks retry.
        return seqIncrement;
    }

}
