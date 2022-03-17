package per.itachi.scenario.db2j.repository.rdbms;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import per.itachi.scenario.db2j.entity.db.SeqIncrement;

@Mapper
public interface SeqIncrementMapper {

    SeqIncrement findByTableNameAndNbrAndCol(@Param("tableName") String tableName,
                                             @Param("tableNbr") int tableNbr,
                                             @Param("columnName") String columnName);

    int save(@Param("seqIncrement") SeqIncrement seqIncrement);

    int updateCurPosByTableNameAndNbrAndCol(@Param("seqIncrement") SeqIncrement seqIncrement);

}
