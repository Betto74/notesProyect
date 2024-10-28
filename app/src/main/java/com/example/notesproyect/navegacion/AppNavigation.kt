package com.example.notesproyect.navegacion

import android.os.Build
import android.provider.ContactsContract.CommonDataKinds.Note
import androidx.annotation.RequiresApi
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.notesproyect.Screens.Notas
import com.example.notesproyect.Screens.Principal
import com.example.notesproyect.viewmodel.NotesViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(){
    val navController  = rememberNavController()
    val viewModel: NotesViewModel = viewModel()

    NavHost(navController = navController, startDestination = AppScreen.FirstScreen.route){
        composable( route = AppScreen.FirstScreen.route){
            Principal(viewModel,navController)
        }
        composable( route = AppScreen.SecondScreen.route+ "/{text}",
            arguments = listOf(navArgument(name = "text"){
                type = NavType.StringType
            }) ){
            Notas(viewModel,navController, it.arguments?.getString("text"))
        }
    }
}