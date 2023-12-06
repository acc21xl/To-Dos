package com.example.todo.screens

import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import java.io.ByteArrayOutputStream
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.autoSaver
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.todo.data.TodoDatabase
import com.example.todo.entities.DogEntity
import com.example.todo.viewmodels.DogViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import com.example.todo.viewmodels.TodosViewModel
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.material3.AlertDialog
import java.time.format.DateTimeFormatter

fun ByteArray.toBitmap(): Bitmap {
    return BitmapFactory.decodeByteArray(this, 0, this.size)
}

@Composable
fun DogDetailsScreen(navController: NavController, viewModel: DogViewModel, onBackClicked: () -> Unit) {
    val dog by viewModel.dog.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Check if dog is not null before displaying details
        if (dog != null) {
            // Display dog details using Text, Image, etc.
            Text(text = "Dog Name: ${dog!!.name}")
            Text(text = "Dog Breed: ${dog!!.breed}")
            Text(text = "Dog Birthday: ${dog!!.birthdayDate}")
            Text(text = "Dog Notes: ${dog!!.notes}")
            // Check if there are image bytes
            if (dog!!.imageBytes?.isNotEmpty() == true) {
                // Convert image bytes to Bitmap
                val bitmap = dog!!.imageBytes?.toBitmap()

                // Display image using Image composable
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Dog Image",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.Black, CircleShape)
                            .background(Color.Gray),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            // Display dog details using Text, Image, etc.
            Spacer(modifier = Modifier.height(30.dp))
            Text(text = " ${dog!!.name}")
            Spacer(modifier = Modifier.height(30.dp))
            Text(text = "${dog!!.breed}")
            Spacer(modifier = Modifier.height(30.dp))
            fun dateFormat(date: Date?) = with(date ?: Date()) {
                SimpleDateFormat("dd/MM/yyy").format(this)
            }
            var dogBirthday = dateFormat(dog!!.birthdayDate)
            Text(text = dogBirthday)
            Spacer(modifier = Modifier.height(30.dp))
            Text(text = " ${dog!!.notes}")

            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { showDialog = true }) {
                Text(text = "Edit")
            }
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text(text = "Edit Dog") },
                    text = {
                        // Show AddDogScreen in the dialog with the current dog details
                        AddDogScreen(
                            viewModel = viewModel,
                            navController = navController,
                            onDogAdded = { /* Handle added dog if needed */ },
                            existingDog = dog
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                // Handle the confirmation action if needed
                                showDialog = false
                            }
                        ) {
                            Text("Cancel Changes")
                        }
                    }
                )
            }
        }

    }
}