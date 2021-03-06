package com.yurakolesnikov.newsapp.utils

class Constants {

    companion object {
        const val API_KEY = "39d148bb5938428fa6f6c8073a863a93"
        const val BASE_URL = "https://newsapi.org"
        const val SEARCH_NEWS_TIME_DELAY = 500L
        const val QUERY_PAGE_SIZE = 20
    }
}

enum class Mode {
    NIGHT,
    DAY
}