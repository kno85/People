package com.aca.people.data.remote

import com.aca.people.network.User
import com.aca.people.network.UserCoordinates
import com.aca.people.network.UserLocation
import com.aca.people.network.UserLogin
import com.aca.people.network.UserName
import com.aca.people.network.UserProfilePicture
import com.aca.people.network.UserRegistered
import com.aca.people.network.UserStreet
import com.aca.people.network.UserTimezone
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

// In a test utility class or companion object
fun <T> createMockHttpErrorResponse(
    statusCode: Int,
    errorBody: ResponseBody
): Response<T> { // Or your ResponseDto<T> if applicable
    return Response.error(statusCode, errorBody)
}

// Usage:
val errorResponseBody = "{\"error\":\"Not Found\"}"
    .toResponseBody("application/json".toMediaTypeOrNull())

val mockHttpErrorResponse: Response<List<User>?> = // Or ResponseDto<List<User>?>
    createMockHttpErrorResponse(404, errorResponseBody)
fun createMockResponseDto(
    userList: List<User> = getMockUserList()
): ResponseDto<List<User>?> {
    // Crea la instancia de ResponseDto
    val response = ResponseDto<List<User>?>() // <-- Y aquí

    // Asigna la lista de usuarios al campo 'results'
    response.results = userList

    return response
}
fun getMockUserList(): List<User> {
    return listOf(
        User(
            gender = "female",
            name = UserName(title = "Ms", first = "Alice", last = "Smith"),
            location = UserLocation(
                street = UserStreet(number = "123", name = "Main St"),
                city = "Anytown",
                state = "Stateville",
                country = "USA",
                postcode = "12345",
                coordinates = UserCoordinates(latitude = "40.7128", longitude = "-74.0060"),
                timezone = UserTimezone(offset = "-5:00", description = "Eastern Time")
            ),
            email = "alice.smith@example.com",
            login = UserLogin(
                uuid = "a1b2c3d4-e5f6-7890-1234-567890abcdef",
                username = "alicesmith",
                password = "password123",
                salt = "randomsalt1",
                md5 = "md5hash1",
                sha1 = "sha1hash1",
                sha256 = "sha256hash1"
            ),
            registered = UserRegistered(date = "2020-01-15T10:30:00Z", age = "30"),
            phone = "555-0101",
            cell = "555-0102",
            picture = UserProfilePicture(
                large = "https://example.com/alice_large.jpg",
                medium = "https://example.com/alice_medium.jpg",
                thumbnail = "https://example.com/alice_thumbnail.jpg"
            ),
            nat = "US"
        ),
        User(
            gender = "male",
            name = UserName(title = "Mr", first = "Bob", last = "Johnson"),
            location = UserLocation(
                street = UserStreet(number = "456", name = "Oak Ave"),
                city = "Otherville",
                state = "Province A",
                country = "Canada",
                postcode = "A1B 2C3",
                coordinates = UserCoordinates(latitude = "45.4215", longitude = "-75.6972"),
                timezone = UserTimezone(offset = "-4:00", description = "Atlantic Time")
            ),
            email = "bob.johnson@example.ca",
            login = UserLogin(
                uuid = "b2c3d4e5-f6a7-8901-2345-678901bcdef0",
                username = "bobjohnson",
                password = "securepassword",
                salt = "randomsalt2",
                md5 = "md5hash2",
                sha1 = "sha1hash2",
                sha256 = "sha256hash2"
            ),
            registered = UserRegistered(date = "2018-07-22T14:00:00Z", age = "42"),
            phone = "555-0201",
            cell = "555-0202",
            picture = UserProfilePicture(
                large = "https://example.com/bob_large.jpg",
                medium = "https://example.com/bob_medium.jpg",
                thumbnail = "https://example.com/bob_thumbnail.jpg"
            ),
            nat = "CA"
        ),
        User(
            gender = "female",
            name = UserName(title = "Dr", first = "Carol", last = "Williams"),
            location = UserLocation(
                street = UserStreet(number = "789", name = "Pine Ln"),
                city = "Testburg",
                state = "Testland",
                country = "UK",
                postcode = "SW1A 1AA",
                coordinates = UserCoordinates(latitude = "51.5074", longitude = "0.1278"),
                timezone = UserTimezone(offset = "+1:00", description = "British Summer Time")
            ),
            email = "carol.williams@example.co.uk",
            login = UserLogin(
                uuid = "c3d4e5f6-a7b8-9012-3456-789012cdef01",
                username = "carolwilliams",
                password = "userpass",
                salt = "randomsalt3",
                md5 = "md5hash3",
                sha1 = "sha1hash3",
                sha256 = "sha256hash3"
            ),
            registered = UserRegistered(date = "2022-11-01T09:15:00Z", age = "28"),
            phone = "555-0301",
            cell = "555-0302",
            picture = UserProfilePicture(
                large = "https://example.com/carol_large.jpg",
                medium = "https://example.com/carol_medium.jpg",
                thumbnail = "https://example.com/carol_thumbnail.jpg"
            ),
            nat = "GB"
        )
    )
}
