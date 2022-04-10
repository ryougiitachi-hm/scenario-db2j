package per.itachi.scenario.db2j.manager.idgen;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SnowflakeDto {

    private long timestamp;

    private long dbNbr;

    private long tableNbr;

    private long sequence;
}
