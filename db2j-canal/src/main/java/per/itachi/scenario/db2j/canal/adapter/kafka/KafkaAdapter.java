package per.itachi.scenario.db2j.canal.adapter.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import per.itachi.scenario.db2j.canal.common.constant.Constants;

import java.util.UUID;

@Component
public class KafkaAdapter implements KafkaPort {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void publishMessage() {
        kafkaTemplate.send(Constants.KAFKA_TOPIC_CANAL_MYSQL_MASTER_33001,
                UUID.randomUUID().toString(), UUID.randomUUID().toString());
    }
}
