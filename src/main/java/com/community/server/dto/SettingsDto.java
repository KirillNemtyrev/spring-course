package com.community.server.dto;

import lombok.Data;

@Data
public class SettingsDto {
    private String username;
    private String name;
    private String email;
    private Long lastDividend;
}
