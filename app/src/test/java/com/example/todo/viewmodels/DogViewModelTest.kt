package com.example.todo.viewmodels

import androidx.compose.ui.graphics.Color
import com.example.todo.data.DogDAO
import com.example.todo.data.TodoDAO
import com.example.todo.entities.DogEntity
import com.example.todo.entities.TodoEntity
import com.example.todo.enums.Priority
import com.example.todo.enums.Status
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import java.util.Date
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class DogViewModelTest {

    private lateinit var dogViewModel: DogViewModel
    private val dogDAO: DogDAO = mockk(relaxed = true)
    private val todoDAO: TodoDAO = mockk(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        val sampleDog = DogEntity(
            id = 1,
            name = "Buddy",
            imageBytes = null,
            breed = "Labrador",
            birthdayDate = Date(),
            notes = "Friendly dog",
            deleted = false
        )

        coEvery { dogDAO.getAllDogs() } returns flowOf(listOf(sampleDog))
        coEvery { todoDAO.getCompletedTodos() } returns flowOf(emptyList())

        dogViewModel = DogViewModel(dogDAO, todoDAO)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset main dispatcher to the original Main dispatcher
    }

    @Test
    fun `createDog invokes DAO insert method`() = runTest {
        val newDog = DogEntity(
            name = "Max",
            imageBytes = null,
            breed = "Golden Retriever",
            birthdayDate = Date(),
            notes = "Playful",
            deleted = false
        )

        dogViewModel.createDog(newDog)

        coVerify { dogDAO.insert(newDog) }
    }

    @Test
    fun `updateDog invokes DAO update method`() = runTest {
        val updatedDog = dogViewModel.dog.value!!.copy(name = "Max Updated")

        dogViewModel.updateDog(updatedDog)

        coVerify { dogDAO.update(updatedDog) }
    }

    @Test
    fun `deleteDog invokes DAO delete method`() = runTest {
        val dogToDelete = dogViewModel.dog.value!!

        dogViewModel.deleteDog(dogToDelete)

        coVerify { dogDAO.delete(dogToDelete) }
    }

    @Test
    fun `loadDog sets dog value correctly`() = runTest {
        val sampleDog = DogEntity(
            name = "Max",
            imageBytes = null,
            breed = "Golden Retriever",
            birthdayDate = Date(),
            notes = "Playful",
            deleted = false
        )
        coEvery { dogDAO.getDog() } returns sampleDog

        dogViewModel.loadDog()
        advanceUntilIdle()

        assertEquals(sampleDog, dogViewModel.dog.value)
    }

    @Test
    fun `loadCompletedTasks loads tasks correctly`() = runTest {
        val sampleTasks = listOf(
            TodoEntity(
            id = 1,
            title = "Task",
            description = "This is a sample description",
            priority = Priority.LOW,
            imageBytes = null,
            latitude = null,
            longitude = null,
            status = Status.PENDING,
            creationDate = Date(),
            isCompleted = false,
            toCompleteByDate = Date(),
            completionDate = null,
            dogId = 1,
            moodScore = 5,
            deleted = false
        )
        )
        coEvery { todoDAO.getCompletedTodos() } returns flowOf(sampleTasks)

        dogViewModel.loadCompletedTasks()
        advanceUntilIdle()

        assertEquals(sampleTasks, dogViewModel.completedTasks.value)
    }

    @Test
    fun `updateRecentMoodScores updates recentMoodScores correctly`() = runTest {
        val sampleTasks = listOf(
            TodoEntity(id = 1,
                title = "Task",
                description = "This is a sample description",
                priority = Priority.LOW,
                imageBytes = null,
                latitude = null,
                longitude = null,
                status = Status.PENDING,
                creationDate = Date(),
                isCompleted = false,
                toCompleteByDate = Date(),
                completionDate = null,
                dogId = 1,
                moodScore = 3,
                deleted = false),
            TodoEntity(id = 2,
                title = "Task",
                description = "This is a sample description",
                priority = Priority.LOW,
                imageBytes = null,
                latitude = null,
                longitude = null,
                status = Status.PENDING,
                creationDate = Date(),
                isCompleted = false,
                toCompleteByDate = Date(),
                completionDate = null,
                dogId = 1,
                moodScore = 5,
                deleted = false)
        )
        dogViewModel.updateRecentMoodScores(sampleTasks)

        assertEquals(listOf(3, 5), dogViewModel.recentMoodScores.value)
    }

    @Test
    fun `getMoodColor returns correct color for mood score`() {
        val color = dogViewModel.getMoodColor(4.0) // Example for a 'Happy' mood
        assertEquals(Color(0xFF90EE90), color) // Expected color for 'Happy'
    }

    @Test
    fun `getMoodText returns correct text for mood score`() {
        val text = dogViewModel.getMoodText(2.5) // Example for a 'Neutral' mood
        assertEquals("Neutral", text)
    }
}

