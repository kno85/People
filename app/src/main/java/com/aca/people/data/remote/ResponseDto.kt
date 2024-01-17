package com.aca.people.data.remote

import com.google.gson.annotations.SerializedName


class ResponseDto<T : Any?> {
    @SerializedName("results")
    val results: T? = null
}