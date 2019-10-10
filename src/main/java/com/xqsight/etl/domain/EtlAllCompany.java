package com.xqsight.etl.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
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
public class EtlAllCompany implements Serializable{

    private String companyUuid;

    private String franchiseeUuid;

    private String companyAccount;

    private String companyName;

    private String companyCity;

    private String zonePart;

    private String serverIp;

    private String dbName;

    private Date createdTime;

    private String companyType;

    private String userName;

    private String password;

    /**
     * 全公司同步
     */
    private Boolean fullSynchronize;

}