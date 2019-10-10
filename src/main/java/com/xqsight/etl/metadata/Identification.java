package com.xqsight.etl.metadata;

/**
 * 〈一句话功能简述〉<br>
 * 〈功能详细描述〉
 *
 * @author ganggang.wang
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public interface Identification {

    interface METAJOBINFO_STATE {
        /**
         * "KILL": 243,"FAIL": -1,"OK": 0,"RUN": 1,"RETRY": 2, "未运行":3, "超时"：10
         */
        int KILL = 143;
        int FAIL = -1;
        int OK = 0;
        int RUN = 1;
        int RETRY = 2;
        int HAVENOTRUN = 3;
        int TIMEOUT = 10;

    }

    interface METAJOBINFO_JOBTYPE {
        /**
         * "ALL": 全量, "INCREMRNT": 增量
         */
        String ALL = "ALL";
        String INCREMRNT = "INCREMRNT";
    }

    interface DATAXJOBINFO {
        int contentIdx = 0;
    }


}
