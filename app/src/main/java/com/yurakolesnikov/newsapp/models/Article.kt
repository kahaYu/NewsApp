package com.yurakolesnikov.newsapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yurakolesnikov.newsapp.utils.formatDate
import java.io.Serializable

@Entity(
    tableName = "articles"
)
data class Article(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 1,
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val source: Source?,
    val title: String?,
    val url: String?,
    val urlToImage: String?
) : Serializable {
    var formattedPublishedAt =
        publishedAt?.formatDate() // Make computations here instead of adapter
}