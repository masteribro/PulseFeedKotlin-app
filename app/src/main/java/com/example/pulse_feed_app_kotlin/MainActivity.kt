package com.example.pulse_feed_app_kotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pulse_feed_app_kotlin.ui.screens.HomeScreen
import com.example.pulse_feed_app_kotlin.ui.theme.Pulse_feed_kotlinTheme
import com.example.pulse_feed_app_kotlin.viewmodels.HomeViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()


        setContent {


            Pulse_feed_kotlinTheme {

                val homeViewModel: HomeViewModel = viewModel()

                HomeScreen(viewModel = homeViewModel)
            }
        }
    }
}