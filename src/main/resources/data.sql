-- 建立幣別資料表
CREATE TABLE IF NOT EXISTS currency (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    currency_code VARCHAR(10),
    currency_name VARCHAR(50),
    exchange_rate DOUBLE
);

-- 插入初始測試資料
INSERT INTO currency (currency_code, currency_name, exchange_rate) VALUES ('USD', '美元', 1.0);
INSERT INTO currency (currency_code, currency_name, exchange_rate) VALUES ('GBP', '英鎊', 1.0);
INSERT INTO currency (currency_code, currency_name, exchange_rate) VALUES ('EUR', '歐元', 1.0);
