package com.xqsight.etl.util;

import com.xqsight.etl.domain.EtlAllCompany;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * 〈一句话功能简述〉<br>
 * 〈功能详细描述〉
 *
 * @author ganggang.wang
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class JdbcUtil {

    public static Logger log = LoggerFactory.getLogger(JdbcUtil.class);

    public static Set<String> listErrorIP = new HashSet<>();

    public static boolean testSqlServerConnect(EtlAllCompany targetCompany) {
        Connection connection = null;
        String ip = targetCompany.getServerIp();
        try {
            if (listErrorIP.contains(ip)) {
                return false;
            }
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String jdbc = getSqlServerJdbc(ip, targetCompany.getDbName());
            connection = DriverManager.getConnection(jdbc, targetCompany.getUserName(), targetCompany.getPassword());
            listErrorIP.remove(ip);
            return true;
        } catch (Exception e) {
            log.error(targetCompany.getCompanyUuid() + " test connection fail", e);
            listErrorIP.add(ip);
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    public static String getSqlServerJdbc(String serverIp, String dbName) {
        String jdbc = "jdbc:sqlserver://%s:1433;databaseName=%s";
        return String.format(jdbc, serverIp, dbName);
    }
}
