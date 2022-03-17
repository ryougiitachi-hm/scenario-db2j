package per.itachi.scenario.db2j.manager.idgen;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class DbStepSequenceKey {

    private String tableName;

    private Integer tableNbr;

    private String columnName;
}
