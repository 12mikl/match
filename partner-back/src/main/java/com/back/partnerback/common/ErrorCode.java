package com.back.partnerback.common;

/** 错误状态码
 * @author yang
 * @create 2024-05-19 13:37
 */
public enum  ErrorCode {

    SUCCESS(0,"请求成功",""),
    PARAMS_ERROR(40000,"请求参数错误",""),
    NULL_PARAMS(400001,"请求数据为空",""),
    NO_LOGIN(40100,"未登录",""),
    NO_AUTH(40101,"无权限操作",""),
    NO_USER(40102,"不存在该用户",""),
    SYSTEM_ERROR(50000,"系统内部异常","")
    ;

    /**
     * 状态码
     */
    private final int code;

    /**
     * 状态码信息
     */
    private final String message;

    /**
     * 状态码描述信息(详情)
     */
    private final String description;

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}
