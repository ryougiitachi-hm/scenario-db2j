package per.itachi.scenario.db2j.manager.idgen;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import per.itachi.scenario.db2j.adapter.rdbms.SeqIncrementPort;
import per.itachi.scenario.db2j.entity.db.SeqIncrement;

/**
 * need to consider solutions about the following limits:
 * limit of step size, reloading db;
 * limit of sequence bits part,
 *
 * Reset current position, may be optional, too complicated ( *_ *).
 * */
@Slf4j
@Component
public class DbStepSequenceGenerator implements SequenceGenerator{

    /**
     * just disposable
     * */
    @Value("${db.id-gen.snowflake.sequence.step-size}")
    private int stepSize;

    @Value("${db.id-gen.snowflake.sequence.log-count-of-retry-threshold}")
    private int logCountOfRetryThreshold;

    @Autowired
    private SeqIncrementPort seqIncrementPort;

    private ConcurrentMap<List<Serializable>, DbStepSequenceValue> stepSequences;

    @PostConstruct
    public void init() {
        this.stepSequences = new ConcurrentHashMap<>();
    }

    /**
     * Removing @Transactional should be better.
     * */
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
            SeqIncrement seqIncrement = generateNextStepWithLoopRetry(tableName, tableNbr, columnName);
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
        int currentIncrement = increment.incrementAndGet();
//        -- tmpIncrement; // 0-based can avoid -- operation

        // TODO: local synchronization?
        // How to take a lock with granularity of the specific key.
        int countOfRetry = 0;
        while (currentIncrement > value.getStepSize()) {
            if (++countOfRetry >= logCountOfRetryThreshold) {
                log.warn("The count of retry for generateNextStepWithLoopRetry is {}, tableName={}, tableNbr={}, columnName={}. ",
                        countOfRetry, tableName, tableNbr, columnName);
            }
            // beyond step size, reload db.
            SeqIncrement seqIncrement = generateNextStepWithLoopRetry(tableName, tableNbr, columnName);
            value = DbStepSequenceValue.builder()
                    .increment(new AtomicInteger(0))
                    .currentPos(seqIncrement.getCurrentPos())
                    .stepSize(seqIncrement.getStepSize())
                    .build();
            stepSequences.put(key, value);
            increment = value.getIncrement(); // key statement, if no, infinite retry.
            currentIncrement = increment.incrementAndGet();
        }

        return value.getCurrentPos() + currentIncrement;
    }

    /**
     * workaround
     * */
    private SeqIncrement generateNextStepWithLoopRetry(String tableName, int tableNbr, String columnName) {
        int countOfRetry = 0;
        Optional<SeqIncrement> seqIncrementOptional = Optional.empty();
        do {
            if (++countOfRetry >= logCountOfRetryThreshold) {
                log.warn("The count of retry for generateNextStepWithLoopRetry is {}, tableName={}, tableNbr={}, columnName={}. ",
                        countOfRetry, tableName, tableNbr, columnName);
            }
            seqIncrementOptional = seqIncrementPort
                    .generateNextStep(tableName, tableNbr, columnName);
        } while (!seqIncrementOptional.isPresent()); // infinite loop retry.
        return seqIncrementOptional.get();
    }

}
