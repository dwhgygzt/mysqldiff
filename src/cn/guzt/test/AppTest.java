package cn.guzt.test;

import cn.guzt.diff.action.DiffAction;
import cn.guzt.diff.properties.DbObjType;
import cn.guzt.diff.properties.ResultObj;
import cn.guzt.diff.service.DiffService;
import cn.guzt.diff.service.bean.DiffPrepareBean;
import cn.guzt.diff.util.mybatis.bean.DbProperties;
import cn.guzt.diff.util.mysql.CodeUtil;
import com.alibaba.fastjson.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.apache.log4j.Logger;

/**
 * 测试代码
 */
public class AppTest {
    private  static Logger logger = Logger.getLogger(AppTest.class);

    public static void test(){
        DiffAction diffAction = new DiffAction();
        DbProperties originalDb = new DbProperties(); // 本地的数据库配置信息 一般指测试库
        originalDb.setDbId("本地测试库"); // 数据库的唯一标识
        originalDb.setDbName("dbname"); // 数据库名称
        originalDb.setHost("your db url/ip"); // 数据库路径
        originalDb.setPort(3306); // 端口号
        originalDb.setUser("readonly"); // 登录名
        originalDb.setPassword("password"); // 登录密码

        DbProperties targetDb = new DbProperties(); // 远程的数据库配置信息 一般指运维库
        targetDb.setDbId("远程运维库"); // 数据库的唯一标识
        targetDb.setDbName("dbname");
        targetDb.setHost("your db url/ip");
        targetDb.setPort(3306);
        targetDb.setUser("readonly");
        targetDb.setPassword("password");

        // 执行比对的action方法
        // 导出的比对结果文件在   工程文件根路径/mysqldiff/export/  路径下
        diffAction.compareAction(originalDb, targetDb);
    }

    public static void main(String[] args){
        logger.info("执行测试方法...");
        test();
        logger.info("完成测试方法...");
    }

}
