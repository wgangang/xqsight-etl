package com.xqsight.etl.metadata;

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
public class DataxExcuteLog implements Serializable{

    private long cost;

    private String jobCommand;

    @Override
    public String toString() {
        return "DataxExcuteLog{" +
                "cost=" + cost +
                ", jobCommand='" + jobCommand + '\'' +
                '}';
    }
}
