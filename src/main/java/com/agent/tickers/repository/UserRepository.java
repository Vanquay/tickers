package com.agent.tickers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.agent.tickers.models.Users;

public interface UserRepository extends JpaRepository<Users, Long> {

}
