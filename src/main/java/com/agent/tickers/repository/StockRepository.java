package com.agent.tickers.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.agent.tickers.models.Stock;

public interface StockRepository extends JpaRepository<Stock, Long> {

    List<Stock> findByUser_Id(Long userId);
}
