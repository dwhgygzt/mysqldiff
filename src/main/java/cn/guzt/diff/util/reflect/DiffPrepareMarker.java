package cn.guzt.diff.util.reflect;

import java.lang.annotation.*;

/**
 * @author guzt
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface DiffPrepareMarker {

}