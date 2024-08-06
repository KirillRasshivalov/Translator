package com.example.translator.managers;

import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import static com.example.translator.controller.TranslatorController.exceptionFound;
import static com.example.translator.service.YandexTranslateService.*;

public class TransferManager implements Callable<String> {
    private final String sourceWord;
    private final RestTemplate restTemplate;

    private final String apiKey;

    public TransferManager(String sourceWord, RestTemplate restTemplate, String apiKey){
        this.sourceWord = sourceWord;
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
    }

    @Override
    public String call() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("key", apiKey);
        params.put("text", sourceWord);
        params.put("lang", SourceLang + "-" + TargeetLang);

        try {
            Map<String, Object> response = restTemplate.getForObject(URL, Map.class, params);
            if (response != null && response.containsKey("text")) {
                return ((List<String>) response.get("text")).get(0);
            }
        } catch (Exception e) {
            exceptionFound = true;
            return "Во время перевода возникла непредвиденная ошибка";
        }
        return "";
    }
}
