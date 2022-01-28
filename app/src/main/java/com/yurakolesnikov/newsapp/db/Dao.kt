package com.yurakolesnikov.newsapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Dao
import com.yurakolesnikov.newsapp.models.Article

@Dao
interface Dao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article): Long

    @Delete
    suspend fun deleteArticle(article: Article)

    @Query("SELECT * FROM articles")
    fun getAllArticles(): LiveData<List<Article>>
}