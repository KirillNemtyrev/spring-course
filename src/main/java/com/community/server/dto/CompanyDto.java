package com.community.server.dto;

import com.community.server.entity.StockEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CompanyDto {
    private Long id;
    private String name;
    private String owner;
    private List<StockEntity> stocks;
    private Long stock;

    private BigDecimal cost;
    private float procent;
    private boolean upper;
    private BigDecimal much;
}
