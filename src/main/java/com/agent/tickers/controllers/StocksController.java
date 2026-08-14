package com.agent.tickers.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api")
public class StocksController {
    
    @GetMapping("/stocks")
    public String getStocks() {
        
        return "List of stocks";
    }
}
