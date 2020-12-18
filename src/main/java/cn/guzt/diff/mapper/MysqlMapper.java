package cn.guzt.diff.mapper;

import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MysqlMapper {

    /**
     * 表信息
     *
     * @param dbName schema
     * @return List
     */
    List<JSONObject> getTables(@Param("dbName") String dbName);

    /**
     * 表信息
     *
     * @param dbName schema
     * @return List
     */
    List<JSONObject> getColumns(@Param("dbName") String dbName);

    /**
     * 表信息
     *
     * @param dbName schema
     * @return List
     */
    List<JSONObject> getIndexs(@Param("dbName") String dbName);

    /**
     * 表信息
     *
     * @param dbName schema
     * @return List
     */
    List<JSONObject> getPartitions(@Param("dbName") String dbName);

    /**
     * 表信息
     *
     * @param dbName schema
     * @return List
     */
    List<JSONObject> getTirggers(@Param("dbName") String dbName);

    /**
     * 表信息
     *
     * @param dbName schema
     * @return List
     */
    List<JSONObject> getProcedures(@Param("dbName") String dbName);

    /**
     * 表信息
     *
     * @param dbName schema
     * @return List
     */
    List<JSONObject> getFunctions(@Param("dbName") String dbName);

    /**
     * test sql
     *
     * @return 1
     */
    int checkDbConnect();

}
