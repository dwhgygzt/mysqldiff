package cn.guzt.diff.util.mybatis;


import cn.guzt.diff.util.mybatis.bean.DbProperties;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author guzt
 */
public class MySqlSessionFactory {

    private static final ConcurrentHashMap<String, SqlSessionFactory> SSF_MAP = new ConcurrentHashMap<>();

    /**
     * 根据mysql数据库配置信息获得一个 SqlSessionFactory.
     *
     * @return ignore
     */
    public static SqlSessionFactory getSqlSessionFactory(DbProperties dbProperties) {
        SqlSessionFactory sqlSessionFactory = SSF_MAP.get(dbProperties.getInstanceId());
        if (sqlSessionFactory == null) {
            synchronized (MySqlSessionFactory.class) {
                sqlSessionFactory = SSF_MAP.get(dbProperties.getInstanceId());
                if (sqlSessionFactory == null) {
                    try {
                        Environment environment = new Environment("development",
                                new JdbcTransactionFactory(),
                                MySqlDataSourceFactory.getDataSource(dbProperties));
                        Configuration configuration = new Configuration(environment);
                        configuration.addMappers("cn.guzt.diff.mapper");
                        sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
                        SSF_MAP.put(dbProperties.getInstanceId(), sqlSessionFactory);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        return sqlSessionFactory;
    }

    /**
     * 获得一个数据库的链接.
     *
     * @param dbProperties ignore
     * @return ignore
     */
    public static SqlSession openSession(DbProperties dbProperties) {
        return MySqlSessionFactory.getSqlSessionFactory(dbProperties).openSession();
    }

}
