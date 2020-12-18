package cn.guzt.diff.util.mysql;

import cn.guzt.diff.properties.Constant;
import cn.guzt.diff.properties.ResultObj;
import cn.guzt.diff.service.bean.DiffBean;
import cn.guzt.diff.service.bean.DiffPrepareBean;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.compress.utils.Sets;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toList;

/**
 * @author guzt
 */
public class CodeUtil {

    private static Set<String> itemLimit = Sets.newHashSet("\n", "\r", "\r\n", " ");

    /**
     * 存储过程或函数过滤掉注释和空行
     *
     * @param routinesCode 返回简洁紧凑的代码
     * @return ignore
     */
    public static List<JSONObject> filterNotes(String routinesCode) {
        List<JSONObject> resultCode = new ArrayList<>();
        if (StringUtils.isEmpty(routinesCode)) {
            return resultCode;
        }
        String[] itemList = routinesCode.replaceAll("\r", "").replaceAll("\t", "").split("\n");
        String item;
        for (int i = 0; i < itemList.length; i++) {
            item = itemList[i];
            if (!StringUtils.isEmpty(item)) {
                item = item.trim();
            }
            if (StringUtils.isEmpty(item)
                    || itemLimit.contains(item)
                    || "#".equals(item.substring(0, 1))
                    // -- 类似于 tryCatch 捕获异常
                    || (item.length() >= 2 && "--".equals(item.substring(0, 2)))) {
                // 剔除注释信息和空行
                continue;
            }

            JSONObject filteredCode = new JSONObject();
            filteredCode.put(Constant.CODE_ROWNUM, i + 1);
            filteredCode.put(Constant.CODE_DETAIL, item);
            resultCode.add(filteredCode);
        }
        return resultCode;
    }

    /**
     * 比对存储过程或函数代码差异的行数
     *
     * @return ignore
     */
    public static ResultObj compareDifferenceRow(List<JSONObject> original, List<JSONObject> target) {
        ResultObj resultObj = new ResultObj();
        StringBuilder msg = new StringBuilder();
        msg.append("第 ");
        int compareRowCnt;
        int differenceRowSum = 0;
        if (original == null || target == null || original.isEmpty() || target.isEmpty()) {
            resultObj.setCode(Constant.RESULT_FAILE);
            resultObj.setMsg("参与比对的代码块为空");
            return resultObj;
        }
        compareRowCnt = minNum(original.size(), target.size());

        if (original.size() != target.size()) { // 代码行数不一致的情况
            for (int i = 0; i < compareRowCnt; i++) {
                if (!original.get(i).getString(Constant.CODE_DETAIL).equals(target.get(i).getString(Constant.CODE_DETAIL))) {
                    differenceRowSum++;
                    msg.append("original库的").append(original.get(i).getString(Constant.CODE_ROWNUM)).append("行对比target库的第").append(target.get(i).getString(Constant.CODE_ROWNUM)).append("行不一致; ");
                    break;
                }
            }
            String remarkRowNum = "";
            if (differenceRowSum > 0) {
                remarkRowNum = msg.toString();
            }
            resultObj.setCode(Constant.RESULT_FAILE);
            resultObj.setMsg("剔除注释行和空字符行后发现有用的代码行数不一致; " + remarkRowNum);
            return resultObj;
        }

        for (int i = 0; i < compareRowCnt; i++) { // 代码行数一致的情况
            if (!original.get(i).getString(Constant.CODE_DETAIL).equals(target.get(i).getString(Constant.CODE_DETAIL))) {
                differenceRowSum++;
                msg.append(original.get(i).getString(Constant.CODE_ROWNUM)).append(" ");
            }
        }

        if (differenceRowSum > 0) {
            resultObj.setCode(Constant.RESULT_FAILE);
            msg.append("行代码有差异");
            msg.append("，共有").append(differenceRowSum).append("行差异");
            resultObj.setMsg(msg.toString());
        } else {
            resultObj.setCode(Constant.RESULT_OK);
            resultObj.setMsg("无差异");
        }
        return resultObj;
    }

