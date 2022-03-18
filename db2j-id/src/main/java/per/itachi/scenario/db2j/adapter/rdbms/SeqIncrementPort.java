package per.itachi.scenario.db2j.adapter.rdbms;

import per.itachi.scenario.db2j.entity.db.SeqIncrement;

import java.util.Optional;

public interface SeqIncrementPort {

    Optional<SeqIncrement> generateNextStep(String tableName, int tableNbr, String columnName);

    Optional<SeqIncrement> save(String tableName, int tableNbr, String columnName);
}
