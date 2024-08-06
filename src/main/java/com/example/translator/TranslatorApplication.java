package com.example.translator;

import com.example.translator.managers.DataBaseManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.SQLException;

import static com.example.translator.managers.DataBaseManager.successfulConnection;

@SpringBootApplication
public class TranslatorApplication {

    public static void main(String[] args) throws SQLException {
        DataBaseManager.getConnect();
        if (successfulConnection) DataBaseManager.createTable();

        SpringApplication.run(TranslatorApplication.class, args);
    }
}
