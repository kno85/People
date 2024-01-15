package com.aca.people.network
import com.google.gson.annotations.SerializedName


 class Results(
    @SerializedName("results")
    val results: List<User>?
)

 class User(
    @SerializedName("gender")
    val gender: String?,
    @SerializedName("name")
    val name: UserName?,
    @SerializedName("location")
    val location: UserLocation?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("login")
    val login: UserLogin?,
    @SerializedName("registered")
    val registered: UserRegistered?,
    @SerializedName("phone")
    val phone: String?,
    @SerializedName("cell")
    val cell: String?,
    @SerializedName("picture")
    val picture: UserProfilePicture?,
    @SerializedName("nat")
    val nat: String?
)

 class UserName(
    @SerializedName("title")
    val title: String?,
    @SerializedName("first")
    val first: String?,
    @SerializedName("last")
    val last: String?
)

 class UserLocation(
    @SerializedName("street")
    val street: UserStreet?,
    @SerializedName("city")
    val city: String?,
    @SerializedName("state")
    val state: String?,
    @SerializedName("country")
    val country: String?,
    @SerializedName("postcode")
    val postcode: String?,
    @SerializedName("coordinates")
    val coordinates: UserCoordinates?,
    @SerializedName("timezone")
    val timezone: UserTimezone?
)

 class UserStreet(
    @SerializedName("number")
    val number: String?,
    @SerializedName("name")
    val name: String?
)

 class UserCoordinates(
    @SerializedName("latitude")
    val latitude: String?,
    @SerializedName("longitude")
    val longitude: String?
)

 class UserTimezone(
    @SerializedName("offset")
    val offset: String?,
    @SerializedName("description")
    val description: String?
)

 class UserLogin(
    @SerializedName("uuid")
    val uuid: String?,
    @SerializedName("username")
    val username: String?,
    @SerializedName("password")
    val password: String?,
    @SerializedName("salt")
    val salt: String?,
    @SerializedName("md5")
    val md5: String?,
    @SerializedName("sha1")
    val sha1: String?,
    @SerializedName("sha256")
    val sha256: String?
)

 class UserRegistered(
    @SerializedName("date")
    val date: String?,
    @SerializedName("age")
    val age: String?
)

 class UserProfilePicture(
    @SerializedName("large")
    val large: String?,
    @SerializedName("medium")
    val medium: String?,
    @SerializedName("thumbnail")
    val thumbnail: String?
)
