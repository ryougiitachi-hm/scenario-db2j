package per.itachi.scenario.db2j.adaptee.controller;

import java.io.Serializable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import per.itachi.scenario.db2j.manager.IdGenerator;

@Slf4j
@RestController
@RequestMapping("/db")
public class TestDatabaseController {

    @Autowired
    private IdGenerator idGenerator;

    @GetMapping("/id-generator/snowflake")
    public Serializable testSnowflakeIdGenerator(@RequestParam String tableName,
                                         @RequestParam int tableNbr, @RequestParam String columnName) {
        Serializable id = idGenerator.generate(tableName, tableNbr, columnName);
        log.info("The generated id is {}. ", id);
        return id;
    }
}
