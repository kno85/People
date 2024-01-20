package com.aca.people.domain

import java.io.Serializable

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
):Serializable{
    fun doesMatchSearchQuery(query: String):Boolean {
        val matchingCombinations = listOf(
            "${name?.first}${name?.last}",
            "${name?.first} ${name?.last}",
            "${email}")
        return matchingCombinations.any() {
            it.contains(query, ignoreCase = true)
        }
    }
}

data class UserName(
    val title: String?="",
    val first: String?="",
    val last: String?=""
):Serializable

data class UserLocation(
    val street: UserStreet?,
    val city: String?="",
    val state: String?="",
    val country: String?="",
    val postcode: String?="0",
    val coordinates: UserCoordinates?,
    val timezone: UserTimezone?
):Serializable

data class UserStreet(
    val number: String?="",
    val name: String?=""
):Serializable

data class UserCoordinates(
    val latitude: String?="",
    val longitude: String?=""
):Serializable

data class UserTimezone(
    val offset: String?="",
    val description: String?=""
):Serializable

data class UserLogin(
    val uuid: String?="",
    val username: String?="",
    val password: String?="",
    val salt: String?="",
    val md5: String?="",
    val sha1: String?="",
    val sha256: String?=""
):Serializable

data class UserRegistered(
    val date: String?="",
    val age: String?=""
):Serializable


data class UserProfilePicture(
    val large: String?="",
    val medium: String?="",
    val thumbnail: String?=""
):Serializable
