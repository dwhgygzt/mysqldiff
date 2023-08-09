package cn.guzt.diff.util.mysql.impl;


import cn.guzt.diff.mapper.MysqlMapper;
import cn.guzt.diff.properties.Constant;
import cn.guzt.diff.properties.DbObjType;
import cn.guzt.diff.service.bean.DiffPrepareBean;
import cn.guzt.diff.util.mybatis.bean.DbProperties;
import cn.guzt.diff.util.mysql.AbstractPrepare;
import cn.guzt.diff.util.mysql.CodeUtil;
import cn.guzt.diff.util.reflect.DiffPrepareMarker;
import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * @author guzt
 */
@SuppressWarnings("unused")
@DiffPrepareMarker
public class ProcedurePrepareService extends AbstractPrepare {

    public ProcedurePrepareService() {
        super(DbObjType.PROCEDURE);
    }

    @Override
    public List<JSONObject> getObjList(MysqlMapper mapper , DbProperties db) {
        return mapper.getProcedures(db.getDbName());
    }

    @Override
    public void setRowToMap(List<JSONObject>  originalRow, Map<String, JSONObject> targetMapRow, List<String> targetRowKey) {
        CodeUtil.setRowToMap(originalRow, targetMapRow, targetRowKey, Constant.PROCEDURE_KEY_COLUMN);
    }

    @Override
    public List<JSONObject> diffCompare(DiffPrepareBean original, DiffPrepareBean target) {
        return CodeUtil.diffCompare(original,target,Constant.PROCEDURE);
    }
}
