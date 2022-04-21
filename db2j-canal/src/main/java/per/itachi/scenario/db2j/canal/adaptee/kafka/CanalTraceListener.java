package per.itachi.scenario.db2j.canal.adaptee.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import per.itachi.scenario.db2j.canal.domain.message.CanalMsg;
import per.itachi.scenario.db2j.canal.domain.message.OrderMsg;
import per.itachi.scenario.db2j.canal.domain.message.UserMsg;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static per.itachi.scenario.db2j.canal.common.constant.Constants.KAFKA_TOPIC_CANAL_MYSQL_MASTER_33001;

@Slf4j
@Component
public class CanalTraceListener {

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "canal-master-33001-user", groupId = "canal-master",
            properties = "spring.json.value.default.type=per.itachi.scenario.db2j.canal.domain.message.CanalMsg")
    public void traceUserData(CanalMsg record, Acknowledgment ack) {
        log.info("Received ConsumerRecord: {}", record);
        UserMsg[] userMsgs = convertListMapToObject(UserMsg[].class, record.getData());
        List<UserMsg> userMsgList = Arrays.asList(userMsgs);
        log.info("Received ConsumerRecord row data is {}", userMsgList);
        ack.acknowledge();
    }

    @KafkaListener(topics = "canal-master-33001-order", groupId = "canal-master",
            properties = "spring.json.value.default.type=per.itachi.scenario.db2j.canal.domain.message.CanalMsg")
    public void traceOrderData(CanalMsg record, Acknowledgment ack) {
        log.info("Received ConsumerRecord: {}", record);
        OrderMsg[] orderMsgs = convertListMapToObject(OrderMsg[].class, record.getData());
        List<OrderMsg> orderMsgList = Arrays.asList(orderMsgs);
        log.info("Received ConsumerRecord row data is {}", orderMsgList);
        ack.acknowledge();
    }

    /**
     * A bit low, because spring-kafka can't convert generic type.
     * */
    private <T> T convertListMapToObject(Class<T> clazz, List<Map<String, Object>> map) {
        try {
            String json = objectMapper.writeValueAsString(map);
            return objectMapper.readValue(json, clazz);
        }
        catch (JsonProcessingException e) {
            log.error("", e);
            return null;
        }
    }
}
