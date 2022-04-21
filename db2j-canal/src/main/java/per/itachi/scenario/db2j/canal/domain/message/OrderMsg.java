package per.itachi.scenario.db2j.canal.domain.message;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class OrderMsg {

    private Long id;

    private String orderNbr;

    private String userId;

    private LocalDateTime cdate;

    private Integer version;
}