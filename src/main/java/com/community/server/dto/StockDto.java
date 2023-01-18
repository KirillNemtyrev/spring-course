package com.community.server.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StockDto {
    private BigDecimal cost;
    private BigDecimal much;
    private float procent;
    private boolean upper;
}
