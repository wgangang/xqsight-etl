package com.xqsight.etl.util;

import java.io.InputStreamReader;
import java.util.Properties;

/**
 * 〈一句话功能简述〉<br>
 * 〈功能详细描述〉
 *
 * @author ganggang.wang
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class PropertyUtils {

    public static final String INVALID_COMPANY = "companylist.invalid";

    public static final String JSON_FILE_DIR = "jsonFileDir";

    public static final String COMPANY_SIZE = "compamySizePer";

    public static final String SYN_METHOD = "synMethod";


    private static Properties prop = null;

    static {
        try {
            InputStreamReader in = new InputStreamReader(
                    Thread.currentThread().getContextClassLoader().getResourceAsStream("app.properties"),
                    "UTF-8");
            prop = new Properties();
            prop.load(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getValue(String key) {
        return prop.getProperty(key);
    }

    public static void main(String[] args) {
        String bufferCountStr = PropertyUtils.getValue("driver");
        System.out.println(bufferCountStr);
    }
}
