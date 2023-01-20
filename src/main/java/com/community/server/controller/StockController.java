package com.community.server.controller;

import com.community.server.body.BuyBody;
import com.community.server.body.SellBody;
import com.community.server.service.StockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/stock")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping
    public ResponseEntity<?> buy(HttpServletRequest httpServletRequest, @Valid @RequestBody BuyBody buyBody){
        return stockService.buy(httpServletRequest, buyBody);
    }

    @PostMapping("/sell")
    public ResponseEntity<?> sell(HttpServletRequest httpServletRequest, @Valid @RequestBody SellBody sellBody){
        return stockService.sell(httpServletRequest, sellBody);
    }

}
