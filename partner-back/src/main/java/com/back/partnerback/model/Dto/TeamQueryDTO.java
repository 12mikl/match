package com.back.partnerback.model.Dto;

import com.back.partnerback.model.Request.QueryPageRequest;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: 楼兰
 * @CreateTime: 2024-07-17
 * @Description:
 */
@Data
public class TeamQueryDTO extends QueryPageRequest implements Serializable {

    /**
     * 队伍名称
     */
    private String searchText;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;
}
