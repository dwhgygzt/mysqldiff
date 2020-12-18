import cn.guzt.diff.action.DiffAction;
import cn.guzt.diff.properties.ResultObj;
import cn.guzt.diff.util.mybatis.bean.DbProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 测试代码
 *
 * @author guzt
 */
public class AppTest {
    private static Logger logger = LogManager.getLogger(AppTest.class);

    public static void test() {
        DiffAction diffAction = new DiffAction();
        // 本地的数据库配置信息 一般指测试库
        DbProperties originalDb = new DbProperties();
        // 数据库的唯一标识
        originalDb.setInstanceId("本地测试库");
        // 数据库名称
        originalDb.setDbName("dbname");
        // 数据库路径
        originalDb.setHost("your db url/ip");
        // 端口号
        originalDb.setPort(3306);
        // 登录名
        originalDb.setUser("readonly");
        // 登录密码
        originalDb.setPassword("password");

        // 远程的数据库配置信息 一般指运维库
        DbProperties targetDb = new DbProperties();
        // 数据库的唯一标识
        targetDb.setInstanceId("远程运维库");
        targetDb.setDbName("dbname");
        targetDb.setHost("your db url/ip");
        targetDb.setPort(3306);
        targetDb.setUser("readonly");
        targetDb.setPassword("password");

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
