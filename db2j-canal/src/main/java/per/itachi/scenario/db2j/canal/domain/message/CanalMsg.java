package per.itachi.scenario.db2j.canal.domain.message;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
public class CanalMsg {

    private List<Map<String, Object>> data;

    private String database;

    private Long es;

    private Long id;

    private Boolean isDdl;

    private Map<String, String> mysqlType;

    private Object old;

    private List<String> pkNames;

    private String sql;

    private Map<String, Integer> sqlType;

    private String table;

    private Long ts;

    private String type;
}
