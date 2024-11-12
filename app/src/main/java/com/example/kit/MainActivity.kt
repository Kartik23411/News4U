package com.example.kit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.kit.ui.theme.News4UTheme
import com.example.kit.viewmodal.HomeViewModal
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import com.example.kit.Screens.HomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel:HomeViewModal = viewModel()
            News4UTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HomeScreen()
//                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    News4UTheme {

    }
}