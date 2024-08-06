package com.example.translator.service;

import com.example.translator.exceptions.EmptyInputException;
import com.example.translator.exceptions.ErrorDuringTranslateException;
import com.example.translator.exceptions.SourceLangNotFoundException;
import com.example.translator.exceptions.TargetLangNotFoundException;
import com.example.translator.managers.TransferManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.example.translator.controller.TranslatorController.exceptionFound;

@Service
public class YandexTranslateService {

    @Value("${yandex.api.key}")
    private String apiKey;

    private static final String YANDEX_API_URL = "https://translate.yandex.net/api/v1.5/tr.json/translate";

    private final RestTemplate restTemplate;

    public static String URL;
    public static String SourceLang;
    public static String TargeetLang;

    public YandexTranslateService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String translate(String text, String sourceLang, String targetLang) throws ExecutionException, InterruptedException {
        String url = YANDEX_API_URL + "?key={key}&text={text}&lang={lang}";

        try{
            if (sourceLang.equals("not found")) throw new SourceLangNotFoundException();
            else if (targetLang.equals("not found")) throw new TargetLangNotFoundException();
            else if(text.isEmpty()) throw new EmptyInputException();
        } catch (SourceLangNotFoundException | TargetLangNotFoundException | EmptyInputException e){
            exceptionFound = true;
            return e.getMessage();
        }

        String[] words = text.split(" ");
        ExecutorService executor = Executors.newFixedThreadPool(10);
        ArrayList<Future<String>> finalText = new ArrayList<>();

        for(String word : words){
            try{
                finalText.add(executor.submit(new TransferManager(word, restTemplate, apiKey)));
                if (exceptionFound) throw new ErrorDuringTranslateException();
            }catch(ErrorDuringTranslateException e){
                return e.getMessage();
            }catch(RuntimeException e){
                exceptionFound = true;
                return "Возникла непредвиденная ошибка.";
            }
        }
        String responce = "";
        for(Future<String> word : finalText){
            try {
                responce += word.get() + " ";
            }catch(InterruptedException | ExecutionException e){
                exceptionFound = true;
                executor.shutdown();
                return "Что-то пошло не так.";
            }
        }
        executor.shutdown();
        return responce;
    }
}
