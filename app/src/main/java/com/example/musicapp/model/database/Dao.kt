package com.example.musicapp.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.musicapp.model.entity.Music
import kotlinx.coroutines.flow.Flow


@Dao
interface UserDao {

    @Insert
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM user WHERE userId = :userId")
    suspend fun getUserById(userId: Long): User?

    @Query("SELECT * FROM user WHERE username = :username AND password = :password LIMIT 1")
    suspend fun login(username: String, password: String): User?

    @Query("SELECT * FROM user WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM user WHERE userId = :userId")
    fun getUserWithPlaylists(userId: Long): Flow<UserWithPlaylists?>

    // 获取默认用户(0,0)
    @Query("SELECT * FROM user WHERE username = '0' AND password = '0' LIMIT 1")
    suspend fun getDefaultUser(): User?
    @Query("SELECT * FROM user")
    suspend fun getAllUsers(): List<User>
}

@Dao
interface PlaylistDao {

    @Insert
    suspend fun insertPlaylist(playlist: Playlist)

    @Update
    suspend fun updatePlaylist(playlist: Playlist)

    @Query("DELETE FROM playlist WHERE playlistId = :playlistId")
    suspend fun deletePlaylist(playlistId: Long)

    @Query("SELECT * FROM playlist WHERE userId = :userId")
    fun getPlaylistsByUserId(userId: Long): Flow<List<Playlist>>

    @Query("SELECT * FROM playlist WHERE playlistId = :playlistId")
    suspend fun getPlaylistById(playlistId: Long): Playlist?

    // 歌单与歌曲关联操作
    @Insert
    suspend fun addMusicToPlaylist(crossRef: PlaylistMusicCrossRef)

    @Query("DELETE FROM playlist_music WHERE playlistId = :playlistId AND musicId = :musicId")
    suspend fun removeMusicFromPlaylist(playlistId: Long, musicId: Long)

    @Query("SELECT * FROM playlist WHERE playlistId = :playlistId")
    fun getPlaylistWithMusics(playlistId: Long): Flow<PlaylistWithMusics?>

    @Query("DELETE FROM playlist_music WHERE playlistId = :playlistId")
    suspend fun clearPlaylist(playlistId: Long)
}

@Dao
interface MusicDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMusic(music: Music) : Long

    @Insert
    suspend fun insertMusics(musics: List<Music>)

    @Update
    suspend fun updateMusic(music: Music)

    @Query("DELETE FROM Music WHERE id = :musicId")
    suspend fun deleteMusic(musicId: Long)

    @Query("SELECT * FROM Music WHERE id = :musicId")
    suspend fun getMusicById(musicId: Long): Music?

    @Query("SELECT * FROM Music")
    fun getAllMusics(): Flow<List<Music>>
}