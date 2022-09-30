package com.github.kotlintelegrambot.echo.util

import java.io.FileReader
import java.util.Properties

class LanguageHandler {
    companion object{
        var botLanguage: String = ""
        private var vocabulary: Properties = Properties()

        fun setLanguage(language: String){
            botLanguage = language
            val languageReader = FileReader("src/main/resources/ru_$language.yml")
            vocabulary.load(languageReader)
        }

        fun getText(text: String) = vocabulary.getProperty(text)
    }
}