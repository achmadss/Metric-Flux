package dev.achmad.metricflux.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen

object HomeScreen: Screen {
    private fun readResolve(): Any = HomeScreen

    @SuppressLint("MissingPermission")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel = viewModel<HomeScreenViewModel>()
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("Metric Flux")
                    },
                )
            }
        ) { contentPadding ->
            Box(modifier = Modifier.padding(contentPadding).fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {

                }
            }
        }
    }
}