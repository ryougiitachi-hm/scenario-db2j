package per.itachi.scenario.db2j.manager.idgen;

public class SnowflakeUtils {

    public static SnowflakeDto convertLongToDto(long snowflake,
                                                int countOfTimestamp, int countOfDbNbr,
                                                int countOfTableNbr, int countOfSequence) {
        SnowflakeDto dto = new SnowflakeDto();
        dto.setTimestamp(((snowflake & 0x7fffffffffffffffL) >> (countOfDbNbr + countOfTableNbr + countOfSequence))
                & (Long.MIN_VALUE >> (Long.SIZE - countOfTimestamp) ));
        dto.setDbNbr(((snowflake & Long.MAX_VALUE) >> (countOfTableNbr + countOfSequence))
                & (Long.MIN_VALUE >> (Long.SIZE - countOfDbNbr) ));
        dto.setDbNbr(((snowflake & Long.MAX_VALUE) >> (countOfSequence))
                & (Long.MIN_VALUE >> (Long.SIZE - countOfTableNbr) ));
        dto.setDbNbr(((snowflake & Long.MAX_VALUE))
                & (Long.MIN_VALUE >> (Long.SIZE - countOfSequence) ));
        return dto;
    }

    private SnowflakeUtils() {}
}
