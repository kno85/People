package com.aca.people.domain

data class User(
    val gender: String?="",
    val name: UserName?,
    val location: UserLocation?,
    val email: String?="",
    val login: UserLogin?,
    val registered: UserRegistered?,
    val phone: String?="",
    val cell: String?="",
    val picture: UserProfilePicture?,
    val nat: String?=""
)

data class UserName(
    val title: String?="",
    val first: String?="",
    val last: String?=""
)

data class UserLocation(
    val street: UserStreet?,
    val city: String?="",
    val state: String?="",
    val country: String?="",
    val postcode: Int?=0,
    val coordinates: UserCoordinates?,
    val timezone: UserTimezone?
)

data class UserStreet(
    val number: Int?=0,
    val name: String?=""
)

data class UserCoordinates(
    val latitude: String?="",
    val longitude: String?=""
)

data class UserTimezone(
    val offset: String?="",
    val description: String?=""
)

data class UserLogin(
    val uuid: String?="",
    val username: String?="",
    val password: String?="",
    val salt: String?="",
    val md5: String?="",
    val sha1: String?="",
    val sha256: String?=""
)

data class UserRegistered(
    val date: String?="",
    val age: Int?=0
)


data class UserProfilePicture(
    val large: String?="",
    val medium: String?="",
    val thumbnail: String?=""
)
