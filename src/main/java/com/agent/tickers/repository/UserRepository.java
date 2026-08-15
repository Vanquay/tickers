package com.agent.tickers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.agent.tickers.models.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
