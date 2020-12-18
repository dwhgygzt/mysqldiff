package cn.guzt.diff.util.reflect;


import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author guzt
 */
public class ReflectUtil {

    private static Logger logger = LogManager.getLogger(ReflectUtil.class);

    /**
     * 得到指定包名下所有的类
     *
     * @param packageUrl      包路径 采用 '/' 分割符，例如 cn/guzt/diff/
     * @param annotationClass 自定义的注解
     * @return ignore
     * @throws URISyntaxException     ignore
     * @throws ClassNotFoundException ignore
     */
    public static List<Class<?>> getClassesByAnnotation(
            String packageUrl, Class<? extends Annotation> annotationClass) throws URISyntaxException, ClassNotFoundException {
        List<Class<?>> list = new ArrayList<>();
        ClassLoader loader = ReflectUtil.class.getClassLoader();
        //通过classloader载入包路径，得到url
        URL url = loader.getResource(packageUrl);
        URI uri = url.toURI();
        //通过File获得uri下的所有文件
        File file = new File(uri);
        File[] files = file.listFiles();
        for (File f : files) {
            String fName = f.getName();
            if (!fName.endsWith(".class")) {
                continue;
            }
            fName = fName.substring(0, fName.length() - 6);
            String perfix = packageUrl.replaceAll("/", ".");
            String allName = perfix + fName;
            // 3. 通过反射加载类
            Class<?> cls = Class.forName(allName);
            if (cls.isAnnotationPresent(annotationClass)) {
                list.add(Class.forName(allName));
            }
        }
        return list;
    }

    /**
     * 根据class 实例化
     *
     * @param list ignore
     */
    public static void newInstanceClassList(List<Class<?>> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        try {
            list.forEach(item -> {
                try {
                    item.newInstance();
                    logger.debug("实例化" + item.toString());
                } catch (Exception e) {
                    logger.error(e);
                }
            });
        } catch (Exception e) {
            logger.error(e);
        }
    }
}
