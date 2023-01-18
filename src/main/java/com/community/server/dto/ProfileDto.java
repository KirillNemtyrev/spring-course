package com.community.server.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProfileDto {
    private String username;
    private String name;
    private BigDecimal balance;
    private BigDecimal backpack;
    private List<CompanyDto> company;
}
