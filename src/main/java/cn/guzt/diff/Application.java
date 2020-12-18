package cn.guzt.diff;

import cn.guzt.diff.action.DiffAction;
import cn.guzt.diff.properties.ResultObj;
import cn.guzt.diff.util.mybatis.bean.DbProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * main
 *
 * @author guzt
 */
public class Application {

    private static Logger logger = LogManager.getLogger(Application.class);

    public static void test() {
        DiffAction diffAction = new DiffAction();
        // 本地的数据库配置信息 一般指测试库
        DbProperties originalDb = new DbProperties();
        // 数据库的唯一标识
        originalDb.setInstanceId("DEV-srm");
        // 数据库名称
        originalDb.setDbName("tiannake-srm-api-dev");
        // 数据库路径
        originalDb.setHost("rm-bp184x857665yd5yfio.mysql.rds.aliyuncs.com");
        // 端口号
        originalDb.setPort(3306);
        // 登录名
        originalDb.setUser("root");
        // 登录密码
        originalDb.setPassword("middol8801_");

        // 远程的数据库配置信息 一般指运维库
        DbProperties targetDb = new DbProperties();
        // 数据库的唯一标识
        targetDb.setInstanceId("PROD-srm");
        targetDb.setDbName("tiannake_srm_prod");
        targetDb.setHost("rm-uf6rfdb0w5t837c0q9o.mysql.rds.aliyuncs.com");
        targetDb.setPort(3306);
        targetDb.setUser("tnk_readonly");
        targetDb.setPassword("tnkSRM2020_RONY");

        // 执行比对的action方法
        // 导出的比对结果文件在   工程文件根路径/mysqldiff/export/  路径下
        ResultObj resultObj = diffAction.compareAction(originalDb, targetDb);
        logger.info("比对结果: {}", resultObj.tojsonstring());
    }


    public static void main(String[] args) {
        logger.info("执行测试方法...");
        test();
        logger.info("完成测试方法...");
    }


}
