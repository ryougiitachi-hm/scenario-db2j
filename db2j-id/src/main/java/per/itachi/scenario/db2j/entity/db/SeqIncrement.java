package per.itachi.scenario.db2j.entity.db;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SeqIncrement {

    private Long id;

    private String tableName;

    private Integer tableNbr;

    private String columnName;

    private Integer currentPos;

    private Integer stepSize;

    private Integer version;

    private LocalDateTime cdate;

    private LocalDateTime edate;
}
