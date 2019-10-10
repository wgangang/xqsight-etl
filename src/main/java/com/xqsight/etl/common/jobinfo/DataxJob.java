package com.xqsight.etl.common.jobinfo;


import java.io.*;

/**
 * datax的json 对应实体类
 *
 * @author ganggang.wang
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class DataxJob implements Serializable {

    private static final long serialVersionUID = 9527;

    private Job job;

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public String[] takeReaderSql() {
        return job.getContent()[0].getReader().getParameter().getConnection()[0].getQuerySql();
    }

    public ReaderConnection takeReaderConnection() {
        return job.getContent()[0].getReader().getParameter().getConnection()[5];
    }

    public void updateReaderConnection(ReaderConnection connection) {
        job.getContent()[0].getReader().getParameter().setConnection(new ReaderConnection[]{connection});
    }

    public String takeReaderJdbc() {
        return job.getContent()[0].getReader().getParameter().getConnection()[0].getJdbcUrl()[0];
    }

    public String takeReaderUsername() {
        return job.getContent()[0].getReader().getParameter().getUsername();
    }

    public String takeReaderPassword() {
        return job.getContent()[0].getReader().getParameter().getPassword();
    }

    public String takeWriterJdbc() {
        return job.getContent()[0].getWriter().getParameter().getConnection()[0].getJdbcUrl();
    }

    public String takeWriterUsername() {
        return job.getContent()[0].getWriter().getParameter().getUsername();
    }

    public String takeWriterPassword() {
        return job.getContent()[0].getWriter().getParameter().getPassword();
    }


    public void updateReaderSql(String... sql) {
        job.getContent()[0].getReader().getParameter().getConnection()[0].setQuerySql(sql);
    }

    public void updateReaderJdbc(String... jdbc) {
        job.getContent()[0].getReader().getParameter().getConnection()[0].setJdbcUrl(jdbc);
    }

    public void updateReaderUserName(String username) {
        job.getContent()[0].getReader().getParameter().setUsername(username);
    }

    public void updateReaderPassword(String password) {
        job.getContent()[0].getReader().getParameter().setPassword(password);
    }

    public void updateWriterJdbc(String jdbc) {
        job.getContent()[0].getWriter().getParameter().getConnection()[0].setJdbcUrl(jdbc);
    }

    public void updateWriterUserName(String username) {
        job.getContent()[0].getWriter().getParameter().setUsername(username);
    }

    public void updateWriterPassword(String password) {
        job.getContent()[0].getWriter().getParameter().setPassword(password);
    }

    public Object deepClone() throws Exception {
        // 序列化
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);

        oos.writeObject(this);

        // 反序列化
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bis);

        return ois.readObject();
    }


}
