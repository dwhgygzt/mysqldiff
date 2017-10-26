package cn.guzt.diff.util.mybatis.bean;

import com.alibaba.fastjson.JSONObject;
import java.io.Serializable;

/**
 * 数据库配置对象
 */
public class DbProperties implements Serializable {

    private static final long serialVersionUID = 3496678493493861606L;

    String dbId; // 配置对象唯一标识

    String host; // 主机地址

    String user; // 登录名称

    String password; // 登录口令

    String dbName; // 连接的数据库名称

    Integer port; // 端口号

    public DbProperties(String dbId, String host, String user, String password, String dbName, Integer port) {
        this.dbId = dbId;
        this.host = host;
        this.user = user;
        this.password = password;
        this.dbName = dbName;
        this.port = port;
    }

    public DbProperties(String dbId, String host, String user, String password, String dbName) {
        this.dbId = dbId;
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

    public String getDbId() {
        return dbId;
    }

    public void setDbId(String dbId) {
        this.dbId = dbId;
    }

    @Override
    public String toString() {
        return super.toString()+" "+ JSONObject.toJSONString(this);
    }
}
