package com.example.todo.screens

import android.Manifest
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.widget.DatePicker
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.tailtasks.enums.MoodScore
import com.example.tailtasks.enums.Priority
import com.example.tailtasks.enums.Status
import com.example.todo.MyNotification
import com.example.todo.entities.TagEntity
import com.example.todo.entities.TodoEntity
import com.example.todo.enums.RepeatFrequency
import com.example.todo.viewmodels.TodosViewModel
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.compose.ui.platform.LocalContext



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoForm(viewModel: TodosViewModel, onClose: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val selectedTags = remember { mutableStateListOf<TagEntity>() }
    var priority by remember { mutableStateOf(Priority.LOW) }
    var repeat by remember { mutableStateOf(false) }
    var latitude by remember { mutableStateOf(0.0) }
    var longitude by remember { mutableStateOf(0.0) }
    var distance by remember { mutableStateOf(0.0) }
    var toCompleteByDate by remember { mutableStateOf(Date()) }
    var repeatFrequency by remember { mutableStateOf(RepeatFrequency.Daily) }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var imageBytes by remember { mutableStateOf<ByteArray?>(null) }
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        BannerImagePicker(imageBitmap) { newBitmap ->
            imageBitmap = newBitmap
            imageBytes = bitmapToByteArray(newBitmap)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 128.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(Modifier.height(8.dp))
            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            DateInput("To Complete By", toCompleteByDate) { newDate ->
                toCompleteByDate = newDate
            }
            Spacer(Modifier.height(8.dp))

            TagInput(viewModel, selectedTags)
            Spacer(Modifier.height(8.dp))


            // Dropdowns or selectors for Priority and Status
            DropdownPriority(priority) { priority = it }

            // Repeat Checkbox and Frequency Selector
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = repeat,
                    onCheckedChange = { repeat = it }
                )
                Text("Repeat")
            }

            // Show Repeat Frequency Selector if repeat is true
            if (repeat) {
                RepeatFrequencySelector(
                    selectedFrequency = repeatFrequency,
                    onFrequencySelected = { repeatFrequency = it }
                )
            }
            Spacer(Modifier.height(8.dp))

            BoxWithConstraints {
                val maxWidth = this.maxWidth
                val fieldWidth = maxWidth / 3

                Row(horizontalArrangement = Arrangement.Start) {
                    NumberInputField(
                        value = latitude,
                        onValueChange = { latitude = it },
                        label = "Latitude",
                        width = fieldWidth
                    )
                    NumberInputField(
                        value = longitude,
                        onValueChange = { longitude = it },
                        label = "Longitude",
                        width = fieldWidth
                    )
                    NumberInputField(
                        value = distance,
                        onValueChange = { distance = it },
                        label = "Distance",
                        width = fieldWidth
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Button(
                    onClick = onClose,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    modifier = Modifier.padding(vertical = 0.dp, horizontal = 8.dp)
                ) {
                    Text("Cancel")
                }

                Button(onClick = {
                    if (validateTodoInput(title, description)) {
                        val newTodo = TodoEntity(
                            title = title,
                            description = description,
                            dogId = 0, // Filled out later
                            moodId = null,
                            priority = priority,
                            status = Status.ONGOING,
                            repeat = repeat,
                            repeatFrequency = repeatFrequency.ordinal,
                            latitude = latitude,
                            longitude = longitude,
                            distance = distance,
                            isCompleted = false,
                            toCompleteByDate = toCompleteByDate,
                            creationDate = Date(),
                            completionDate = null,
                            deleted = false,
                            imageBytes = imageBytes
                        )


                        viewModel.submitTodo(newTodo, selectedTags)
                        onClose()
                    } else {
                        // Show error message for invalid input
                    }
                    val myNotification = MyNotification(context,title,description)
                    val time = toCompleteByDate.time
                    myNotification.scheduleNotification(timeInMillis = time)
                }) {
                    Text("Submit Todo")
                }
            }
            }
        }
    }

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TagInput(viewModel: TodosViewModel, selectedTags: MutableList<TagEntity>) {
    var text by remember { mutableStateOf("") }

    Column {
        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Add a Tag") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                if (text.isNotBlank()) {
                    selectedTags.add(TagEntity(title = text.trim(), id =0))
                    text = ""
                }
            })
        )
        FlowRow {
            selectedTags.forEach { tag ->
                Chip(tag = tag.title, onRemove = { selectedTags.remove(tag) })
            }
        }
    }
}

