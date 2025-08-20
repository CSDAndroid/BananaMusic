package com.example.musicapp.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.musicapp.model.entity.Music

@Database(
    entities = [
        User::class,
        Playlist::class,
        Music::class,
        PlaylistMusicCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun musicDao(): MusicDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "music_database"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // 初始化默认用户(0,0)
                            db.execSQL("INSERT INTO user (username, password) VALUES ('0', '0')")
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
