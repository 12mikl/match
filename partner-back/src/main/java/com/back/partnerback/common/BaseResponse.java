package com.back.partnerback.common;

import lombok.Data;

import java.io.Serializable;

/** 通用返回类
 * @author yang
 * @create 2024-05-19 13:10
 */
@Data
public class BaseResponse<T> implements Serializable {

    private int code;

    private T data;

    private String message;

    private String description;

    public BaseResponse(int code, T data, String message,String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }

    public BaseResponse(int code, T data,String message) {
        this(code,data,message,"");
    }

    /**
     *  失败  data未null
     * @param errorCode
     */
    public BaseResponse(ErrorCode errorCode){
        this(errorCode.getCode(),null,errorCode.getMessage());
    }

    /**
     *  失败
     * @param errorCode
     */
    public BaseResponse(ErrorCode errorCode,String message,String description){
        this(errorCode.getCode(),null,message,description);
    }

}
