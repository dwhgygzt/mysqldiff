package cn.guzt.diff.action;

import cn.guzt.diff.properties.Constant;
import cn.guzt.diff.properties.DbObjType;
import cn.guzt.diff.properties.ResultObj;
import cn.guzt.diff.service.DiffService;
import cn.guzt.diff.service.bean.DiffPrepareBean;
import cn.guzt.diff.util.export.ExportUtil;
import cn.guzt.diff.util.mybatis.bean.DbProperties;
import com.alibaba.fastjson.JSONObject;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.apache.log4j.Logger;

/**
 * 执行比对动作
 */
public class DiffAction {
    private  static Logger logger = Logger.getLogger(DiffAction.class);

    // 实例化 service层对象
    private DiffService service = DiffService.getInstance();


    /**
     * 数据库对象结构比对方法
     * @param original 一般指本地数据库
     * @param target 一般指远程数据库
     */
    public ResultObj compareAction(DbProperties original, DbProperties target){
        ResultObj resultObj = new ResultObj();
        DiffService service = DiffService.getInstance();

        // 1. 验证数据库是否可连通
        if (!service.checkDbConnect(original)){
            resultObj.setCode(Constant.RESULT_FAILE);
            resultObj.setMsg(original.getDbId()+" 数据库连接失败");
            return resultObj;
        }
        if (!service.checkDbConnect(target)){
            resultObj.setCode(Constant.RESULT_FAILE);
            resultObj.setMsg(target.getDbId()+" 数据库连接失败");
            return resultObj;
        }

        // 2. 异步获取各个数据库对象的信息
        Future<DiffPrepareBean> tableOriginal = service.initDiffPrepareBeanAsync(original, DbObjType.TABLE);
        Future<DiffPrepareBean> columnOriginal = service.initDiffPrepareBeanAsync(original,DbObjType.COLUMN);
        Future<DiffPrepareBean> indexOriginal = service.initDiffPrepareBeanAsync(original,DbObjType.INDEX);
        Future<DiffPrepareBean> partitionOriginal = service.initDiffPrepareBeanAsync(original,DbObjType.PARTITION);
        Future<DiffPrepareBean> functionOriginal = service.initDiffPrepareBeanAsync(original,DbObjType.FUNCTION);
        Future<DiffPrepareBean> procedureOriginal = service.initDiffPrepareBeanAsync(original,DbObjType.PROCEDURE);

        Future<DiffPrepareBean> tableTarget = service.initDiffPrepareBeanAsync(target, DbObjType.TABLE);
        Future<DiffPrepareBean> columnTarget = service.initDiffPrepareBeanAsync(target,DbObjType.COLUMN);
        Future<DiffPrepareBean> indexTarget = service.initDiffPrepareBeanAsync(target,DbObjType.INDEX);
        Future<DiffPrepareBean> partitionTarget = service.initDiffPrepareBeanAsync(target,DbObjType.PARTITION);
        Future<DiffPrepareBean> functionTarget = service.initDiffPrepareBeanAsync(target,DbObjType.FUNCTION);
        Future<DiffPrepareBean> procedureTarget = service.initDiffPrepareBeanAsync(target,DbObjType.PROCEDURE);

        try {
            // 3. 取得上一步运行后的数据库对象的信息
            DiffPrepareBean p_tableOriginal = tableOriginal.get();
            DiffPrepareBean p_columnOriginal = columnOriginal.get();
            DiffPrepareBean P_indexOriginal = indexOriginal.get();
            DiffPrepareBean p_partitionOriginal = partitionOriginal.get();
            DiffPrepareBean p_functionOriginal = functionOriginal.get();
            DiffPrepareBean p_procedureOriginal = procedureOriginal.get();

            DiffPrepareBean p_tableTarget = tableTarget.get();
            DiffPrepareBean p_columnTarget = columnTarget.get();
            DiffPrepareBean P_indexTarget = indexTarget.get();
            DiffPrepareBean p_partitionTarget = partitionTarget.get();
            DiffPrepareBean p_functionTarget = functionTarget.get();
            DiffPrepareBean p_procedureTarget = procedureTarget.get();

            // 4. 进行比对操作
            List<JSONObject> tableDiff = service.getCompareResult(p_tableOriginal, p_tableTarget, DbObjType.TABLE);
            List<JSONObject> columnDiff = service.getCompareResult(p_columnOriginal, p_columnTarget, DbObjType.COLUMN);
            List<JSONObject> indexDiff = service.getCompareResult(P_indexOriginal, P_indexTarget, DbObjType.INDEX);
            List<JSONObject> partitionDiff = service.getCompareResult(p_partitionOriginal, p_partitionTarget, DbObjType.PARTITION);
            List<JSONObject> functionDiff = service.getCompareResult(p_functionOriginal, p_functionTarget, DbObjType.FUNCTION);
            List<JSONObject> procedureDiff = service.getCompareResult(p_procedureOriginal, p_procedureTarget, DbObjType.PROCEDURE);


            // 5. 导出比对结果文件
            ExportUtil.exportDiffFile(original,target,tableDiff,columnDiff,indexDiff,partitionDiff,functionDiff,procedureDiff);

        }
        catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
        }

        return resultObj;
    }

}
