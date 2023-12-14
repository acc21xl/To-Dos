package com.example.todo.viewmodels

import com.example.todo.data.DogDAO
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
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

    private fun createSampleDog(id: Int = 1): DogEntity {
        return DogEntity(
            id = id,
            name = "Buddy",
            imageBytes = null,
            breed = "Labrador",
            birthdayDate = Date(),
            notes = "Friendly dog",
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
        sampleDog = createSampleDog()
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

    @Test
    fun `loadActiveTasks loads active tasks correctly`() = runTest {
        val activeTask = createSampleTodo().copy(isCompleted = false)
        coEvery { todoDAO.getActiveTodos() } returns flowOf(listOf(activeTask))

        todosViewModel.loadActiveTasks()
        advanceUntilIdle()

        assertEquals(listOf(activeTask), todosViewModel.activeTasks.value)
    }

    @Test
    fun `loadTasks loads all tasks correctly`() = runTest {
        val allTasks = listOf(createSampleTodo(), createSampleTodo(id = 2))
        coEvery { todoDAO.getAllTodos() } returns flowOf(allTasks)

        todosViewModel.loadTasks()
        advanceUntilIdle()

        assertEquals(allTasks, todosViewModel.tasks.value)
    }

    @Test
    fun `loadDog loads dog correctly`() = runTest {
        coEvery { dogDAO.getDog() } returns sampleDog

        todosViewModel.loadDog()
        advanceUntilIdle()

        assertEquals(sampleDog, todosViewModel.dog.value)
    }

    @Test
    fun `updateTask updates a task correctly`() = runTest {
        val updatedTask = createSampleTodo().copy(title = "Updated Task")

        todosViewModel.updateTask(updatedTask)
        advanceUntilIdle()

        coVerify { todoDAO.update(updatedTask) }
    }

    @Test
    fun `deleteTask deletes a task correctly`() = runTest {
        val taskId = sampleTodo.id.toLong()

        todosViewModel.deleteTask(taskId)
        advanceUntilIdle()

        coVerify { todoDAO.deleteTodoTags(taskId) }
        coVerify { todoDAO.deleteTask(taskId) }
    }

    @Test
    fun `getTagsForTodo returns correct tags`() = runTest {
        val todoId = 1L
        coEvery { todoDAO.getTagsForTodo(todoId) } returns flowOf(sampleTags)

        val tagsFlow = todosViewModel.getTagsForTodo(todoId)
        assertEquals(sampleTags, tagsFlow.first(), "Tags should match the expected data")
    }

}
