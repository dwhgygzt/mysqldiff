package cn.guzt.diff.service;

import cn.guzt.diff.mapper.MysqlMapper;
import cn.guzt.diff.properties.DbObjType;
import cn.guzt.diff.service.bean.DiffPrepareBean;
import cn.guzt.diff.util.mybatis.MySqlSessionFactory;
import cn.guzt.diff.util.mybatis.bean.DbProperties;
import cn.guzt.diff.util.mysql.AbstractPrepare;
import cn.guzt.diff.util.reflect.DiffPrepareMarker;
import cn.guzt.diff.util.reflect.ReflectUtil;
import com.alibaba.fastjson.JSONObject;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

/**
 * 比对的具体实现
 */
public class DiffService {
    private  static Logger logger = Logger.getLogger(DiffService.class);
    private DiffService(){}
    private volatile static DiffService instance = null;

    /**
     * 创建一个线程池，用于处理要进行比对的数据库对象查询操作。
     */
    private final Executor executor = Executors.newFixedThreadPool(20, new ThreadFactory() {
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        }
    });

    /**
     * 单实例模式
     * @return
     */
    public static DiffService getInstance() {
        // 先检查实例是否存在，如果不存在才进入下面的同步块
        if (instance == null) {
            // 同步块，线程安全地创建实例
            synchronized (DiffService.class) {
                // 再次检查实例是否存在，如果不存在则创建实例，从而确保之后进入同步块的线程不会再创建实例
                if (instance == null) {
                    instance = new DiffService();
                }
                // 一些初始化操作
                try {
                    ReflectUtil.newInstanceClassList(ReflectUtil.getClassesByAnnotation("cn/guzt/diff/util/mysql/impl/", DiffPrepareMarker.class));
                }
                catch (Exception e) {
                    logger.error(e);
                }
            }
        }
        return instance;
    }

    /**
     * 检查数据库是否可以连接
     * @param db
     * @return
     */
    public boolean checkDbConnect(DbProperties db){
        boolean isOk = true;
        SqlSession session = null;
        try {
            session = MySqlSessionFactory.openSession(db);
            session.getMapper(MysqlMapper.class).checkDbConnect();
        }catch (Exception e){
            logger.error(db.getDbId()+"连接信息异常",e);
            isOk = false;
        }finally {
            // 关闭数据库连接
            if (session != null){
                session.close();
            }
        }
        return isOk;
    }

    /**
     * 根据数据库配置查询指定的数据库对象类型信息列表
     * @param db 数据库配置信息
     * @param dbObjType 数据库对象类型
     * @return
     */
    public List<JSONObject> getDbObjectList(DbProperties db, DbObjType dbObjType){
        List<JSONObject> list = null;
        // 获得一个数据库连接
        SqlSession session = null;
        try {
            session = MySqlSessionFactory.openSession(db);
            if (session == null){
                logger.error("未获得数据库连接，dbInfo="+db.toString());
                return list;
            }
            logger.debug("获得一个数据库连接"+session.toString());
            list = AbstractPrepare.getPrepareService(dbObjType).getObjList( session.getMapper(MysqlMapper.class) , db );
        } catch (Exception e){
            logger.error("获取数据库对象列表异常，dbInfo="+db.toString(),e);
        }finally {
            // 关闭数据库连接
            if (session != null){
                session.close();
                logger.debug("关闭一个数据库连接"+session.toString());
            }
        }
        return list;
    }

    /**
     * 将原来查询出的数据库对象列表转换一个MAP，用于快速比对操作
     * @param originalRow
     * @param targetMapRow
     * @param targetRowKey
     */
    public void setRowToMap(List<JSONObject> originalRow, Map<String, JSONObject> targetMapRow, List<String> targetRowKey, DbObjType dbObjType) {
        AbstractPrepare.getPrepareService(dbObjType).setRowToMap(originalRow,targetMapRow,targetRowKey);
    }

    /**
     * 异步执行 创建用于比对的对象
     * @param db
     * @param dbObjType
     * @return
     */
    public Future<DiffPrepareBean> initDiffPrepareBeanAsync (DbProperties db, DbObjType dbObjType){
        return CompletableFuture.supplyAsync(() -> initDiffPrepareBean(db,dbObjType), executor);
    }

    /**
     * 同步执行 创建用于比对的对象
     * @param db
     * @param dbObjType
     * @return
     */
    public DiffPrepareBean initDiffPrepareBean (DbProperties db, DbObjType dbObjType){
        DiffPrepareBean diffPrepareBean = new DiffPrepareBean();
        Map<String, JSONObject> targetMapRow = new HashMap<>();
        List<String> targetRowKey = new ArrayList<>();

        List<JSONObject> originalRow = getDbObjectList(db,dbObjType);
        if (originalRow == null){
            originalRow = new ArrayList<>();
        }
        this.setRowToMap(originalRow,targetMapRow,targetRowKey,dbObjType);

        diffPrepareBean.setOriginalRow(originalRow);
        diffPrepareBean.setTargetMapRow(targetMapRow);
        diffPrepareBean.setTargetRowKey(targetRowKey);
        diffPrepareBean.setDb(db);
        return diffPrepareBean;
    }

    /**
     * 获取某一数据库对象的比对结果.
     * @param original
     * @param target
     * @param dbObjType
     * @return
     */
    public List<JSONObject> getCompareResult(DiffPrepareBean original, DiffPrepareBean target, DbObjType dbObjType){
        return  AbstractPrepare.getPrepareService(dbObjType).diffCompare(original,target);
    }

}
