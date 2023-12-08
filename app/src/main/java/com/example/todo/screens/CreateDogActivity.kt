package com.example.todo.screens

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.todo.entities.DogEntity
import com.example.todo.viewmodels.DogViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import com.example.todo.screens.TodoForm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDogScreen(
    viewModel: DogViewModel,
    navController: NavController,
    onDogAdded: ((DogEntity) -> Unit)?,
    existingDog: DogEntity? = null
) {
    var name by remember { mutableStateOf(existingDog?.name.orEmpty()) }
    var breed by remember { mutableStateOf(existingDog?.breed.orEmpty()) }
    var birthday by remember { mutableStateOf(existingDog?.birthdayDate ?: Date()) }
    var notes by remember { mutableStateOf(existingDog?.notes.orEmpty()) }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(existingDog?.imageBytes?.asBitmap()) }

    // Use the provided existingDog details to initialize form fields
    LaunchedEffect(existingDog) {
        existingDog?.let {
            name = it.name
            breed = it.breed
            birthday = it.birthdayDate ?: Date()
            notes = it.notes
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DogImagePicker(imageBitmap) { newBitmap -> imageBitmap = newBitmap }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = breed,
            onValueChange = { breed = it },
            label = { Text("Breed") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        val context = LocalContext.current
        DatePicker(
            context = context,
            selectedDateTime = birthday,
            onDateTimeSelected = { newDate ->
                birthday = newDate
            }
        )

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        val buttonScope = rememberCoroutineScope()
        //val dao = TodoDatabase.(LocalContext.current).dogDao()
        fun insertOnClick(dog: DogEntity) {
            buttonScope.launch {
                //dao.insert(dog)
            }
        }

        Button(
            onClick = {
                // Convert the Bitmap to ByteArray
                val imageBytes = imageBitmap?.let { bitmap ->
                    ByteArrayOutputStream().apply {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, this)
                    }.toByteArray()
                }

                // Create a Dog object to be saved in the database
                val dog = DogEntity(
                    name = name,
                    breed = breed,
                    birthdayDate = birthday,
                    notes = notes,
                    imageBytes = imageBytes,
                    deleted = false
                )
                if (validateDogInput(name, breed, notes)){
                    viewModel.createOrUpdateDog(dog)
                    navController.popBackStack()
                }

                if (onDogAdded != null) {
                    onDogAdded(dog)
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = "Confirm")
        }
    }
}
@Composable
fun DogImagePicker(imageBitmap: Bitmap?, onImageCaptured: (Bitmap) -> Unit) {
    var hasCameraPermission by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { bitmap: Bitmap? ->
            bitmap?.let { onImageCaptured(it) }
        }
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            hasCameraPermission = isGranted
            if (isGranted) {
                cameraLauncher.launch(null)
            } else {
                // Permission denied
            }
        }
    )

    LaunchedEffect(Unit) {
        hasCameraPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .border(2.dp, Color.Gray, CircleShape)
            .background(if (imageBitmap != null) Color.Transparent else Color.LightGray)
            .clickable {
                if (hasCameraPermission) {
                    cameraLauncher.launch(null)
                } else {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }
    ) {
        imageBitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Captured Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } ?: Icon(
            imageVector = Icons.Filled.CameraAlt,
            contentDescription = "Open Camera",
            modifier = Modifier.size(36.dp)
        )
    }
}

@Composable
fun DatePicker(
    context: Context,
    selectedDateTime: Date?,
    onDateTimeSelected: (Date) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val dateTimeString = selectedDateTime?.let {
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(it)
    } ?: "Select Date and Time"

    OutlinedButton(
        onClick = { showDialog = true },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = dateTimeString)
    }

    if (showDialog) {
        val currentCalendar = Calendar.getInstance()
        selectedDateTime?.let { currentCalendar.time = it }

        val year = currentCalendar.get(Calendar.YEAR)
        val month = currentCalendar.get(Calendar.MONTH)
        val day = currentCalendar.get(Calendar.DAY_OF_MONTH)
        val hour = currentCalendar.get(Calendar.HOUR_OF_DAY)
        val minute = currentCalendar.get(Calendar.MINUTE)

        DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                TimePickerDialog(
                    context,
                    { _, selectedHour, selectedMinute ->
                        val selectedCalendar = Calendar.getInstance().apply {
                            set(selectedYear, selectedMonth, selectedDayOfMonth, selectedHour, selectedMinute)
                        }
                        onDateTimeSelected(selectedCalendar.time)
                        showDialog = false
                    },
                    hour,
                    minute,
                    true // Use 24-hour format
                ).show()
            },
            year,
            month,
            day
        ).show()
    }
}
// Convert ByteArray to Bitmap
fun ByteArray?.asBitmap(): Bitmap? {
    return if (this != null && isNotEmpty()) {
        BitmapFactory.decodeByteArray(this, 0, size)
    } else {
        null
    }
}

fun validateDogInput(name: String, breed: String, notes: String): Boolean {
    if (name.isBlank() || !isInputSafe(name)) return false
    if (breed.isBlank() || !isInputSafe(breed)) return false
    if (!isInputSafe(notes)) return false
    return true
}

