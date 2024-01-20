package com.aca.people.data.remote

import com.aca.people.network.ApiService
import com.aca.people.network.User
import com.aca.people.utils.Constants
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import retrofit2.Response.success

class ApiClientTest {

    @Mock
    private lateinit var apiService: ApiService

    @InjectMocks
    private lateinit var apiClient: UserRemoteDataSourceImpl

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        apiClient = UserRemoteDataSourceImpl(apiService)
    }

    @Test
    fun `test fetching users success`() = runTest {
        // Given

        val key = Constants.API_KEY
        val page = 1
        val results = 10
        val expectedResponse = success(ResponseDto<List<User>?>())

        `when`(apiService.getUsers(key, page, results)).thenReturn(expectedResponse.body())

        // When
        val response = apiClient.getUsers(key, page)

        // Then
        assertEquals(expectedResponse.body(), response)
    }

}
