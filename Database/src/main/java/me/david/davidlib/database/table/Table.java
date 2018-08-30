package me.david.davidlib.database.table;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
* This Annotation Represents an Database Table
* Or for MongoDB Collection
*/

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table {

    /*
    * The name of the Table
    * If no name is set DavidLib will use the Name of the Class
    */
    String name() default "";

    Class clazz();

}
