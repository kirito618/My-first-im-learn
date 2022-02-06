package com.hzy.im.result;

import lombok.Data;

@Data
public class LoginResult {
    private String username;

    private String token;

    public LoginResult(String username,String token){
        this.username = username;
        this.token = token;
    }

    public LoginResult(){

    }
}
