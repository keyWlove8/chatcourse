package com.k8.bean;

import lombok.Data;

@Data
public class TokenContext {
    private String accessToken;
    private String refreshToken;
    private String accessJti;
    private String refreshJti;
    private String username;
    private String userid;
}
