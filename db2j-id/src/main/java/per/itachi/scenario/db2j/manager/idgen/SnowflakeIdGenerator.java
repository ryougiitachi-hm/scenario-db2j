package per.itachi.scenario.db2j.manager.idgen;

import java.util.concurrent.ThreadLocalRandom;
import per.itachi.scenario.db2j.manager.IdGenerator;

public class SnowflakeIdGenerator implements IdGenerator {

    /**
     * configurable
     * */
    private int countOfTimestampBits = 41;

    /**
     * configurable
     * */
    private int countOfDbNoBits = 5;

    /**
     * configurable
     * */
    private int countOfTableNoBits = 5;

    /**
     * configurable
     * */
    private int countOfIncrBits = 12;

    /**
     * count of dbs
     * */
    private int countOfDbs = 6;

    /**
     * count of tables
     * */
    private int countOfTables = 5;

    @Override
    public long generate() {
        validateCountOfBits();
        long id = 0;
        // timestamp
//        id <<= countOfTimestampBits; // not necessary
        id |= System.currentTimeMillis() & (0X7FFFFFFFFFFFFFFFL >> countOfTimestampBits);
        // db nbr
        int dbNbr = ThreadLocalRandom.current().nextInt(countOfDbs);
        id <<= countOfDbNoBits;
        id |= Math.abs(dbNbr);
        // table nbr
        int tableNbr = ThreadLocalRandom.current().nextInt(countOfTables);
        id <<= countOfTableNoBits;
        id |= Math.abs(tableNbr);
        // incremental
        return id;
    }

    private void validateCountOfBits() {
        if (countOfTimestampBits <= 0
                || countOfDbNoBits <= 0
                || countOfTableNoBits <= 0
                || countOfIncrBits <= 0) {
            throw new RuntimeException(); // custom exception
        }
        int countOfBits = countOfTimestampBits + countOfDbNoBits
                + countOfTableNoBits + countOfIncrBits;
        if (countOfBits != 63) {
            // more flexible or more feasible
            throw new RuntimeException(); // custom exception
        }
    }
}
