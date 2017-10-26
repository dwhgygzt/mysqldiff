package cn.guzt.diff.util.mybatis;


import cn.guzt.diff.util.mybatis.bean.DbProperties;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

public class MySqlSessionFactory {

    private static final ConcurrentHashMap<String, SqlSessionFactory> SSF_MAP = new ConcurrentHashMap<>();

    /**
     * 根据mysql数据库配置信息仅仅初始化一下SqlSessionFactory
     * @param dbProperties
     */
    public void initMySqlSessionFactory(DbProperties dbProperties){
        getSqlSessionFactory(dbProperties);
    }

    /**
     * 根据mysql数据库配置信息获得一个 SqlSessionFactory.
     *
     * @return
     */
    public static SqlSessionFactory getSqlSessionFactory(DbProperties dbProperties) {
        SqlSessionFactory sqlSessionFactory = SSF_MAP.get(dbProperties.getDbId());
        if (sqlSessionFactory == null) {
            synchronized (MySqlSessionFactory.class) {
                sqlSessionFactory = SSF_MAP.get(dbProperties.getDbId());
                if (sqlSessionFactory == null) {
                    try {
                        Environment environment = new Environment("development",
                                                                  new JdbcTransactionFactory(),
                                                                  MySqlDataSourceFactory.getDataSource(dbProperties));
                        Configuration configuration = new Configuration(environment);
                        configuration.addMappers("cn.guzt.diff.mapper");
                        sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
                        SSF_MAP.put(dbProperties.getDbId(), sqlSessionFactory);
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        return sqlSessionFactory;
    }

    /**
     * 获得一个数据库的链接.
     * @param dbProperties
     * @return
     */
    public static SqlSession openSession(DbProperties dbProperties){
        return MySqlSessionFactory.getSqlSessionFactory(dbProperties).openSession();
    }

    /**
     * 将所有的SqlSessionFactory清空.
     */
    public static void destorySessionFactoryAll() {
        SSF_MAP.clear();
    }

    /**
     * 根据数据库配置ID清除初始化的单实例 SqlSessionFactory.
     */
    public static void destorySessionFactory(String dbId) {
        SSF_MAP.remove(dbId);
    }

}
