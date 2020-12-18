package cn.guzt.diff.util.mysql;

import cn.guzt.diff.mapper.MysqlMapper;
import cn.guzt.diff.properties.DbObjType;
import cn.guzt.diff.service.bean.DiffPrepareBean;
import cn.guzt.diff.util.mybatis.bean.DbProperties;
import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 根据数据库对象类型做具体的一些准备工作处理
 *
 * @author guzt
 */
public abstract class AbstractPrepare {

    /**
     * 其他业务类调用时直接从 PREPARE_SERVICE_MAP 中根据DbObjType取具体的实现类进行处理相关准备工作
     */
    private static final ConcurrentHashMap<DbObjType, AbstractPrepare> PREPARE_SERVICE_MAP = new ConcurrentHashMap<>();

    public static AbstractPrepare getPrepareService(DbObjType type) {
        return PREPARE_SERVICE_MAP.get(type);
    }


    public AbstractPrepare(DbObjType type) {
        PREPARE_SERVICE_MAP.put(type, this);
    }

    /**
     * 根据数据库配置查询指定的数据库对象类型信息列表
     *
     * @param mapper ignore
     * @param db     ignore
     * @return ignore
     */
    public abstract List<JSONObject> getObjList(MysqlMapper mapper, DbProperties db);

    /**
     * 将原来查询出的数据库对象列表转换一个MAP，用于快速比对操作
     *
     * @param originalRow  ignore
     * @param targetMapRow ignore
     * @param targetRowKey ignore
     */
    public abstract void setRowToMap(List<JSONObject> originalRow, Map<String, JSONObject> targetMapRow, List<String> targetRowKey);

    /**
     * 两个数据库对象进行比较.
     *
     * @param original 源数据库
     * @param target   目标数据库
     * @return 返回比较结果
     */
    public abstract List<JSONObject> diffCompare(DiffPrepareBean original, DiffPrepareBean target);

}
