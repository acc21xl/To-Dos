package com.example.todo.screens

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.todo.enums.Priority
import com.example.todo.enums.Status
import com.example.todo.MyNotification
import com.example.todo.entities.TagEntity
import com.example.todo.entities.TodoEntity
import com.example.todo.enums.PermissionStatus
import com.example.todo.viewmodels.TodosViewModel
import java.io.ByteArrayOutputStream
import java.util.Date
import java.io.ByteArrayInputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoForm(
    viewModel: TodosViewModel,
    onClose: () -> Unit,
    existingTodo: TodoEntity? = null,
    predefinedTitle: String = "",
    predefinedDescription: String = ""
) {
    // A form to create or update tasks with details like title, description, tags,
    // priority, and location
    // Includes validation and permission checks for alarm and notifications

    // Assign fields based on whether there is an existing Task supplied or not i.e. if
    // we are doing and update or a create
    var title by remember { mutableStateOf(existingTodo?.title ?: predefinedTitle) }
    var description by remember { mutableStateOf(existingTodo?.description
        ?: predefinedDescription) }
    val existingTags by viewModel.getTagsForTodo(existingTodo?.id?.toLong() ?: -1)
        .collectAsState(initial = emptyList())
    val selectedTags = remember { mutableStateListOf<TagEntity>() }
    LaunchedEffect(existingTags) {
        selectedTags.clear()
        selectedTags.addAll(existingTags)
    }
    var priority by remember { mutableStateOf( existingTodo?.priority ?: Priority.LOW) }
    var latitude by remember { mutableDoubleStateOf( existingTodo?.latitude ?: 0.0) }
    var longitude by remember { mutableDoubleStateOf( existingTodo?.longitude ?: 0.0) }
    var toCompleteByDate by remember { mutableStateOf(existingTodo?.toCompleteByDate
        ?: Date()) }
    var imageBytes by remember { mutableStateOf(existingTodo?.imageBytes) }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showNotificationPermissionDialog by remember { mutableStateOf(false) }
    var showAlarmPermissionDialog by remember { mutableStateOf(false) }

    LaunchedEffect(showNotificationPermissionDialog, showAlarmPermissionDialog) {
        Log.d("TodoForm", "Dialog states changed. Notification: " +
                "$showNotificationPermissionDialog, Alarm: $showAlarmPermissionDialog")
    }

    LaunchedEffect(existingTodo) {
        imageBitmap = existingTodo?.imageBytes?.let { byteArrayToBitmap(it) }
    }
    val context = LocalContext.current

    // Check Alarm and Notification permissions and return the status
    fun checkPermissions(): PermissionStatus {
        val hasAlarmPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }

        val hasNotificationPermission =
            NotificationManagerCompat.from(context).areNotificationsEnabled()

        return when {
            hasAlarmPermission && hasNotificationPermission -> PermissionStatus.BOTH_GRANTED
            !hasAlarmPermission && !hasNotificationPermission -> PermissionStatus.BOTH_DENIED
            !hasAlarmPermission -> PermissionStatus.ALARM_DENIED
            else -> PermissionStatus.NOTIFICATION_DENIED
        }
    }

    fun navigateToExactAlarmPermissionSettings() {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
        } else {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        }
        intent.data = Uri.fromParts("package", context.packageName, null)
        context.startActivity(intent)
    }

    fun navigateToNotificationSettings(context: Context) {
        val intent = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                // For Android Oreo and above, use the official Settings action and extra
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                }
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                // For Android Lollipop through Nougat, use a custom action and extras
                Intent("android.settings.APP_NOTIFICATION_SETTINGS").apply {
                    putExtra("app_package", context.packageName)
                    putExtra("app_uid", context.applicationInfo.uid)
                }
            }
            else -> {
                // Fallback for Android versions below Lollipop
                Intent(Settings.ACTION_SETTINGS)
            }
        }
        context.startActivity(intent)
    }

    // Submit the data and close the form
    fun submitTodoAndNavigateAway() {
        val newTodo = TodoEntity(
            id = existingTodo?.id ?: 0,
            title = title,
            description = description,
            dogId = 0, // Assigned on submit
            moodScore = null,
            priority = priority,
            status = Status.ONGOING,
            latitude = latitude,
            longitude = longitude,
            isCompleted = false,
            toCompleteByDate = toCompleteByDate,
            creationDate = Date(),
            completionDate = null,
            deleted = false,
            imageBytes = imageBytes
        )
        if (validateTodoInput(title, description)) {
            if (existingTodo != null) {
                viewModel.updateTodo(newTodo, selectedTags)
            } else {
                viewModel.submitTodo(newTodo, selectedTags)
            }
        }

        onClose()
    }

    // Form for creating / updating todos
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
            DatePicker(
                context = context,
                selectedDateTime = toCompleteByDate,
                onDateTimeSelected = { newDate ->
                    toCompleteByDate = newDate
                }
            )
            Spacer(Modifier.height(8.dp))

            TagInput(selectedTags)
            Spacer(Modifier.height(8.dp))


            // Dropdowns or selectors for Priority
            DropdownPriority(priority) { priority = it }

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
                        when (checkPermissions()) {
                            PermissionStatus.BOTH_GRANTED -> {
                                submitTodoAndNavigateAway()
                                // Permissions granted, proceed with notification logic
                                val myNotification = MyNotification(context, title, description)
                                val time = toCompleteByDate.time
                                myNotification.scheduleNotification(timeInMillis = time)
                            }
                            PermissionStatus.ALARM_DENIED -> {
                                // Alarm permission denied, guide user to alarm permission settings
                                // navigateToExactAlarmPermissionSettings()
                                Log.d("TodoForm", "Alarm permission denied. Showing dialog.")
                                showAlarmPermissionDialog = true
                                Log.d("TodoForm","Alarm Dialog: " +
                                        "$showAlarmPermissionDialog, Notification Dialog: " +
                                        "$showNotificationPermissionDialog")
                            }
                            PermissionStatus.NOTIFICATION_DENIED -> {
                                // Notification permission denied, guide user to notification settings
                                Log.d("TodoForm", "Notification permission denied. " +
                                        "Showing dialog.")
                                showNotificationPermissionDialog = true
                                Log.d("TodoForm","Alarm Dialog: " +
                                        "$showAlarmPermissionDialog, " +
                                        "Notification Dialog: $showNotificationPermissionDialog")

                            }
                            PermissionStatus.BOTH_DENIED -> {
                                // Both permissions denied, guide user to app settings
                                showNotificationPermissionDialog = true
                            }

                        }
                    } else {
                        // Show error message for invalid input
                        Log.d("TodoForm", "Input validation failed.")
                    }
                })
                {
                    Text("Submit Todo")
                }
                if (showNotificationPermissionDialog) {
                    Log.d("TodoForm", "Displaying notification permission dialog.")
                    AlertDialog(
                        onDismissRequest = { showNotificationPermissionDialog = false },
                        title = { Text("Notification Permission Required") },
                        text = { Text("This app requires notification permission to " +
                                "function properly. Please enable them in the app settings.") },
                        confirmButton = {
                            Button(
                                onClick = {
                                    showNotificationPermissionDialog = false
                                    navigateToNotificationSettings(context)
                                }) {
                                Text("Go to Settings")
                            }
                        },
                        dismissButton = {
                            Button(
                                onClick = {
                                    showNotificationPermissionDialog = false
                                }) {
                                Text("Cancel")
                            }
                        }
                    )
                }


                if (showAlarmPermissionDialog) {
                    Log.d("TodoForm", "Displaying alarm permission dialog.")
                    AlertDialog(
                        onDismissRequest = {
                            // The user closes the dialogue box by clicking on the external area
                            showAlarmPermissionDialog = false
                        },
                        title = { Text("Alarm Permission Required") },
                        text = { Text("This app needs alarm permission to continue. " +
                                "Would you like to open settings to enable alarm permission?") },
                        confirmButton = {
                            Button(
                                onClick = {
                                    showAlarmPermissionDialog = false
                                    // Guide the user to the alarm clock permission settings
                                    navigateToExactAlarmPermissionSettings()
                                }) {
                                Text("Go to Settings")
                            }
                        },
                        dismissButton = {
                            Button(
                                onClick = {
                                    // User chooses not to enable alarm privileges
                                    showAlarmPermissionDialog = false
                                }) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }
        }

    }


}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TagInput(selectedTags: MutableList<TagEntity>) {
    // Allows users to add tags to a task
    // Displays tags as chips which can be removed

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
    // A tag as a removable chip within the TagInput component

    Row(modifier = Modifier.padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(text = tag, modifier = Modifier.padding(8.dp))
        IconButton(onClick = onRemove) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Remove tag")
        }
    }
}

