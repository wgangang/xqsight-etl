package com.xqsight.etl.util;



import java.io.*;

/**
 * 〈一句话功能简述〉<br>
 * 〈功能详细描述〉
 *
 * @author ganggang.wang
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class CloneUtil {

    public static <T> T deepClone(T t) {
        ByteArrayOutputStream ot = null;
        ObjectOutputStream oo = null;
        ByteArrayInputStream it = null;
        ObjectInputStream io = null;
        try {
            ot = new ByteArrayOutputStream();
            oo = new ObjectOutputStream(ot);
            oo.writeObject(t);
            byte[] bytes = ot.toByteArray();

            it = new ByteArrayInputStream(bytes);
            io = new ObjectInputStream(it);
            return (T)io.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            try {
                ot.close();
                oo.close();
                it.close();
                io.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
