package com.example.notesproyect.navegacion

sealed class AppScreen(val route : String) {
    object FirstScreen : AppScreen("first_screen")
    object SecondScreen : AppScreen("second_screen")
    object ThirdScreen : AppScreen("third_screen")
}