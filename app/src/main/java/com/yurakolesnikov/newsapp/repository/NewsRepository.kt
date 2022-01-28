package com.yurakolesnikov.newsapp.repository

import com.yurakolesnikov.newsapp.api.RetrofitInstance
import com.yurakolesnikov.newsapp.db.ArticleDatabase
import com.yurakolesnikov.newsapp.models.Article

class NewsRepository(
    val db: ArticleDatabase
) {
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)

    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.searchForNews(searchQuery, pageNumber )

    suspend fun upsert(article: Article) = db.getDao().upsert(article)

    fun getSavednews() = db.getDao().getAllArticles()

    suspend fun deleteArticle(article: Article) = db.getDao().deleteArticle(article)
}