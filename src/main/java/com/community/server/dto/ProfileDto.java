package com.community.server.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProfileDto {
    private String username;
    private String name;
    private BigDecimal balance = BigDecimal.ZERO;
    private BigDecimal backpack = BigDecimal.ZERO;
    private List<CompanyDto> company;
}
