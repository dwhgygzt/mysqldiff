package cn.guzt.diff.properties;

import com.alibaba.fastjson.JSONObject;

public final class ResultObj{

    private int code;

    private String msg;

    private JSONObject data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    public String toJSONString() {
        return JSONObject.toJSONString(this);
    }
}
