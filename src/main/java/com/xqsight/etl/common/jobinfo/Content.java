package com.xqsight.etl.common.jobinfo;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * datax content 元素实体类<br>
 *
 * @author ganggang.wang
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@Getter
@Setter
public class Content implements Serializable {

    private static final long serialVersionUID = 9523;

    /**
     * 读配置
     */
    private Reader reader;

    /**
     * 写配置
     */
    private Writer writer;

}
