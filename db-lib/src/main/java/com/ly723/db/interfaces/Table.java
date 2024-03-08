package com.ly723.db.interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * model annotation corresponding to table
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table {

    String name() default "";

    /**
     * table version, if this value diff from the previous one, it will update this table
     */
    int version() default 1;

}
