import cn.guzt.test.AppTest;

public class Main {

    public static void main(String[] args) {

        // 请在test方法中配置你的jdbc信息
        // 执行比对测试
        // 导出的比对结果文件在   工程文件根路径/mysqldiff/export/  路径下
        AppTest.test();

    }
}