@Composable
fun Chip(tag: String, onRemove: () -> Unit) {
    Row(modifier = Modifier.padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(text = tag, modifier = Modifier.padding(8.dp))
        IconButton(onClick = onRemove) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Remove tag")
        }
    }
}

@Composable
fun MoodSelector(onMoodSelected: (MoodScore) -> Unit) {
    var sliderPosition by remember { mutableStateOf(3f) } // Default to NEUTRAL
    val moodScore = getMoodFromSliderPosition(sliderPosition.toInt())

    Column {
        Text("Select Mood: ${moodScore.name}")
        Slider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            onValueChangeFinished = {
                onMoodSelected(moodScore)
            },
            valueRange = 1f..5f,
            steps = 3
        )
    }
}

@Composable
fun DropdownPriority(selectedPriority: Priority, onPrioritySelected: (Priority) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(selectedPriority.name) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Priority:", modifier = Modifier.padding(end = 8.dp))
        TextButton(onClick = { expanded = true }) {
            Text(selectedText)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Priority.values().forEach { priority ->
                DropdownMenuItem(text = {
                    Text(priority.name)
                }, onClick = {
                    selectedText = priority.name
                    onPrioritySelected(priority)
                    expanded = false
                })
            }
        }
    }
}


@Composable
fun DropdownStatus(selectedStatus: Status, onStatusSelected: (Status) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(selectedStatus.name) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Status:", modifier = Modifier.padding(end = 8.dp))
        TextButton(onClick = { expanded = true }) {
            Text(selectedText)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Status.values().forEach { status ->
                DropdownMenuItem(text = {
                    Text(status.name)
                }, onClick = {
                    selectedText = status.name
                    onStatusSelected(status)
                    expanded = false
                })
            }
        }
    }
}


fun getMoodFromSliderPosition(position: Int): MoodScore {
    return when (position) {
        1 -> MoodScore.UPSET
        2 -> MoodScore.SAD
        3 -> MoodScore.NEUTRAL
        4 -> MoodScore.HAPPY
        5 -> MoodScore.EXCELLENT
        else -> MoodScore.NEUTRAL
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumberInputField(value: Double, onValueChange: (Double) -> Unit, label: String, width: Dp) {
    TextField(
        value = value.toString(),
        onValueChange = { onValueChange(it.toDoubleOrNull() ?: 0.0) },
        label = { Text(label) },
        modifier = Modifier
            .width(width)
            .padding(4.dp),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateInput(label: String, date: Date?, onDateChanged: (Date) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    date?.let { calendar.time = it }

    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val dateString = if (date != null) dateFormat.format(date) else "Select Date"

    TextField(
        value = dateString,
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }) {
                DatePickerDialog(
                    context,
                    { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                        calendar.set(year, month, dayOfMonth)
                        onDateChanged(calendar.time)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
    )
}

@Composable
fun RepeatFrequencySelector(selectedFrequency: RepeatFrequency, onFrequencySelected: (RepeatFrequency) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(selectedFrequency.displayName) }

    Column {
        TextButton(onClick = { expanded = true }) {
            Text(selectedText)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            RepeatFrequency.values().forEach { frequency ->
                DropdownMenuItem(text = {
                    Text(frequency.displayName)
                }, onClick = {
                    selectedText = frequency.displayName
                    onFrequencySelected(frequency)
                    expanded = false
                })
            }
        }
    }
}

@Composable
fun BannerImagePicker(imageBitmap: Bitmap?, onImageCaptured: (Bitmap) -> Unit) {
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
            .fillMaxWidth()
            .height(200.dp)
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

fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}


fun validateTodoInput(
    title: String,
    description: String
): Boolean {
    if (title.isBlank()) return false
    if (description.isBlank()) return false
    // More tbd
    return true
}
