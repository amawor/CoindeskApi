package com.example.coindesk.integration;

import com.example.coindesk.entity.Currency;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CurrencyApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/currencies";
    }

    // 2. 測試呼叫幣別 CRUD API，並顯示其內容
    @Test
    public void testCurrencyCrudOperations() throws Exception {
        // 1. 建立新幣別
        Currency newCurrency = new Currency();
        newCurrency.setCurrencyCode("TWD");
        newCurrency.setCurrencyName("新台幣");
        newCurrency.setExchangeRate(30.5);

        ResponseEntity<Currency> createResponse = restTemplate.postForEntity(getBaseUrl(), newCurrency, Currency.class);
        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        Currency createdCurrency = createResponse.getBody();
        assertNotNull(createdCurrency);
        System.out.println("新增幣別 JSON: " + objectMapper.writeValueAsString(createdCurrency));

        Long id = createdCurrency.getId();

        // 2. 查詢所有幣別
        ResponseEntity<Currency[]> allCurrenciesResponse = restTemplate.getForEntity(getBaseUrl(), Currency[].class);
        assertEquals(HttpStatus.OK, allCurrenciesResponse.getStatusCode());
        System.out.println("所有幣別 JSON: " + objectMapper.writeValueAsString(Arrays.asList(allCurrenciesResponse.getBody())));

        // 3. 查詢單一幣別
        ResponseEntity<Currency> singleCurrencyResponse = restTemplate.getForEntity(getBaseUrl() + "/" + id, Currency.class);
        assertEquals(HttpStatus.OK, singleCurrencyResponse.getStatusCode());
        System.out.println("單一幣別 JSON: " + objectMapper.writeValueAsString(singleCurrencyResponse.getBody()));

        // 4. 更新幣別名稱
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> updateEntity = new HttpEntity<>("\"台幣\"", headers);

        ResponseEntity<Currency> updateResponse = restTemplate.exchange(
                getBaseUrl() + "/TWD", HttpMethod.PUT, updateEntity, Currency.class);
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        System.out.println("更新後幣別 JSON: " + objectMapper.writeValueAsString(updateResponse.getBody()));

        // 5. 刪除幣別
        restTemplate.delete(getBaseUrl() + "/" + id);

        // 6. 確認已刪除
        ResponseEntity<Currency> deletedCheck = restTemplate.getForEntity(getBaseUrl() + "/" + id, Currency.class);
        assertEquals(HttpStatus.NOT_FOUND, deletedCheck.getStatusCode());
        System.out.println("已刪除幣別查詢結果 HTTP code: " + deletedCheck.getStatusCode());
    }

    // 3. 測試呼叫 coindesk API，並顯示其內容。
    @Test
    public void testGetCoindeskRawData() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                getBaseUrl()+"/coindesk", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        System.out.println("Coindesk API Response: " + response.getBody());
    }

    // 4. 測試呼叫資料轉換的API，並顯示其內容
    //    抓取最新匯率並 更新特定幣別資料 印出API最後更新時間
    @Test
    public void testUpdateCurrencyRateWithTransform() {

        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl()+"/update-rate/USD",
                HttpMethod.PUT,
                new HttpEntity<>(null),
                String.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        System.out.println("轉換API結果: " + response.getBody());
    }

}
