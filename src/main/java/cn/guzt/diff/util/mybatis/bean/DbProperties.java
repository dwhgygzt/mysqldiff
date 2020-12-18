package cn.guzt.diff.util.mybatis.bean;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * 数据库配置对象
 * @author guzt
 */
public class DbProperties implements Serializable {

    private static final long serialVersionUID = 3496678493493861606L;

    /**
     * JDBC连接标识【自定义唯一的ID】
     */
    String instanceId;

    /**
     * 主机地址
     */
    String host;

    /**
     * 登录名称
     */
    String user;

    /**
     * 登录口令
     */
    String password;

    /**
     * 连接的数据库名称
     */
    String dbName;

    /**
     * 端口号
     */
    Integer port;

    public DbProperties(String instanceId, String host, String user, String password, String dbName, Integer port) {
        this.instanceId = instanceId;
        this.host = host;
        this.user = user;
        this.password = password;
        this.dbName = dbName;
        this.port = port;
    }

    public DbProperties(String instanceId, String host, String user, String password, String dbName) {
        this.instanceId = instanceId;
        this.host = host;
        this.user = user;
        this.password = password;
        this.dbName = dbName;
        this.port = 3306;
    }

    public DbProperties() {

    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    @Override
    public String toString() {
        return super.toString() + " " + JSONObject.toJSONString(this);
    }
}
