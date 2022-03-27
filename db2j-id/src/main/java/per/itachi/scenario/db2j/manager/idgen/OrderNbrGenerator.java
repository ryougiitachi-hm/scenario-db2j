package per.itachi.scenario.db2j.manager.idgen;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import per.itachi.scenario.db2j.manager.IdGenerator;

/**
 * By default, an order number consists of :
 * [date] + [dbNbr] + [tableNbr] + [sequence]
 * [8bytes] + [3bytes] + [3bytes] + [11bytes]
 * */
@Primary
@Component
public class OrderNbrGenerator implements IdGenerator {

    /**
     * configurable/non-configurable
     * */
    private int countOfDatetimeBytes = 8;

    /**
     * configurable
     * */
    private int countOfDbNbrBytes = 3;

    /**
     * configurable
     * */
    private int countOfTableNbrBytes = 3;

    /**
     * configurable
     * */
    private int countOfSeqBytes = 11;

    @Autowired
    private SequenceGenerator sequenceGenerator;

    @Override
    public String generate(String tableName, int tableNbr, String columnName) {
        StringBuilder builder = new StringBuilder();
        builder.append(DateTimeFormatter
                .ofPattern("yyyyMMdd").format(LocalDate.now()));// not graceful
        builder.append(String.format("%0" + countOfDbNbrBytes + "d",
                ThreadLocalRandom.current().nextInt(1000))); // should be configurable
        builder.append(String.format("%0" + countOfTableNbrBytes + "d",
                tableNbr));
        builder.append(String.format("%0" + countOfSeqBytes + "d",
                sequenceGenerator.generateNextSequence(tableName, tableNbr, columnName)));
        return builder.toString();
    }

}
