package com.example.translator.managers;

import java.io.Console;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class DataBaseManager {
    private static Connection connection;
    private static String URL;
    private static String USERNAME;
    private static String PASSWORD;

    public static boolean successfulConnection;

    private static void loadInformation(){
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите URL вышей базы данных: ");
        URL = scanner.nextLine();
        System.out.print("Введите имя пользователя: ");
        USERNAME = scanner.nextLine();
        System.out.print("Введите пароль от базы данных: ");
        PASSWORD = scanner.nextLine();
    }

    public static void getConnect() throws SQLException {
        try {
            loadInformation();
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            successfulConnection = true;
            System.out.println("Соедиение с базой данных прошло успешно.");
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static void createTable(){
        String usersRequestTable = "CREATE TABLE IF NOT EXISTS usersRequests (" +
                "id_users SERIAL PRIMARY KEY," +
                "IPAdress VARCHAR(1000)," +
                "originalText VARCHAR(1000)," +
                "errorText VARCHAR(1000)," +
                "resultText VARCHAR(1000));";
        try(PreparedStatement statement = connection.prepareStatement(usersRequestTable)){
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void fillTable(String IP, String originalText, String resultText, String errors) throws SQLException {
        String query = "INSERT INTO usersRequests (IPAdress, originalText, errorText, resultText) VALUES (?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        try{
            connection.setAutoCommit(false);
            preparedStatement.setObject(1, IP);
            preparedStatement.setObject(2, originalText);
            preparedStatement.setObject(3, errors);
            preparedStatement.setObject(4, resultText);
            preparedStatement.executeUpdate();
            connection.commit();
            System.out.println("Данные успешно добавлены в базу данных.");
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
        } finally {
            connection.setAutoCommit(true);
            preparedStatement.close();
        }
    }
}
