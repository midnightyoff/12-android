package com.eltex.androidschool

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eltex.androidschool.ui.theme.AndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val viewModel = viewModel<EventViewModel>()
                    val event by viewModel.event

                    EventCard(
                        modifier = Modifier.padding(innerPadding),
                        event = event,
                        likeClicked = {
                            viewModel.like()
                        },
                        shareClicked = {
                            Toast.makeText(this, getString(R.string.not_implemented), Toast.LENGTH_SHORT).show()
                        },
                        participateClicked = {
                            viewModel.participate()
                        }
                    )
                }
            }
        }
    }
}
