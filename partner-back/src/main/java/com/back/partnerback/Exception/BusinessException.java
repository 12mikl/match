package com.back.partnerback.Exception;

import com.back.partnerback.common.ErrorCode;
import lombok.Data;

/** 自定义异常类
 * @author yang
 * @create 2024-05-19 14:00
 */
@Data
public class BusinessException extends RuntimeException{

    private final int code;
    private final String description;

    /**
     *  自己定义参数传入
     * @param message
     * @param code
     * @param description
     */
    public BusinessException(String message,int code,String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    /**
     * 使用定义好的枚举传入
     * @param errorCode
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
    }


    /**
     *  使用定义好的枚举错误码传入和自己定义的错误描述信息传入
     * @param errorCode
     * @param description
     */
    public BusinessException(ErrorCode errorCode,String description) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }
}
