package org.github.builders;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by julian3 on 15/03/03.
 */
@Target(value={ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface Built {

    String name() default "";
    String packageName() default "";
    String extendsClass() default  "";
    String[] implementsInterfaces() default {};

}
