package com.example.todo.viewmodels

import com.example.todo.data.DogDAO
import com.example.todo.data.TodoDAO
import com.example.todo.entities.DogEntity
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
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

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

    // Test that loadDog loads the dog from the DAO
    @Test
    fun `loadDog loads dog on init`() = runTest {
        val dog = dogViewModel.dog.value
        assertNotNull(dog, "Dog should not be null")
        assertEquals("Buddy", dog?.name, "Loaded dog's name should be Buddy")
        assertTrue(dogViewModel.completedTasks.value.isEmpty(), "Initial completed tasks should be empty")
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

}

