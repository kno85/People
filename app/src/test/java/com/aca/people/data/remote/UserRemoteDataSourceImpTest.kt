package com.aca.people.data.remote

import com.aca.people.CoroutinesTestExtension
import com.aca.people.data.remote.ResponseDto
import com.aca.people.network.ApiService
import com.aca.people.utils.Constants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockitoExtension::class, CoroutinesTestExtension::class)
class UserRemoteDataSourceImplTest {

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var apiService: ApiService

    @InjectMocks
    private lateinit var userRemoteDataSource: UserRemoteDataSourceImpl

    @Test
    @DisplayName("getUsers should return successful response mapped to ResponseDto")
    fun `getUsers success returns dto`() = runTest(testDispatcher) {
        val testApiKey = "key"
        val pageNumber = 1
        val mockUserList = getMockUserList() // Store the mock list for comparison
        val apiResponse = Response.success(mockUserList)

        whenever(apiService.getUsers(key = testApiKey, page = pageNumber, results = Constants.MAX_PAGE_SIZE))
            .thenReturn(createMockResponseDto(mockUserList))

        val result = userRemoteDataSource.getUsers(apiKey = testApiKey, pageNumber = pageNumber)

        // Assert that the result is a Success case
        assertTrue(result.equals(apiResponse))

        // Assert that the data within the Success object matches the mocked data
        val successResult = apiResponse// Safe cast after the previous assertion
        assertEquals(mockUserList, successResult.body()) // Assuming 'data' is the property holding the user list
    }


    @Test
    @DisplayName("getUsers should throw IOException on network error")
    fun `getUsers throws IOException on network error`() = runTest(testDispatcher) {
        val testApiKey = "test_key"
        val pageNumber = 2
        val networkException = IOException("Network error")

        whenever(apiService.getUsers(key = testApiKey, page = pageNumber, results = com.aca.people.utils.Constants.MAX_PAGE_SIZE))
            .thenThrow(networkException)

        val thrown = assertThrows(IOException::class.java) {
            runTest { userRemoteDataSource.getUsers(apiKey = testApiKey, pageNumber = pageNumber) }
        }
        assertEquals(networkException.message, thrown.message)
    }
}
