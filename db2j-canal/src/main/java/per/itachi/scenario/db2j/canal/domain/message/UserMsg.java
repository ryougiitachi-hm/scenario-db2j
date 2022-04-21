package per.itachi.scenario.db2j.canal.domain.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class UserMsg {

    @JsonProperty("ID")
    private Long id;

    @JsonProperty("USERNAME")
    private String username;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("CDATE")
    private LocalDateTime cdate;

    @NotNull
    @JsonProperty("VERSION")
    private Integer version;
}