    /**
     * 将原来查询出的数据库对象列表转换一个MAP，用于快速比对操作
     *
     * @param originalRow  ignore
     * @param targetMapRow ignore
     * @param targetRowKey ignore
     * @param keyColumns   指定数据库对象列表中一行可作为该行标识的 字段集，
     * @see Constant
     */
    public static void setRowToMap(List<JSONObject> originalRow, Map<String, JSONObject> targetMapRow, List<String> targetRowKey, String[] keyColumns) {
        // 这里不可采用并行流 parallelStream，Map，List 线程不安全
        originalRow.forEach(item -> {
            StringBuilder key = new StringBuilder();
            for (String keyColumn : keyColumns) {
                key.append(item.getString(keyColumn)).append("&");
            }
            targetMapRow.put(key.toString(), item);
            targetRowKey.add(key.toString());
        });
    }

    /**
     * 获取两个数据库对象基本差异情况
     *
     * @param original ignore
     * @param target   ignore
     * @return ignore
     */
    public static DiffBean getDiffBean(DiffPrepareBean original, DiffPrepareBean target) {
        DiffBean diffBean = new DiffBean();
        Map<String, JSONObject> originalRow = original.getTargetMapRow();
        Map<String, JSONObject> targetRow = target.getTargetMapRow();
        List<String> originalKeyList = original.getTargetRowKey();
        List<String> targetKeyList = target.getTargetRowKey();

        List<String> originalReduceKeyList = originalKeyList.stream().filter(item -> !targetKeyList.contains(item)).collect(toList());
        List<String> targetReduceKeyList = targetKeyList.stream().filter(item -> !originalKeyList.contains(item)).collect(toList());
        // original数据库对象在target数据库中不存在的对象列表
        List<JSONObject> targetNotExistRow = originalReduceKeyList.parallelStream().map(originalRow::get).collect(toList());

        // target数据库对象在original数据库中不存在的对象列表
        List<JSONObject> originalNotExistRow = targetReduceKeyList.parallelStream().map(targetRow::get).collect(toList());

        // original、target数据库都存在的对象列表行key集合
        List<String> sameKeyList = originalKeyList.parallelStream().filter(targetKeyList::contains).collect(toList());
        //List<String> sameKeyList2 = targetKeyList.stream().filter(item -> originalKeyList.contains(item)).collect(toList());

        diffBean.setOriginalNotExistRow(listNotNull(originalNotExistRow));
        diffBean.setTargetNotExistRow(listNotNull(targetNotExistRow));
        diffBean.setSameKeyList(listNotNull(sameKeyList));
        return diffBean;
    }

