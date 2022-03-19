package per.itachi.scenario.db2j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication
public class IdGenApplication {

    public static void main(String[] args) {
        SpringApplication.run(IdGenApplication.class, args);
    }

}
