package com.test.ChatApplicaation.security;

import lombok.Data;

@Data
public class JwtRequest {
    private String username;
    private String password;
}
