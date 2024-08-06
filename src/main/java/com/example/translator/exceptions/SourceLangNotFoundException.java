package com.example.translator.exceptions;

public class SourceLangNotFoundException extends Exception{
    public SourceLangNotFoundException(){ super("Не найден язык исходного сообщения");}
}
