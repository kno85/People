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
import com.aca.people.domain.UserCoordinates as UserCoordinatesDomain
import com.aca.people.domain.UserLocation as UserLocationDomain
import com.aca.people.domain.UserName as UserNameDomain
import com.aca.people.domain.UserProfilePicture as UserProfilePictureDomain
import com.aca.people.domain.UserRegistered as UserRegisteredDomain
import com.aca.people.domain.UserTimezone as UserTimezoneDomain
import com.aca.people.network.User as UserNetwork
import com.aca.people.network.UserStreet as UserStreetNetwork


 fun mapToDomain(results: List<UserNetwork>?): List<User> {
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

private fun mapToDomainPicture(userProfilePicture: UserProfilePicture?): UserProfilePictureDomain {
    return UserProfilePictureDomain(
        userProfilePicture?.large,
        userProfilePicture?.medium,
        userProfilePicture?.thumbnail
    )
}

private fun mapToDomainRegistered(userRegistered: UserRegistered?): UserRegisteredDomain {
    return (UserRegisteredDomain(userRegistered?.date, userRegistered?.age))
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

private fun mapToDomainTimezone(timezone: UserTimezone?): UserTimezoneDomain {
    return UserTimezoneDomain(timezone?.offset, timezone?.description)
}

private fun mapToDomainCoordintes(coordinates: UserCoordinates?): UserCoordinatesDomain {
    return UserCoordinatesDomain(coordinates?.latitude, coordinates?.longitude)
}

private fun mapToDomainStreet(street: UserStreetNetwork?): UserStreet {
    return UserStreet(street?.number, street?.name)
}

private fun mapToDomainName(userName: UserName?): UserNameDomain {
    return UserNameDomain(userName?.title, userName?.first, userName?.last)
}
