import com.aca.people.data.remote.ResponseDto
import com.aca.people.data.remote.UserRemoteDataSourceImpl
import com.aca.people.data.remote.getMockUserList
import com.aca.people.network.ApiService
import com.aca.people.utils.Constants
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import com.aca.people.data.remote.createMockResponseDto
import com.aca.people.data.remote.createMockHttpErrorResponse
import com.aca.people.network.User
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.io.IOException

@ExtendWith(MockitoExtension::class) // 1. Usar MockitoExtension para inicialización más limpia
class UserRemoteDataSourceImpTest {

    @Mock
    private lateinit var apiService: ApiService


    @InjectMocks
    private lateinit var userRemoteDataSource: UserRemoteDataSourceImpl // 2. Nombre más descriptivo

    // @BeforeEach no es necesario si solo era para MockitoAnnotations.initMocks y la instanciación manual.
    // @InjectMocks se encarga de instanciar userRemoteDataSource e inyectar apiService.


    @Test
    fun `getUsers returns data when api call is successful`() = runTest {
        // Given
        val apiKey = Constants.API_KEY
        val page = 1
        val expectedResultsCount = 10

        // 1. Crea una lista de usuarios de prueba
        val mockUserList = getMockUserList()
// Asegúrate de que tu mockResponseDto es del tipo ResponseDto<List<User>?>
        val mockResponseDto = createMockResponseDto(userList = mockUserList)

// Corrige la llamada a Response.success para que el tipo genérico coincida
        val expectedApiResponse: Response<ResponseDto<List<User>?>?> = Response.success(mockResponseDto)


               `when`(apiService.getUsers(eq(apiKey), anyInt(), anyInt())).thenReturn(expectedApiResponse.body())
// Or with mockito-kotlin:
        whenever(apiService.getUsers(eq(apiKey), anyInt(), anyInt())).thenReturn(expectedApiResponse.body())

        // 5. Llama al método de tu fuente de datos que estás probando
        val actualDataSourceResponse: ResponseDto<List<User>?>? = // El tipo debe coincidir con lo que devuelve el método
            userRemoteDataSource.getUsers(apiKey, page)

// Then
// actualDataSourceResponse ahora SÍ es ResponseDto<List<User>?>?, así que las aserciones deberían funcionar
        assertNotNull(actualDataSourceResponse, "DataSource should return a DTO even on API error.")
        assertNull(
            actualDataSourceResponse.results, // Esto es correcto ahora
            "Results should be null when an API error occurs."
        )
        // 7. Aserciones más específicas para mayor robustez
        assertEquals(mockUserList, actualDataSourceResponse.results)
        assertEquals(expectedResultsCount, actualDataSourceResponse.results?.size)
    }
    @Test
    fun testApiReturns404Error() {
        // 1. Prepara el cuerpo del error (opcional pero bueno para simular un error real)
        val errorJson = "{\"code\": 40410, \"message\": \"User not found in the system\"}"
        val errorResponseBody = errorJson.toResponseBody("application/json".toMediaTypeOrNull())

        // 2. Llama a tu función para crear la respuesta de error simulada.
        //    El tipo genérico <T> se inferirá o se puede especificar.
        //    Aquí, asumimos que el método de ApiService que estamos mockeando
        //    devolvería Response<ResponseDto<List<User>>> en caso de éxito.
        //    Por lo tanto, T se convierte en ResponseDto<List<User>>.
        val mockErrorApiResponse: Response<ResponseDto<List<User>>> =
            createMockHttpErrorResponse(
                statusCode = 404,
                errorBody = errorResponseBody
            )

        // 3. Ahora puedes usar mockErrorApiResponse para simular la respuesta de tu ApiService
        //    Ejemplo con Mockito:
        //    `when`(mockApiService.getUsers(any(), any())).thenReturn(mockErrorApiResponse)

        // Verificaciones de la respuesta de error creada (para asegurarte de que tu helper funciona):
        assert(!mockErrorApiResponse.isSuccessful)
        assertEquals(404, mockErrorApiResponse.code())
        assertNull(mockErrorApiResponse.body()) // El cuerpo de un Response.error() es null
        assertNotNull(mockErrorApiResponse.errorBody())
        assertEquals(errorJson, mockErrorApiResponse.errorBody()?.string()) // .string() consume el body

        println("Respuesta de error simulada creada: $mockErrorApiResponse")
        // Necesitas leer errorBody ANTES de que se cierre el stream si quieres volver a acceder a él.
        // Por eso, la segunda vez que se accede a errorBody().string() puede dar error o vacío.
        // Es mejor guardarlo si necesitas múltiples aserciones sobre su contenido.
    }

    @Test
    @DisplayName("getUsers lanza excepción cuando la API lanza una excepción de red/IO")
    fun `getUsers throws exception when api throws network exception`() = runTest {
        // Given
        val testApiKey = "test_api_key" // Using a test-specific key
        val pageNumber = 1
        val expectedResultsCountInApiCall = 10 // More descriptive variable name
        val networkException = IOException("Network error") // Simular error de red

        // Assuming apiService is properly mocked (e.g., using @Mock annotation or manual mock)
        // and that its getUsers method aligns with these parameters.
        // Use named arguments for clarity if `apiService.getUsers` has many parameters.
        `when`(
            apiService.getUsers(
                page = pageNumber, // Assuming 'page' is the correct param name in ApiService
                results = expectedResultsCountInApiCall,
                key = testApiKey // Assuming 'results' is the correct param name
            )
        ).thenThrow(networkException)

        // When & Then
        val thrownException = assertFailsWith<IOException> {
            userRemoteDataSource.getUsers(apiKey = testApiKey, pageNumber = pageNumber)
        }
        assertEquals(networkException.message, thrownException.message)
    }
}