@Composable
fun DropdownPriority(selectedPriority: Priority, onPrioritySelected: (Priority) -> Unit) {
    // Dropdown for selecting the priority of a task
    // Displays available priority options from enum

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumberInputField(value: Double, onValueChange: (Double) -> Unit, label: String, width: Dp) {
    // A text field for numerical input, used for latitude and longitude
    // Number only keyboard prevents invalid inputs

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

@Composable
fun BannerImagePicker(imageBitmap: Bitmap?, onImageCaptured: (Bitmap) -> Unit) {
    // Allows users to capture an image
    // Checks camera permission before opening the camera.

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

fun bitmapToByteArray(bitmap: Bitmap?): ByteArray {
    // Converts a Bitmap to a ByteArray for storage

    val stream = ByteArrayOutputStream()
    bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}

fun byteArrayToBitmap(byteArray: ByteArray): Bitmap? {
    // Converts a ByteArray back into a Bitmap for display

    val inputStream = ByteArrayInputStream(byteArray)
    return BitmapFactory.decodeStream(inputStream)
}

fun isInputSafe(input: String): Boolean {
    // Ensures a given string input does not contain SQL injection patterns

    val disallowedPatterns = listOf(
        "';", "--", "/*", "*/", "@@", "@","\"", "\'"
    )
    return disallowedPatterns.none { pattern ->
        input.contains(pattern, ignoreCase = true)
    }
}

fun validateTodoInput(title: String, description: String): Boolean {
    // Validates the input fields for a task form, ensuring required fields are filled and safe
    // from SQL injection

    if (title.isBlank() || !isInputSafe(title)) return false
    if (description.isBlank() || !isInputSafe(description)) return false
    return true
}



