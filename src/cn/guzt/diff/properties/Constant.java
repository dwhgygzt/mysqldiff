package cn.guzt.diff.properties;

import java.util.Arrays;
import java.util.List;

public final class Constant {

    public static final String KEY_FLAG = "key";

    public static final String COMPARE_RESULT = "COMPARE_RESULT";

    public static final String COMPARE_MSG = "COMPARE_MSG";

    public static final int RESULT_OK = 1;

    public static final int RESULT_FAILE = 2;

    public static final String CODE_ORIGINAL = "CODE_ORIGINAL";

    public static final String CODE_TARGET = "CODE_TARGET";

    public static final String CODE_ROWNUM = "CODE_NUM";

    public static final String CODE_DETAIL = "CODE_DETAIL";

    // 针对数据库对象列表一行记录， 可用标识该行的唯一标识列组合
    public static final String[] TABLE_KEY_COLUMN = {"TABLE_NAME"};
    public static final String[] COLUMN_KEY_COLUMN = {"TABLE_NAME","COLUMN_NAME"};
    public static final String[] INDEX_KEY_COLUMN = {"TABLE_NAME","INDEX_NAME"};
    public static final String[] PARTITION_KEY_COLUMN = {"TABLE_NAME","PARTITION_NAME"};
    public static final String[] TRIGGER_KEY_COLUMN = {"TRIGGER_NAME"};
    public static final String[] FUNCTION_KEY_COLUMN = {"ROUTINE_NAME"};
    public static final String[] PROCEDURE_KEY_COLUMN = {"ROUTINE_NAME"};

    // 数据库对象列表对应的各字段名称 第三个字段 "key"或"" 表示是该列是否是组成上面定义的TABLE_KEY_COLUMN、COLUMN_KEY_COLUMN等里面的元素 如果是则为“key”
    public static final String[][] TABLE
        = {{"表名称","TABLE_NAME",KEY_FLAG},{"表类型","TABLE_TYPE",""}, {"字符排序规则","TABLE_COLLATION",""}, {"表名注释","TABLE_COMMENT",""}};

    public static final String[][] COLUMN
        = {{"表名称","TABLE_NAME",KEY_FLAG},{"列名称","COLUMN_NAME",KEY_FLAG},{"注释","COLUMN_COMMENT",""},
           {"默认值","COLUMN_DEFAULT",""}, {"是否可为null","IS_NULLABLE",""},{"类型","COLUMN_TYPE",""},
           {"字符集","CHARACTER_SET_NAME",""}, {"字符排序规则","COLLATION_NAME",""},{"扩展属性","EXTRA",""}};

    public static final String[][] INDEX
        = {{"表名称","TABLE_NAME",KEY_FLAG},{"索引名称","INDEX_NAME",KEY_FLAG},{"是否唯一索引类型","NON_UNIQUE",""}, {"索引涉及列","COLUMN_NAME",""}};

    public static final String[][] PARTITION
        = {{"表名称","TABLE_NAME",KEY_FLAG},{"表分区名称","PARTITION_NAME",KEY_FLAG},{"分区类型","PARTITION_METHOD",""},
           {"分区表达式","PARTITION_EXPRESSION",""}, {"分区表达式对应值","PARTITION_DESCRIPTION",""}};

    public static final String[][] TRIGGER
        = {{"触发器名称","TRIGGER_NAME",KEY_FLAG},{"触发事件","EVENT_MANIPULATION",""},{"触发对象对应数据库","EVENT_OBJECT_SCHEMA",""},
           {"触发对象","EVENT_OBJECT_TABLE",""}, {"触发频率","ACTION_ORIENTATION",""},{"ACTION_TIMING","触发时间",""},
           {"ACTION_STATEMENT","主体代码",""}};

    public static final String[][] FUNCTION
        = {{"自定义方法名称","ROUTINE_NAME",KEY_FLAG},{"自定义方法注释","ROUTINE_COMMENT",""},{"主体代码","ROUTINE_DEFINITION",""}};

    public static final String[][] PROCEDURE
        = {{"自定义存储过程名称","ROUTINE_NAME",KEY_FLAG},{"自定义存储过程注释","ROUTINE_COMMENT",""},{"主体代码","ROUTINE_DEFINITION",""}};

    public static final List<String> CODE_COLUMN = Arrays.asList("ACTION_STATEMENT", "ROUTINE_DEFINITION");
}
