package com.xqsight.etl.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 〈一句话功能简述〉<br>
 * 〈功能详细描述〉
 *
 * @author ganggang.wang
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@Getter
@Setter
public class EtlJobInfo implements Serializable {

    private String jsonName;

    private String newJsonName;

    private String readerJdbc;

    private String readerName;

    private String readerPword;

    private String writerJdbc;

    private String writerName;

    private String writerPword;

    private String jobType;

    private String condition;

    private String startTime;

    private String endTime;

    private String cron;

    private Integer state;

    private Boolean locked;

    /**
     * 全表同步
     */
    private Boolean fullTableSynchronize;

}