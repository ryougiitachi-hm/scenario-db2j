package per.itachi.scenario.db2j.manager.idgen;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
@Builder
public class DbStepSequenceValue {

    /**
     * only stores incremental int value.
     * */
    private AtomicInteger increment;

    private int currentPos;

    private int stepSize;
}