    /**
     * 数据库对象进行比对操作
     *
     * @param original ignore
     * @param target   ignore
     * @param columns  比对的字段
     * @return ig
     */
    @SuppressWarnings("unchecked")
    public static List<JSONObject> diffCompare(DiffPrepareBean original, DiffPrepareBean target, String[][] columns) {
        List<JSONObject> result = new ArrayList<>();
        DiffBean diffBean = CodeUtil.getDiffBean(original, target);
        Map<String, JSONObject> originalRow = original.getTargetMapRow();
        Map<String, JSONObject> targetRow = target.getTargetMapRow();
        // original数据库对象在target数据库中不存在的对象列表
        List<JSONObject> originalNotExistRow = (List<JSONObject>) diffBean.getOriginalNotExistRow();
        originalNotExistRow.parallelStream().forEach(item -> {
            item.put(Constant.COMPARE_RESULT, Constant.RESULT_FAILE);
            item.put(Constant.COMPARE_MSG, "该对象在【" + original.getDb().getInstanceId() + "】库中不存在");
        });

        // target数据库对象在original数据库中不存在的对象列表
        List<JSONObject> targetNotExistRow = (List<JSONObject>) diffBean.getTargetNotExistRow();
        targetNotExistRow.parallelStream().forEach(item -> {
            item.put(Constant.COMPARE_RESULT, Constant.RESULT_FAILE);
            item.put(Constant.COMPARE_MSG, "该对象在【" + target.getDb().getInstanceId() + "】库中不存在");
        });

        // original、target数据库都存在的对象列表行key集合
        List<String> sameKeyList = (List<String>) diffBean.getSameKeyList();

        result.addAll(originalNotExistRow);
        result.addAll(targetNotExistRow);

        for (String key : sameKeyList) {
            JSONObject originalRowItem = originalRow.get(key);
            JSONObject targetRowItem = targetRow.get(key);
            JSONObject resultJson = new JSONObject();
            boolean isDiff = false;
            for (String[] column : columns) {
                String jsonKey = column[1];
                // 非空处理
                originalRowItem.put(jsonKey, strNotNull(originalRowItem.getString(jsonKey), " "));
                targetRowItem.put(jsonKey, strNotNull(targetRowItem.getString(jsonKey), " "));

                if (Constant.KEY_FLAG.equals(column[2])) { // 作为标识字段展示，不用比对，此字段一定相同
                    resultJson.put(jsonKey, originalRowItem.getString(jsonKey));
                } else { // 需要进行比对的字段
                    // 判断字段的内容是否为代码
                    if (!Constant.CODE_COLUMN.contains(jsonKey)) { // 非代码内容，只需简单比较
                        if (originalRowItem.getString(jsonKey).equals(targetRowItem.getString(jsonKey))) {
                            resultJson.put(jsonKey, " ");
                        } else {
                            isDiff = true;
                            resultJson.put(jsonKey, "有差异; 【" + original.getDb().getInstanceId() + "】"
                                    + originalRowItem.getString(jsonKey)
                                    + "; 【" + target.getDb().getInstanceId() + "】"
                                    + targetRowItem.getString(jsonKey));
                        }
                    } else { // 是代码内容，需要进行代码比对
                        List<JSONObject> oCodeDetail = CodeUtil.filterNotes(originalRowItem.getString(jsonKey));
                        List<JSONObject> tCodeDetail = CodeUtil.filterNotes(targetRowItem.getString(jsonKey));
                        ResultObj resultObj = CodeUtil.compareDifferenceRow(oCodeDetail, tCodeDetail);
                        if (resultObj.getCode() == Constant.RESULT_OK) {
                            resultJson.put(jsonKey, " ");
                            resultJson.put(Constant.CODE_ORIGINAL, " ");
                            resultJson.put(Constant.CODE_TARGET, " ");
                        } else {
                            isDiff = true;
                            resultJson.put(Constant.CODE_ORIGINAL, codeJsonToString(oCodeDetail));
                            resultJson.put(Constant.CODE_TARGET, codeJsonToString(tCodeDetail));
                            resultJson.put(jsonKey, resultObj.getMsg());
                            // System.out.println(resultJson.tojsonstring());
                        }
                    }
                }

                if (isDiff) {
                    resultJson.put(Constant.COMPARE_RESULT, Constant.RESULT_FAILE);
                    resultJson.put(Constant.COMPARE_MSG, "该对象比对存在差异");
                } else {
                    resultJson.put(Constant.COMPARE_RESULT, Constant.RESULT_OK);
                    resultJson.put(Constant.COMPARE_MSG, "比对结果一致");
                }
            }

            result.add(resultJson);
        }

        return result.stream().filter(item -> item.getIntValue(Constant.COMPARE_RESULT) == Constant.RESULT_FAILE).collect(toList());
    }

    /**
     * 返回两个数中最小值
     *
     * @param originalNum ignore
     * @param targetNum   ignore
     * @return ignore
     */
    public static int minNum(int originalNum, int targetNum) {
        return Math.min(originalNum, targetNum);
    }

    public static List<?> listNotNull(List<?> list) {
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    public static String strNotNull(String msg, String fix) {
        if (msg == null) {
            msg = fix;
        }
        return msg;
    }

    /**
     * 代码 json格式(由方法filterNotes转换的结果)转换为str
     *
     * @param codeDetail ignore
     * @return ignore
     */
    public static String codeJsonToString(List<JSONObject> codeDetail) {
        StringBuilder codeStr = new StringBuilder();
        for (JSONObject item : codeDetail) {
            codeStr.append("[").append(item.getString(Constant.CODE_ROWNUM)).append("]").append(item.getString(Constant.CODE_DETAIL)).append("\r\n");
        }
        return codeStr.toString();
    }

}
