package per.itachi.scenario.db2j.adapter.rdbms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import per.itachi.scenario.db2j.entity.db.SeqIncrement;
import per.itachi.scenario.db2j.repository.rdbms.SeqIncrementMapper;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Repository
public class SeqIncrementAdapter implements SeqIncrementPort{

    private static final int DEFAULT_STEP_SIZE = 20;

    @Autowired
    private SeqIncrementMapper mapper;

    /**
     * About isolation level, maybe it is reasonable to set RC considering always getting the latest sequence increment.
     * */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    @Override
    public Optional<SeqIncrement> generateNextStep(String tableName, int tableNbr, String columnName) {
        SeqIncrement seqIncrement = mapper
                .findByTableNameAndNbrAndCol(tableName, tableNbr, columnName);
        if (seqIncrement == null) {
            seqIncrement = save(tableName, tableNbr, columnName).get();
        }

        int countOfRows = mapper.updateCurPosByTableNameAndNbrAndCol(seqIncrement);
        if (countOfRows <= 0) {
            Optional.empty();
        }
        if (countOfRows >= 2) {
            log.warn("The count of impacted rows is {}, updateCurPosByTableNameAndNbrAndCol. ", countOfRows);
        }

        return Optional.of(seqIncrement);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    @Override
    public Optional<SeqIncrement> save(String tableName, int tableNbr, String columnName) {
        SeqIncrement seqIncrement = new SeqIncrement();
        seqIncrement = new SeqIncrement();
        seqIncrement.setTableName(tableName);
        seqIncrement.setTableNbr(tableNbr);
        seqIncrement.setColumnName(columnName);
        seqIncrement.setStepSize(DEFAULT_STEP_SIZE);
        seqIncrement.setCurrentPos(0); // 0-based, which can avoid -- operation, 1 is the first element.
        seqIncrement.setVersion(1);
        seqIncrement.setCdate(LocalDateTime.now());
        seqIncrement.setEdate(LocalDateTime.now());
        try {
            mapper.save(seqIncrement);
        }
        catch (Exception e) {
            // TODO: for unique key
            log.warn("");
//            return Optional.empty();
        }
        return Optional.of(seqIncrement);
    }
}
