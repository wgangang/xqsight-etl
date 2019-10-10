package com.xqsight.etl.util;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;

import java.io.IOException;

/**
 * 〈一句话功能简述〉<br>
 * 〈功能详细描述〉
 *
 * @author ganggang.wang
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class ScpUtils {

    private static int PORT = 22;
//    private static String USER = "root";
//    private static String PASSWORD = "123ewqasd^%$";

    private static String USER = "xyl123";//登录用户名
    private static String PASSWORD = "P@ssword1";//生成私钥的密码和登录密码，这两个共用这个密码

    private static Connection getConnection(String ip){
        return new Connection(ip, PORT);
    }

    /**
     * ssh用户登录验证，使用用户名和密码来认证
     *
     * @param user
     * @param password
     * @return
     */
    private static boolean isAuthedWithPassword(String user, String password, Connection connection) {
        try {
            return connection.authenticateWithPassword(user, password);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean isAuth(Connection connection) {
        return isAuthedWithPassword(USER, PASSWORD, connection);
    }

    public static void putFile(String localFile, String remoteTargetDirectory, String ip) {
        Connection connection = getConnection(ip);
        try {
            connection.connect();
            boolean isAuthed = isAuth(connection);
            if (isAuthed) {
                SCPClient scpClient = connection.createSCPClient();
                scpClient.put(localFile, remoteTargetDirectory);
            } else {
                System.out.println("认证失败!");
            }
        } catch (Exception ex) {
            System.out.println(String.format("scp to %s failed", ip));
            ex.printStackTrace();
        } finally {
            connection.close();
        }
    }

    public static void main(String[] args) {
        try {
            putFile("E:\\work\\hive-数据导入.sql", "./", "192.168.20.5");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
