package com.ly723.db.interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * model field annotation corresponding to table column
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
    String name() default "";

    String type() default "";

    String defaultValue() default "null";

    boolean isPrimaryKey() default false;

    boolean isNull() default true;

    boolean isUnique() default false;
}