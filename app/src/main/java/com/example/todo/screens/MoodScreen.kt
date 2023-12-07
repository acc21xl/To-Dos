package com.example.todo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.todo.enums.MoodScore
import com.example.todo.viewmodels.TodosViewModel
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.todo.enums.Priority
import com.example.todo.TaskRow
import com.example.todo.entities.TodoEntity
import com.example.todo.viewmodels.MoodViewModel

@Composable
fun MoodScreen(navController: NavController, viewModel: MoodViewModel, onBackClicked: () -> Unit) {
    val dog by viewModel.dog.collectAsState()

    Column{

        Text(
            text="Average Mood:",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(8.dp)
        )
            // Ensure dog is not null before displaying details
            if (dog != null) {
                // Calculate average of last 5 scores
                val avg: MoodScore = getLastFiveMoodsAvg()
                Text(text = "${avg}")
            }
            // Display last 5 completed todos with scores
            //still to do

    }
}

private fun ColumnScope.Text(
    text: String,
    style: TextStyle,
    modifier: Modifier,
    function: () -> Unit
) = Unit

//Get average of last 5, return closest of the enum values
fun getLastFiveMoodsAvg(): MoodScore {
    val lastFive: Int
    val avgMood: MoodScore = MoodScore.NEUTRAL
    //val lastFive = getLastFiveMoods(): Int
    //if lastFive >= 4.5:
        //avgMood = EXCELLENT
    //if 3.5 <= lastFive < 4.5:
        //avgMood = HAPPY
    //if 2.5 <= lastFive < 3.5:
        //avgMood = NEUTRAL
    //if 1.5 <= lastFive < 2.5:
        //avgMood = SAD
    //if lastFive < 1.5:
        //avgMood = UPSET
    return avgMood
}