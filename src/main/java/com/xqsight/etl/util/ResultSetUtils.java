package com.xqsight.etl.util;

import java.lang.reflect.Field;
import java.sql.ResultSet;

/**
 * 〈一句话功能简述〉<br>
 * 〈功能详细描述〉
 *
 * @author ganggang.wang
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class ResultSetUtils {

    public static <T> T getObject(ResultSet resultSet, Class<T> clazz){
        T target = null;
        try {
            target = clazz.newInstance();
            Field[] fields = clazz.getDeclaredFields();
            for(Field field:fields){
                String name = field.getName();
                String type = field.getType().getName();
                if(type.equals("boolean")){
                    Object object = resultSet.getObject(name);
                    field.setAccessible(true);
                    if(object == null)
                        field.set(target, false);
                    else
                        field.set(target, object);
                }else{
                Object object = resultSet.getObject(name);
                field.setAccessible(true);
                field.set(target, object);
                }
            }
            return target;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
