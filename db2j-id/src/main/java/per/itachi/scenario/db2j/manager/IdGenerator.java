package per.itachi.scenario.db2j.manager;

import java.io.Serializable;
import javax.validation.constraints.NotNull;

public interface IdGenerator {

    @NotNull
    Serializable generate(String tableName, int tableNbr, String columnName);
}
