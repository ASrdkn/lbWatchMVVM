package com.example.lbwatch.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Item(
    var imdbID: String,
    @Json(name = "Title")
    var title: String,
    @Json(name = "Poster")
    var posterPath: String,
    @Json(name = "Type")
    var overview: String,
    @Json(name = "Year")
    var releaseDate: String? = null,
) {
    fun getReleaseYearFromDate(): String? {
        return releaseDate?.split("-")?.get(0)
    }
}