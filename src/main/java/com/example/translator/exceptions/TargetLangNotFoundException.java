package com.example.translator.exceptions;

public class TargetLangNotFoundException extends Exception{
    public TargetLangNotFoundException() {super("Не найден язык в который нужно перевести");}
}
