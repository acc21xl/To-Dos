package com.example.todo.viewmodels

import android.app.Application
import android.location.Location
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(manifest=Config.NONE)
class LocationViewModelTest {

    @Mock
    private lateinit var application: Application

    private lateinit var locationViewModel: LocationViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        locationViewModel = LocationViewModel(application)
    }

    @Test
    fun testUpdateLocation() {
        val newLocation = Location("").apply {
            latitude = 10.0
            longitude = 20.0
        }

        locationViewModel.updateLocation(newLocation)

        assertEquals(10.0, locationViewModel.latitude)
        assertEquals(20.0, locationViewModel.longitude)
        assertTrue(locationViewModel.valid())
    }

    @Test
    fun testInvalidateLocation() {
        locationViewModel.invalidate()

        assertEquals(null, locationViewModel.latitude)
        assertEquals(null, locationViewModel.longitude)
        assertFalse(locationViewModel.valid())
    }

    @Test
    fun testCalculateDistance() {
        val distance = locationViewModel.calculateDistance(10.0, 20.0, 10.1, 20.1)
        assertTrue { distance != null && distance > 0 }
    }

}
