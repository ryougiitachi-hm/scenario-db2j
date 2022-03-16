package per.itachi.scenario.db2j.manager.idgen;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import per.itachi.scenario.db2j.repository.rdbms.SeqIncrementMapper;

@Component
public class DbStepSequenceGenerator implements SequenceGenerator{

    @Autowired
    private SeqIncrementMapper seqIncrementMapper;

    @Transactional
    @Override
    public int generateNextSequence(String tableName, int tableNbr, String columnName) {
        return 0;
    }
}
