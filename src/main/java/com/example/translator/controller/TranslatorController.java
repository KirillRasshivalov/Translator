package com.example.translator.controller;

import com.example.translator.exceptions.EmptyInputException;
import com.example.translator.exceptions.SourceLangNotFoundException;
import com.example.translator.exceptions.TargetLangNotFoundException;
import com.example.translator.managers.DataBaseManager;
import com.example.translator.service.YandexTranslateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

import static com.example.translator.managers.DataBaseManager.successfulConnection;

@Controller
public class TranslatorController {

    public static boolean exceptionFound;

    private static String IPadress;

    @Autowired
    private YandexTranslateService yandexTranslateService;

    @GetMapping("/")
    public String getTranslatorPage(Model model) {
        model.addAttribute("targetLang", "");
        model.addAttribute("sourceLang", "");
        model.addAttribute("inputText", "");
        model.addAttribute("outputText", "");
        model.addAttribute("exceptionText", "");
        return "translator";
    }

    @PostMapping("/translate")
    public String translate(@RequestParam("inputText") String inputText,
                            @RequestParam("sourceLang") String sourceLang,
                            @RequestParam("targetLang") String targetLang,
                            Model model) throws SourceLangNotFoundException, EmptyInputException,
            TargetLangNotFoundException, ExecutionException, InterruptedException, UnknownHostException, SQLException {

        String outputText = yandexTranslateService.translate(inputText, sourceLang, targetLang);


        model.addAttribute("inputText", inputText);
        model.addAttribute("targetLang", targetLang);
        model.addAttribute("sourceLang", sourceLang);
        try{
            InetAddress localhost = InetAddress.getLocalHost();
            IPadress = localhost.getHostAddress();
        }catch(Exception e){
            exceptionFound = true;
            outputText =  "Ошибка во время получения IP адреса.";
        }
        if (exceptionFound){
            if (successfulConnection) DataBaseManager.fillTable(IPadress, inputText, "null", outputText);
            model.addAttribute("exceptionText", outputText);
        }else {
            if (successfulConnection) DataBaseManager.fillTable(IPadress, inputText, outputText, "null");
            model.addAttribute("exceptionText", "");
            model.addAttribute("outputText", outputText);
        }
        return "translator";
    }
}
