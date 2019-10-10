package com.xqsight.etl.common.jobinfo;

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
public class ReaderParameter implements Serializable {

    private static final long serialVersionUID = 9521;

    private String username;

    private String password;

    private String where;

    private ReaderConnection[] connection;

}
