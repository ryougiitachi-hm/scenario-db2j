package per.itachi.scenario.db2j.adaptee.controller;

import java.io.Serializable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import per.itachi.scenario.db2j.manager.IdGenerator;
import per.itachi.scenario.db2j.manager.idgen.SnowflakeDto;
import per.itachi.scenario.db2j.manager.idgen.SnowflakeUtils;

@Slf4j
@RestController
@RequestMapping("/db")
public class TestDatabaseController {

    @Value("${db.id-gen.snowflake.count-of-bits.timestamp}")
    private int countOfTimestampBits = 41;

    @Value("${db.id-gen.snowflake.count-of-bits.db-nbr}")
    private int countOfDbNoBits = 5;

    @Value("${db.id-gen.snowflake.count-of-bits.table-nbr}")
    private int countOfTableNoBits = 5;

    @Value("${db.id-gen.snowflake.count-of-bits.sequence}")
    private int countOfIncrBits = 12;

    @Autowired
    private IdGenerator idGenerator;

    @GetMapping("/id-generator/snowflake")
    public Serializable testSnowflakeIdGenerator(@RequestParam String tableName,
                                         @RequestParam int tableNbr, @RequestParam String columnName) {
        Serializable id = idGenerator.generate(tableName, tableNbr, columnName);
        SnowflakeDto dto = SnowflakeUtils.convertLongToDto((long)id, countOfTimestampBits,
                countOfDbNoBits, countOfTableNoBits, countOfIncrBits);
        log.info("The generated id is {}, which consists of {}. ", id, dto);
        return id;
    }

}
