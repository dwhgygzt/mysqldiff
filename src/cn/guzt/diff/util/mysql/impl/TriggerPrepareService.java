package cn.guzt.diff.util.mysql.impl;


import cn.guzt.diff.mapper.MysqlMapper;
import cn.guzt.diff.properties.Constant;
import cn.guzt.diff.properties.DbObjType;
import cn.guzt.diff.service.bean.DiffPrepareBean;
import cn.guzt.diff.util.mybatis.bean.DbProperties;
import cn.guzt.diff.util.mysql.AbstractPrepare;
import cn.guzt.diff.util.mysql.CodeUtil;
import com.alibaba.fastjson.JSONObject;
import java.util.List;
import java.util.Map;
import cn.guzt.diff.util.reflect.DiffPrepareMarker;

@DiffPrepareMarker
public class TriggerPrepareService extends AbstractPrepare {

    public TriggerPrepareService() {
        super(DbObjType.TRIGGER);
    }

    @Override
    public List<JSONObject> getObjList(MysqlMapper mapper , DbProperties db) {
        return mapper.getTirggers(db.getDbName());
    }

    @Override
    public void setRowToMap(List<JSONObject>  originalRow, Map<String, JSONObject> targetMapRow, List<String> targetRowKey) {
        CodeUtil.setRowToMap(originalRow, targetMapRow, targetRowKey, Constant.TRIGGER_KEY_COLUMN);
    }

    @Override
    public List<JSONObject> diffCompare(DiffPrepareBean original, DiffPrepareBean target) {
        return CodeUtil.diffCompare(original,target,Constant.TRIGGER);
    }
}
