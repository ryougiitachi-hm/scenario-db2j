package per.itachi.scenario.db2j.adapter.rdbms;

import per.itachi.scenario.db2j.entity.db.SeqIncrement;

public interface SeqIncrementPort {

    SeqIncrement generateNextStep(String tableName, int tableNbr, String columnName);
}
