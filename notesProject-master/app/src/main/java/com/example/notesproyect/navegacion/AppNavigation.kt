@file:Suppress("DEPRECATION")

package com.example.notesproyect.navegacion

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.notesproyect.Screens.DetailsNoteScreen
import com.example.notesproyect.Screens.Notas
import com.example.notesproyect.Screens.Principal
import com.example.notesproyect.viewmodel.NotesViewModel
import com.example.notesproyect.data.Note


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppScreen.FirstScreen.route) {
        composable(route = AppScreen.FirstScreen.route) {
            Principal(navController)
        }
        composable(route = AppScreen.SecondScreen.route + "/{text}/{noteId}",
            arguments = listOf(
                navArgument(name = "text") {
                    type = NavType.StringType
                },
                navArgument(name = "noteId") {
                    type = NavType.IntType // Asegúrate de que el ID sea un entero
                }
            )
        ) { backStackEntry ->
            val text = backStackEntry.arguments?.getString("text") ?: "" // Proporciona un valor por defecto si es null
            val noteId = backStackEntry.arguments?.getInt("noteId") ?: -1
            Notas(navController, text, noteId) // Pasa ambos parámetros
        }
        composable(
            route = AppScreen.ThirdScreen.route + "/{id}",
            arguments = listOf(
                navArgument("id") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0

            // Aquí se crea una nueva nota con el id obtenido


            DetailsNoteScreen(navController, id)
        }



    }
}