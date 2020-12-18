package cn.guzt.diff.action;

import cn.guzt.diff.properties.Constant;
import cn.guzt.diff.properties.DbObjType;
import cn.guzt.diff.properties.ResultObj;
import cn.guzt.diff.service.DiffService;
import cn.guzt.diff.service.bean.DiffPrepareBean;
import cn.guzt.diff.util.export.ExportUtil;
import cn.guzt.diff.util.mybatis.bean.DbProperties;
import com.alibaba.fastjson.JSONObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.List;
import java.util.concurrent.Future;

/**
 * 执行比对动作
 */
public class DiffAction {
    private static Logger logger = LogManager.getLogger(DiffAction.class);

    /**
     * 实例化 service层对象
     */
    private DiffService service = DiffService.getInstance();


    /**
     * 数据库对象结构比对方法
     *
     * @param original 一般指本地数据库
     * @param target   一般指远程数据库
     */
    public ResultObj compareAction(DbProperties original, DbProperties target) {
        ResultObj resultObj = new ResultObj();
        if (service == null){
            service = DiffService.getInstance();
        }
        
        // 1. 验证数据库是否可连通
        if (!service.checkDbConnect(original)) {
            resultObj.setCode(Constant.RESULT_FAILE);
            resultObj.setMsg(original.getInstanceId() + " 数据库连接失败");
            return resultObj;
        }
        if (!service.checkDbConnect(target)) {
            resultObj.setCode(Constant.RESULT_FAILE);
            resultObj.setMsg(target.getInstanceId() + " 数据库连接失败");
            return resultObj;
        }

        // 2. 异步获取各个数据库对象的信息
        Future<DiffPrepareBean> tableOriginal = service.initDiffPrepareBeanAsync(original, DbObjType.TABLE);
        Future<DiffPrepareBean> columnOriginal = service.initDiffPrepareBeanAsync(original, DbObjType.COLUMN);
        Future<DiffPrepareBean> indexOriginal = service.initDiffPrepareBeanAsync(original, DbObjType.INDEX);
        Future<DiffPrepareBean> partitionOriginal = service.initDiffPrepareBeanAsync(original, DbObjType.PARTITION);
        Future<DiffPrepareBean> functionOriginal = service.initDiffPrepareBeanAsync(original, DbObjType.FUNCTION);
        Future<DiffPrepareBean> procedureOriginal = service.initDiffPrepareBeanAsync(original, DbObjType.PROCEDURE);

        Future<DiffPrepareBean> tableTarget = service.initDiffPrepareBeanAsync(target, DbObjType.TABLE);
        Future<DiffPrepareBean> columnTarget = service.initDiffPrepareBeanAsync(target, DbObjType.COLUMN);
        Future<DiffPrepareBean> indexTarget = service.initDiffPrepareBeanAsync(target, DbObjType.INDEX);
        Future<DiffPrepareBean> partitionTarget = service.initDiffPrepareBeanAsync(target, DbObjType.PARTITION);
        Future<DiffPrepareBean> functionTarget = service.initDiffPrepareBeanAsync(target, DbObjType.FUNCTION);
        Future<DiffPrepareBean> procedureTarget = service.initDiffPrepareBeanAsync(target, DbObjType.PROCEDURE);

        try {
            // 3. 取得上一步运行后的数据库对象的信息
            DiffPrepareBean ptableOriginal = tableOriginal.get();
            DiffPrepareBean pcolumnOriginal = columnOriginal.get();
            DiffPrepareBean pindexOriginal = indexOriginal.get();
            DiffPrepareBean ppartitionOriginal = partitionOriginal.get();
            DiffPrepareBean pfunctionOriginal = functionOriginal.get();
            DiffPrepareBean pprocedureOriginal = procedureOriginal.get();

            DiffPrepareBean ptableTarget = tableTarget.get();
            DiffPrepareBean pcolumnTarget = columnTarget.get();
            DiffPrepareBean pindexTarget = indexTarget.get();
            DiffPrepareBean ppartitionTarget = partitionTarget.get();
            DiffPrepareBean pfunctionTarget = functionTarget.get();
            DiffPrepareBean pprocedureTarget = procedureTarget.get();

            // 4. 进行比对操作
            List<JSONObject> tableDiff = service.getCompareResult(ptableOriginal, ptableTarget, DbObjType.TABLE);
            List<JSONObject> columnDiff = service.getCompareResult(pcolumnOriginal, pcolumnTarget, DbObjType.COLUMN);
            List<JSONObject> indexDiff = service.getCompareResult(pindexOriginal, pindexTarget, DbObjType.INDEX);
            List<JSONObject> partitionDiff = service.getCompareResult(ppartitionOriginal, ppartitionTarget, DbObjType.PARTITION);
            List<JSONObject> functionDiff = service.getCompareResult(pfunctionOriginal, pfunctionTarget, DbObjType.FUNCTION);
            List<JSONObject> procedureDiff = service.getCompareResult(pprocedureOriginal, pprocedureTarget, DbObjType.PROCEDURE);


            // 5. 导出比对结果文件
            ExportUtil.exportDiffFile(original, target, tableDiff, columnDiff, indexDiff, partitionDiff, functionDiff, procedureDiff);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
        }

        return resultObj;
    }

}
