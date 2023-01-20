package com.community.server.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class StockDto {
    private BigDecimal cost;
    private BigDecimal much;
    private Date date;
    private float procent;
    private boolean upper;
}
