package per.itachi.scenario.db2j.manager.idgen;

import java.util.concurrent.ThreadLocalRandom;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import per.itachi.scenario.db2j.manager.IdGenerator;

@Slf4j
@Primary
@Component
public class SnowflakeIdGenerator implements IdGenerator {

    /**
     * configurable
     * */
    @Value("${db.id-gen.snowflake.count-of-bits.timestamp}")
    private int countOfTimestampBits = 41;

    /**
     * configurable
     * */
    @Value("${db.id-gen.snowflake.count-of-bits.db-nbr}")
    private int countOfDbNoBits = 5;

    /**
     * configurable
     * */
    @Value("${db.id-gen.snowflake.count-of-bits.table-nbr}")
    private int countOfTableNoBits = 5;

    /**
     * configurable
     * */
    @Value("${db.id-gen.snowflake.count-of-bits.sequence}")
    private int countOfIncrBits = 12;

    /**
     * count of dbs
     * */
    private int countOfDbs = 6;

    /**
     * count of tables per database
     * */
    private int countOfTables = 5;

    @Autowired
    private SequenceGenerator sequenceGenerator;

    @Override
    public Long generate(String tableName, int tableNbr, String columnName) {
        validateCountOfBits();
        long id = 0;
        // timestamp
        long timestamp = System.currentTimeMillis() & (0X7FFFFFFFFFFFFFFFL >> (63 - countOfTimestampBits));
//        id <<= countOfTimestampBits; // not necessary
        id |= timestamp;
        // db nbr
        int dbNbr = ThreadLocalRandom.current().nextInt(countOfDbs);
        id <<= countOfDbNoBits;
        id |= Math.abs(dbNbr);
        // table nbr
//        int tableNbr = ThreadLocalRandom.current().nextInt(countOfTables);
        id <<= countOfTableNoBits;
        id |= Math.abs(tableNbr);
        // incremental
        int sequence = sequenceGenerator.generateNextSequence(tableName, tableNbr, columnName);
        id <<= countOfIncrBits;
        id |= sequence & (0X7FFFFFFF >> (32 - countOfIncrBits));
        log.debug("Created new snowflake, id={}, timestamp={}, dbNbr={}, tableNbr={}, sequence={}. ",
                id, timestamp, dbNbr, tableNbr, sequence);
        log.debug("Created new snowflake for hex, id={}, timestamp={}, dbNbr={}, tableNbr={}, sequence={}. ",
                String.format("%016X", id), String.format("%016X", timestamp), String.format("%08X", dbNbr),
                String.format("%08X", tableNbr), String.format("%08X", sequence));
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
