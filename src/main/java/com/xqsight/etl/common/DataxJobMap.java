package com.xqsight.etl.common;


import com.xqsight.etl.common.jobinfo.DataxJob;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 〈一句话功能简述〉<br>
 * 〈功能详细描述〉
 *
 * @author ganggang.wang
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class DataxJobMap implements Serializable {

    @Getter
    @Setter
    private Map<String, DataxJob> dataxJobMap;

    public boolean isEmpty() {
        return dataxJobMap == null || dataxJobMap.size() < 1 || dataxJobMap.isEmpty();
    }

    public DataxJobMap(Map<String, DataxJob> dataxJobMap) {
        this.dataxJobMap = dataxJobMap;
    }

    public DataxJob remove(String key) {
        return dataxJobMap.remove(key);
    }

    public int getSize() {
        return dataxJobMap.size();
    }

    public void put(String key, DataxJob dataxJob) {
        dataxJobMap.put(key, dataxJob);
    }

    public void updateReaderSql(String jsonFile, String... sql) {
        DataxJob dataxJob = dataxJobMap.get(jsonFile);
        dataxJob.updateReaderSql(sql);
    }

    public DataxJobMap filterByJsonFile(String key) {
        Map<String, DataxJob> map = new HashMap();
        Set<Map.Entry<String, DataxJob>> set = dataxJobMap.entrySet();
        for (Map.Entry<String, DataxJob> entry : set) {
            String k = entry.getKey();
            if (k.startsWith(key)) {
                map.put(k, entry.getValue());
            }
        }
        return new DataxJobMap(map);
    }
}
