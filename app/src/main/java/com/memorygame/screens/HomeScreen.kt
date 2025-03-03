package com.memorygame.screens
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.memorygame.components.ButtonGrid
import com.memorygame.components.TitleText
import com.memorygame.main.MemoryGameViewModel
import com.memorygame.ui.theme.MemoryGameTheme


// =================================================================================================

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(viewModel = MemoryGameViewModel(), navController = rememberNavController())
}

// =================================================================================================

@Composable
fun HomeScreen(viewModel: MemoryGameViewModel, navController: NavHostController) {

    MemoryGameTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
            modifier            = Modifier
                                    .background(MaterialTheme.colorScheme.primary)
                                    .padding(10.dp)
                                    .fillMaxSize()
        ) {

            TitleText()
            ButtonGrid(viewModel)

        }
    }
}