package com.back.partnerback.model.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: 楼兰
 * @CreateTime: 2024-07-19
 * @Description:
 */
@Data
public class TeamVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * id
     */
    private Long userId;

    /**
     * 队伍名称
     */
    private String name;

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date expireTime;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     *  当前队伍人数
     */
    private Integer hasJoinNum;

    private boolean hasJoin = false;

    private static final long serialVersionUID = 1L;
}
