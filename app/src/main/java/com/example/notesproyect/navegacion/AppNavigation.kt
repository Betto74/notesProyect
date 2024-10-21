package com.example.notesproyect.navegacion

import android.provider.ContactsContract.CommonDataKinds.Note
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.notesproyect.Screens.Notas
import com.example.notesproyect.Screens.Principal
import com.example.notesproyect.viewmodel.NotesViewModel

@Composable
fun AppNavigation(){
    val navController  = rememberNavController()

    NavHost(navController = navController, startDestination = AppScreen.FirstScreen.route){
        composable( route = AppScreen.FirstScreen.route){
            Principal(NotesViewModel(),navController)
        }
        composable( route = AppScreen.SecondScreen.route+ "/{text}",
            arguments = listOf(navArgument(name = "text"){
                type = NavType.StringType
            }) ){
            Notas(NotesViewModel(),navController, it.arguments?.getString("text"))
        }
    }
}