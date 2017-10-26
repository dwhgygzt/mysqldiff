package cn.guzt.diff.util.mybatis;

import javax.sql.DataSource;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import cn.guzt.diff.util.mybatis.bean.DbProperties;

public class MySqlDataSourceFactory  {

    private static final String DRIVER = "com.mysql.jdbc.Driver";

    public static DataSource getDataSource(DbProperties dbProperties) {
        String url = "jdbc:mysql://"+ dbProperties.getHost() +":" +dbProperties.getPort() +"/"+ dbProperties.getDbName();
        String username = dbProperties.getUser();
        String password = dbProperties.getPassword();
        return new PooledDataSource(DRIVER, url, username, password);
    }

}
