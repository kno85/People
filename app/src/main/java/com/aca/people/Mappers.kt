package com.aca.people

import com.aca.people.domain.User
import com.aca.people.domain.UserStreet
import com.aca.people.network.UserCoordinates
import com.aca.people.network.UserLocation
import com.aca.people.network.UserLogin
import com.aca.people.network.UserName
import com.aca.people.network.UserProfilePicture
import com.aca.people.network.UserRegistered
import com.aca.people.network.UserTimezone
import com.aca.people.domain.UserLocation as UserLocationDomain
import com.aca.people.network.UserStreet as UserStreetNetwork


 fun mapToDomain(results: List<com.aca.people.network.User>?): List<User?> {
    if (results != null) {
        return results.map { user ->
            User(
                user.gender,
                mapToDomainName(user.name),
                mapToDomainLocation(user.location),
                user.email,
                mapToDomainLogin(user.login),
                mapToDomainRegistered(user.registered),
                user.phone,
                user.cell,
                mapToDomainPicture(user.picture),
                user.nat
            )
        }
    } else return emptyList()
}

private fun mapToDomainPicture(userProfilePicture: UserProfilePicture?): com.aca.people.domain.UserProfilePicture {
    return com.aca.people.domain.UserProfilePicture(
        userProfilePicture?.large,
        userProfilePicture?.medium,
        userProfilePicture?.thumbnail
    )
}

private fun mapToDomainRegistered(userRegistered: UserRegistered?): com.aca.people.domain.UserRegistered {
    return (com.aca.people.domain.UserRegistered(userRegistered?.date, userRegistered?.age))
}

private fun mapToDomainLogin(userLogin: UserLogin?): com.aca.people.domain.UserLogin {
    return com.aca.people.domain.UserLogin(
        userLogin?.uuid,
        userLogin?.username,
        userLogin?.password,
        userLogin?.salt,
        userLogin?.md5,
        userLogin?.sha1,
        userLogin?.sha256,
    )
}

private fun mapToDomainLocation(userLocation: UserLocation?): UserLocationDomain {
    return UserLocationDomain(
        mapToDomainStreet(userLocation?.street),
        userLocation?.city,
        userLocation?.state,
        userLocation?.country,
        userLocation?.postcode,
        mapToDomainCoordintes(userLocation?.coordinates),
        mapToDomainTimezone(userLocation?.timezone)
    )
}

private fun mapToDomainTimezone(timezone: UserTimezone?): com.aca.people.domain.UserTimezone? {
    return com.aca.people.domain.UserTimezone(timezone?.offset, timezone?.description)
}

private fun mapToDomainCoordintes(coordinates: UserCoordinates?): com.aca.people.domain.UserCoordinates {
    return com.aca.people.domain.UserCoordinates(coordinates?.latitude, coordinates?.longitude)
}

private fun mapToDomainStreet(street: UserStreetNetwork?): UserStreet {
    return UserStreet(street?.number, street?.name)
}

private fun mapToDomainName(userName: UserName?): com.aca.people.domain.UserName {
    return com.aca.people.domain.UserName(userName?.title, userName?.first, userName?.last)
}
