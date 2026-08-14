package com.agent.tickers.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.agent.tickers.model.Stock;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class StockRepository extends JpaRepository<Stock, Long> {

}
