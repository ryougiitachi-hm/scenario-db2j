package per.itachi.scenario.db2j.adapter.rdbms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import per.itachi.scenario.db2j.entity.db.SeqIncrement;
import per.itachi.scenario.db2j.repository.rdbms.SeqIncrementMapper;

@Repository
public class SeqIncrementAdapter implements SeqIncrementPort{

    @Autowired
    private SeqIncrementMapper mapper;

    /**
     * About isolation level, maybe it is reasonable to set RC considering always getting the latest sequence increment.
     * */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    @Override
    public SeqIncrement generateNextStep(String tableName, int tableNbr, String columnName) {
        return null;
    }
}
