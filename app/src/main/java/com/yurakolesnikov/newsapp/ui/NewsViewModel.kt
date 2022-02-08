package com.yurakolesnikov.newsapp.ui

import android.app.Activity
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.yurakolesnikov.newsapp.NewsApplication
import com.yurakolesnikov.newsapp.models.Article
import com.yurakolesnikov.newsapp.models.NewsResponse
import com.yurakolesnikov.newsapp.repository.NewsRepository
import com.yurakolesnikov.newsapp.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import java.util.*

class NewsViewModel(
    val newsRepository: NewsRepository,
    app: Application
) : AndroidViewModel(app) {

    var breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1
    var breakingNewsResponse: NewsResponse? = null

    var searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: NewsResponse? = null

    var articleForArticleFragment: Article? = null
    var lastSearchQuery = ""

    var totalResults = 0

    var isTooManyRequests = false

    var previousInternetStateBreakingNews = true
    var previousInternetStateSearchNews = true

    var toastShowTime = 0L

    init {
        getBreakingNews("us")
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        safeBreakingNewsCall(countryCode)
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        safeSearchNewsCall(searchQuery)
    }

    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

    fun getSavedNews() = newsRepository.getSavedNews()

    private suspend fun safeBreakingNewsCall(countryCode: String) {
        breakingNews.postValue(Resource.Loading())
        try {
            previousInternetStateBreakingNews = hasInternetConnection()
            if (hasInternetConnection()) {
                val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
                when {
                    response.isSuccessful ->
                        breakingNews.postValue(handleBreakingNewsResponse(response))
                    response.code() == 429 -> {
                        breakingNews.postValue(Resource.Error("too many requests"))
                        isTooManyRequests = true
                    }
                    !response.isSuccessful ->
                        breakingNews.postValue(Resource.Error(response.message()))
                }
            } else {
                breakingNews.postValue(Resource.Error("no internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> breakingNews.postValue(Resource.Error("network failure"))
                else -> breakingNews.postValue(Resource.Error("conversion error"))
            }
        }
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        breakingNewsPage++
        if (breakingNewsResponse == null) breakingNewsResponse = response.body()
        else breakingNewsResponse!!.articles.addAll(
            (response.body()?.articles) ?: listOf<Article>()
        )
        return Resource.Success(breakingNewsResponse!!)
    }

    private suspend fun safeSearchNewsCall(searchQuery: String) {
        searchNews.postValue(Resource.Loading())
        try {
            previousInternetStateSearchNews = hasInternetConnection()
            if (hasInternetConnection()) {
                val response = newsRepository.searchNews(searchQuery, searchNewsPage)
                when {
                    response.isSuccessful ->
                        searchNews.postValue(handleSearchNewsResponse(response))
                    response.code() == 429 -> {
                        searchNews.postValue(Resource.Error("too many requests"))
                        isTooManyRequests = true
                    }
                    !response.isSuccessful ->
                        searchNews.postValue(Resource.Error(response.message()))
                }
            } else {
                searchNews.postValue(Resource.Error("no internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchNews.postValue(Resource.Error("network failure"))
                else -> searchNews.postValue(Resource.Error("conversion error"))
            }
        }
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        searchNewsPage++
        if (searchNewsResponse == null) searchNewsResponse = response.body()
        else searchNewsResponse!!.articles.addAll(
            (response.body()?.articles) ?: listOf<Article>()
        )
        return Resource.Success(searchNewsResponse!!)
    }

    fun hasInternetConnection(): Boolean {
        val connectivityManager =
            getApplication<NewsApplication>().getSystemService(Context.CONNECTIVITY_SERVICE)
                    as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

    fun showSafeToast(activity: Activity, text: String) {
        if (Calendar.getInstance().timeInMillis >= toastShowTime + 4000L) {
            Toast.makeText(activity, "An error occurred: $text", Toast.LENGTH_LONG).show()
            toastShowTime = Calendar.getInstance().timeInMillis
        }
    }
}