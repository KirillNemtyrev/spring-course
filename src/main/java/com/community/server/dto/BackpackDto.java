package com.community.server.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class BackpackDto {
    private BigDecimal balance;
    private BigDecimal brokerage;
    private List<CompanyDto> companyList;
}
