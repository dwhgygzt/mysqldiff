package cn.guzt.diff.util.mybatis;

import cn.guzt.diff.util.mybatis.bean.DbProperties;
import org.apache.ibatis.datasource.pooled.PooledDataSource;

import javax.sql.DataSource;

/**
 * @author guzt
 */
public class MySqlDataSourceFactory {

    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    public static DataSource getDataSource(DbProperties dbProperties) {
        String url = "jdbc:mysql://" + dbProperties.getHost() + ":" + dbProperties.getPort() + "/" + dbProperties.getDbName()
                + "?useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2B8&useSSL=false&nullCatalogMeansCurrent=true&rewriteBatchedStatements=true";
        String username = dbProperties.getUser();
        String password = dbProperties.getPassword();
        return new PooledDataSource(DRIVER, url, username, password);
    }

}
