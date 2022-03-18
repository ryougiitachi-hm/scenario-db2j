package per.itachi.scenario.db2j.manager;

public interface IdGenerator {

    long generate(String tableName, int tableNbr, String columnName);
}
