package com.aca.people.data.remote

import com.aca.people.CoroutinesTestExtension
import com.aca.people.network.ApiService
import com.aca.people.network.User
import com.aca.people.utils.Constants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import retrofit2.Response

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
        val mockUserList = getMockUserList()
        val apiResponse = Response.success(createMockResponseDto(mockUserList))

        whenever(apiService.getUsers(key = testApiKey, page = pageNumber, results = Constants.MAX_PAGE_SIZE))
            .thenReturn(apiResponse)
        val result = userRemoteDataSource.getUsers(apiKey = testApiKey, pageNumber = pageNumber)

        assertTrue(result.isSuccessful)
        assertEquals(mockUserList, result.body()?.results)
    }
    @Test
    @DisplayName("getUsers should return error response on failure")
    fun `getUsers returns error response on failure`() = runTest(testDispatcher) {
        val testApiKey = "test_key"
        val pageNumber = 2

        val errorResponse = Response.error<ResponseDto<List<User>>>(
            500,
            ResponseBody.create(
                "application/json".toMediaTypeOrNull(),
                "{\"error\":\"Internal Server Error\"}"
            )
        )

        whenever(apiService.getUsers(key = testApiKey, page = pageNumber, results = Constants.MAX_PAGE_SIZE))
            .thenReturn(errorResponse)

        val result = userRemoteDataSource.getUsers(apiKey = testApiKey, pageNumber = pageNumber)

        assertTrue(result.code() == 500)
        assertTrue(result.errorBody() != null)
    }

}
