package per.itachi.scenario.db2j.maxwell.adaptee.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MaxwellTraceListener {

    @KafkaListener(topics = "maxwell-trace-master", groupId = "maxwell-master")
    public void trace(String messsage) {
        log.info("Receive msg: {}. ", messsage);
    }
}
