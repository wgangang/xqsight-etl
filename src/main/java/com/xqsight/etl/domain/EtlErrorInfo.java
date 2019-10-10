package com.xqsight.etl.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

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
public class EtlErrorInfo {

    private Date catchTime;

    private String jsonName;

    private String errorInfo;
}