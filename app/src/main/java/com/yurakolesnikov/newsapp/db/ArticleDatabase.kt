package com.yurakolesnikov.newsapp.db

import android.content.Context
import androidx.room.*
import com.yurakolesnikov.newsapp.models.Article
import com.yurakolesnikov.newsapp.db.Dao

@Database(
    entities = [Article::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class) // Transform custom object Source to primitive type
abstract class ArticleDatabase : RoomDatabase() {

    abstract fun getDao(): Dao

    companion object {
        @Volatile // To prevent modifying instance from different threads simultaneously
        private var instance: ArticleDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = // Reduce code for database instantiation
            instance ?: synchronized(LOCK) { // Only from one thread per time
                // Double check for instance, cuz another thread may already create it
                instance ?: createDatabase(context).also { instance = it }
            }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                ArticleDatabase::class.java,
                "article_db.db"
            ).build()

    }

}