package cn.guzt.diff.mapper;

import com.alibaba.fastjson.JSONObject;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MysqlMapper {

    List<JSONObject> getTables(@Param("dbName") String dbName);

    List<JSONObject> getColumns(@Param("dbName") String dbName);

    List<JSONObject> getIndexs(@Param("dbName") String dbName);

    List<JSONObject> getPartitions(@Param("dbName") String dbName);

    List<JSONObject> getTirggers(@Param("dbName") String dbName);

    List<JSONObject> getProcedures(@Param("dbName") String dbName);

    List<JSONObject> getFunctions(@Param("dbName") String dbName);

    int checkDbConnect();

}
