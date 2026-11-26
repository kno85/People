package com.aca.people.utils

import com.aca.people.BuildConfig

object Constants {
    const val BASE_URL = "https://randomuser.me/"
    const val MAX_PAGE_SIZE = 10
    const val NETWORK_TIMEOUT_SECONDS = 60L
    const val DEFAULT_SEED = "people-default-seed"
    const val API_KEY = BuildConfig.API_KEY
    val SEED: String
        get() = API_KEY.ifBlank { DEFAULT_SEED }
}
