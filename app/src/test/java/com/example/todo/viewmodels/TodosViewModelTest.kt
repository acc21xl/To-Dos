package com.example.todo.viewmodels

import com.example.todo.data.DogDAO
import com.example.todo.data.TagDAO
import com.example.todo.data.TodoDAO
import com.example.todo.entities.DogEntity
import com.example.todo.entities.TagEntity
import com.example.todo.entities.TodoEntity
import com.example.todo.enums.Priority
import com.example.todo.enums.Status
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.After
import org.junit.Test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import java.util.Date
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class TodosViewModelTest {

    private lateinit var todosViewModel: TodosViewModel
    private val todoDAO: TodoDAO = mockk(relaxed = true)
    private val dogDAO: DogDAO = mockk(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var sampleTodo: TodoEntity
    private lateinit var sampleDog: DogEntity
    private lateinit var sampleTags: List<TagEntity>
    private fun createSampleTodo(id: Int = 1): TodoEntity {
        return TodoEntity(
            id = id,
            title = "Sample Task",
            description = "This is a sample description",
            priority = Priority.HIGH,
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
    }
    private fun createSampleTag(id: Int = 1): TagEntity {
        return TagEntity(
            id = id,
            title = "Sample Tag",
        )
    }


    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher) // Set a test dispatcher
        // Mock initialization
        sampleTodo = createSampleTodo()
        sampleDog = DogEntity(
            id = 1,
            name = "Buddy",
            imageBytes = null,
            breed = "Labrador",
            birthdayDate = Date(),
            notes = "Friendly dog",
            deleted = false
        )
        sampleTags = listOf(createSampleTag())

        coEvery { todoDAO.getAllTodos() } returns flowOf(listOf(sampleTodo))
        coEvery { dogDAO.getAllDogs() } returns flowOf(listOf(sampleDog))
        coEvery { todoDAO.getActiveTodos() } returns flowOf(listOf(sampleTodo))
        coEvery { todoDAO.getCompletedTodos() } returns flowOf(emptyList())

        // Instantiate the ViewModel
        todosViewModel = TodosViewModel(todoDAO, dogDAO)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
//        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `initial values are set correctly`() = runTest (testDispatcher) {
        // Assertions for initial state of ViewModel
        assertNotNull(todosViewModel.tasks.value)
        assertNotNull(todosViewModel.activeTasks.value)
        assertNotNull(todosViewModel.completedTasks.value)
    }

    @Test
    fun `loadCompletedTasks loads data correctly`() = runTest {

        coEvery { todoDAO.getCompletedTodos() } returns flowOf(listOf(sampleTodo))

        todosViewModel.loadCompletedTasks()

        assertEquals(listOf(sampleTodo), todosViewModel.completedTasks.value, "Completed tasks should match loaded data")
    }

    @Test
    fun `submitTodo invokes DAO insert method`() = runTest {

        coEvery { todoDAO.insertNewTodoWithTags(sampleTodo, sampleTags) } returns Unit

        todosViewModel.submitTodo(sampleTodo, sampleTags)

        coVerify { todoDAO.insertNewTodoWithTags(sampleTodo, sampleTags) }
    }

    @Test
    fun `updateTodo invokes DAO update method`() = runTest {
        val updatedTodo = TodoEntity(
            id = 1,
            title = "Updated Task",
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

        coEvery { todoDAO.updateTodoWithTags(updatedTodo, sampleTags) } returns Unit

        todosViewModel.updateTodo(updatedTodo, sampleTags)

        coVerify { todoDAO.updateTodoWithTags(updatedTodo, sampleTags) }
    }



}
