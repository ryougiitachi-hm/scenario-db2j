package per.itachi.scenario.db2j.manager.idgen;

public interface SequenceGenerator {

    int generateNextSequence(String tableName, int tableNbr, String columnName);
}
