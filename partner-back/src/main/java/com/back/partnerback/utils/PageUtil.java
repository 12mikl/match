package com.back.partnerback.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/** 分页
 * @author yang
 * @create 2024-05-12 18:33
 */
public class PageUtil implements Serializable {

    /**
     * 封装前端所需的分页
     * @param info
     * @return
     */
    public static Map<String,Object> pageTable(IPage<?> info){
        Map<String,Object> map = new HashMap<>(2);
        map.put("rows",info.getRecords());
        map.put("total",info.getTotal());
        return map;
    }
}
