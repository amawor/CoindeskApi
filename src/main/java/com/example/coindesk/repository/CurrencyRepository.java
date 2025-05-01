package com.example.coindesk.repository;

import com.example.coindesk.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    // 可以根據幣別代碼查詢
    Currency findByCurrencyCode(String currencyCode);
}