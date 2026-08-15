package com.agent.tickers.models;

import java.math.BigDecimal;
import java.time.Instant;

public record StockPrice(String symbol, BigDecimal price, Instant timestamp) {
}
