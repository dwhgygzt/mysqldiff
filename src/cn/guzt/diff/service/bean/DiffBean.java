package cn.guzt.diff.service.bean;

import java.io.Serializable;
import java.util.List;

public class DiffBean implements Serializable {

    private static final long serialVersionUID = 3496678493493861106L;

    // original数据库对象在target数据库中不存在的对象列表
    List<?> originalNotExistRow;

    // target数据库对象在original数据库中不存在的对象列表
    List<?> targetNotExistRow;

    // original、target数据库都存在的对象列表行key集合
    List<?> sameKeyList;

    public List<?> getOriginalNotExistRow() {
        return originalNotExistRow;
    }

    public void setOriginalNotExistRow(List<?> originalNotExistRow) {
        this.originalNotExistRow = originalNotExistRow;
    }

    public List<?> getTargetNotExistRow() {
        return targetNotExistRow;
    }

    public void setTargetNotExistRow(List<?> targetNotExistRow) {
        this.targetNotExistRow = targetNotExistRow;
    }

    public List<?> getSameKeyList() {
        return sameKeyList;
    }

    public void setSameKeyList(List<?> sameKeyList) {
        this.sameKeyList = sameKeyList;
    }
}
