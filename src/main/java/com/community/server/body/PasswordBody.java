package com.community.server.body;

import lombok.Data;

@Data
public class PasswordBody {
    private String oldPassword;
    private String newPassword;
}
