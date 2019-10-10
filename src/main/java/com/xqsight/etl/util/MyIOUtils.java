package com.xqsight.etl.util;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.Charset;

/**
 * 〈一句话功能简述〉<br>
 * 〈功能详细描述〉
 *
 * @author ganggang.wang
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class MyIOUtils {

    private final static String DEFAULT_CHARSER = "UTF-8";

    public static void write(File file, String info, String charset) {
        BufferedWriter writer = null;
        try {
            if (StringUtils.isBlank(charset)) {
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), Charset.forName(DEFAULT_CHARSER)));
            } else {
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), Charset.forName(charset)));
            }
            writer.write(info);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(writer);
        }
    }

    public static String read(File file, String charset) {
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            if (StringUtils.isBlank(charset)) {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName(DEFAULT_CHARSER)));
            } else {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName(charset)));
            }
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            close(reader);
        }
    }

    public static boolean rename(File oldFile, File newFile) {
        return oldFile.renameTo(newFile);
    }

    public static void close(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
