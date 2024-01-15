package com.aca.people.domain

data class PeopleDomain(
    val results: List<User>
)

data class User(
    val gender: String,
    val name: UserName,
    val location: UserLocation,
    val email: String,
    val login: UserLogin,
    val dob: UserDOB,
    val registered: UserRegistered,
    val phone: String,
    val cell: String,
    val id: UserId,
    val picture: UserProfilePicture,
    val nat: String
)

data class UserName(
    val title: String,
    val first: String,
    val last: String
)

data class UserLocation(
    val street: UserStreet,
    val city: String,
    val state: String,
    val country: String,
    val postcode: Int,
    val coordinates: UserCoordinates,
    val timezone: UserTimezone
)

data class UserStreet(
    val number: Int,
    val name: String
)

data class UserCoordinates(
    val latitude: String,
    val longitude: String
)

data class UserTimezone(
    val offset: String,
    val description: String
)

data class UserLogin(
    val uuid: String,
    val username: String,
    val password: String,
    val salt: String,
    val md5: String,
    val sha1: String,
    val sha256: String
)

data class UserDOB(
    val date: String,
    val age: Int
)

data class UserRegistered(
    val date: String,
    val age: Int
)

data class UserId(
    val name: String,
    val value: String
)

data class UserProfilePicture(
    val large: String,
    val medium: String,
    val thumbnail: String
)
