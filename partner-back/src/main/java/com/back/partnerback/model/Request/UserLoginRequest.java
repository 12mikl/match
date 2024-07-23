package com.back.partnerback.model.Request;

import lombok.Data;

/**
 * @author yang
 * @create 2024-05-13 21:31
 */
@Data
public class UserLoginRequest {

    private String userAccount;

    private String userPassword;
}
