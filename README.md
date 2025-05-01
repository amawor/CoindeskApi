# Coindesk API
透過呼叫 Coindesk API，將匯率資料更新至本地資料庫中。專案使用 Spring Boot 開發，並支援幣別資料的 CRUD 操作。

## H2 DB console
url: http://localhost:8080/h2-console
```
Driver Class:	org.h2.Driver
JDBC URL:	jdbc:h2:mem:testdb
User Name:	sa
Password:	
```


## 初始化 SQL

在 `src/main/resources/data.sql` 中包含了初始化資料庫的 SQL 腳本。以下為 SQL 內容：

```sql
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
```

## 資料庫結構

### CURRENCY 資料表

| 欄位 | 描述 |
|---|---|
| `ID` | ID |
| `CURRENCY_CODE`| 幣別代碼 |
| `CURRENCY_NAME`| 中文名稱 |
| `EXCHANGE_RATE`| 匯率 |

## 啟動方式

```java
src/main/java/com/example/coindesk/CoindeskApiApplication.java
```
該檔案將啟動Spring Boot並開始接受請求。

# 測試

以下為專案中各項功能的測試位置：

## 單元測試

- 測試資料轉換邏輯。  
  位置：`src/test/java/com/example/coindesk/controller/CurrencyControllerTest.java`

## API 測試

- 測試呼叫幣別對應表資料的 CRUD API 並顯示其內容。  
  位置：`src/test/java/com/example/coindesk/integration/CurrencyApiTest.java`
  ```java
  testCurrencyCrudOperations()
  ```

## 呼叫 Coindesk API 測試

- 測試呼叫 Coindesk API 並顯示其內容。  
  位置：`src/test/java/com/example/coindesk/integration/CurrencyApiTest.java`
  ```java
  testGetCoindeskRawData()
  ```

## 資料轉換 API 測試

- 測試呼叫資料轉換的 API 並顯示其內容。  
  位置：`src/test/java/com/example/coindesk/integration/CurrencyApiTest.java`
  ```java
  testUpdateCurrencyRateWithTransform()
  ```

# API 詳細說明

## 1. 更新所有幣別匯率

- **方法**: `PUT`
- **URL**: `/api/currencies/update-rate`
- **描述**: 呼叫 Coindesk API 更新所有幣別的匯率。
- **回應**: 成功更新回傳 200 OK，失敗回傳 502 Bad Gateway。

## 2. 查詢所有幣別資料

- **方法**: `GET`
- **URL**: `/api/currencies`
- **描述**: 查詢所有幣別資料。
- **回應**: 回傳所有幣別資料的 JSON 格式。

## 3. 查詢單一幣別資料

- **方法**: `GET`
- **URL**: `/api/currencies/{id}`
- **描述**: 查詢單一幣別的資料，根據 `id` 查詢。
- **回應**: 若幣別存在，回傳該幣別的資料，否則回傳 404 Not Found。

## 4. 新增幣別

- **方法**: `POST`
- **URL**: `/api/currencies`
- **描述**: 新增一個幣別資料。
- **回應**: 回傳新增的幣別資料，並返回 200 OK。

## 5. 更新幣別名稱

- **方法**: `PUT`
- **URL**: `/api/currencies/{currencyCode}`
- **描述**: 根據幣別代碼更新幣別名稱。
- **回應**: 若幣別存在且更新成功，回傳更新後的幣別資料，否則回傳 400 Bad Request(沒有提供名稱) 或 404 Not Found(查無此幣別)。

## 6. 刪除幣別

- **方法**: `DELETE`
- **URL**: `/api/currencies/{id}`
- **描述**: 根據 `id` 刪除指定幣別資料。
- **回應**: 若刪除成功，回傳 204 No Content，否則回傳 404 Not Found。

## 7. 查詢 Coindesk API 資料

- **方法**: `GET`
- **URL**: `/api/currencies/coindesk`
- **描述**: 查詢 Coindesk API 的原始資料並回傳。
- **回應**: 回傳 Coindesk API 的 JSON 資料，若失敗回傳 502 Bad Gateway。

## 8. 更新特定幣別資料

- **方法**: `PUT`
- **URL**: `/api/currencies/update-rate/{currencyCode}`
- **描述**: 更新特定幣別的匯率及最後更新時間。
- **回應**: 成功更新回傳 200 OK，並回傳更新後的幣別資料及更新時間；若失敗回傳 400 Bad Request(無法抓取coindesk api或是api回傳值有誤) 或 404 Not Found(無此特定幣別)。
