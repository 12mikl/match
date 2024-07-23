package com.back.partnerback.model.Request;

import lombok.Data;

/** 分页请求参数
 * @author yang
 * @create 2024-05-12 18:39
 */
@Data
public class QueryPageRequest {

    private Integer pageNum = 1; // 默认当前页

    private Integer pageSize = 10; // 默认页数量

}
