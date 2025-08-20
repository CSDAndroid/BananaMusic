package com.example.musicapp.model.repository

import com.example.musicapp.model.database.MusicDao
import com.example.musicapp.model.database.PlaylistDao
import com.example.musicapp.model.database.UserDao
import com.example.musicapp.model.database.Playlist
import com.example.musicapp.model.database.PlaylistMusicCrossRef
import com.example.musicapp.model.database.PlaylistWithMusics
import com.example.musicapp.model.database.User
import com.example.musicapp.model.database.UserWithPlaylists
import com.example.musicapp.model.entity.Music
import kotlinx.coroutines.flow.Flow

class MusicRepository private constructor(
    private val musicDao: MusicDao
) {
    // 单例实例
    companion object {
        @Volatile
        private var instance: MusicRepository? = null

        fun getInstance(musicDao: MusicDao): MusicRepository {
            return instance ?: synchronized(this) {
                instance ?: MusicRepository(musicDao).also { instance = it }
            }
        }
    }

    suspend fun insertMusic(music: Music) {
        musicDao.insertMusic(music)
    }

    suspend fun insertMusics(musics: List<Music>) {
        musicDao.insertMusics(musics)
    }

    suspend fun updateMusic(music: Music) {
        musicDao.updateMusic(music)
    }

    suspend fun deleteMusic(musicId: Long) {
        musicDao.deleteMusic(musicId)
    }

    suspend fun getMusicById(musicId: Long): Music? {
        return musicDao.getMusicById(musicId)
    }

    fun getAllMusics(): Flow<List<Music>> {
        return musicDao.getAllMusics()
    }
}

// 歌单仓库
class PlaylistRepository private constructor(
    private val playlistDao: PlaylistDao
) {
    // 单例实例
    companion object {
        @Volatile
        private var instance: PlaylistRepository? = null

        fun getInstance(playlistDao: PlaylistDao): PlaylistRepository {
            return instance ?: synchronized(this) {
                instance ?: PlaylistRepository(playlistDao).also { instance = it }
            }
        }
    }

    suspend fun createPlaylist(playlist: Playlist) {
        playlistDao.insertPlaylist(playlist)
    }

    suspend fun updatePlaylist(playlist: Playlist) {
        playlistDao.updatePlaylist(playlist)
    }

    suspend fun deletePlaylist(playlistId: Long) {
        playlistDao.deletePlaylist(playlistId)
    }

    fun getPlaylistsByUserId(userId: Long): Flow<List<Playlist>> {
        return playlistDao.getPlaylistsByUserId(userId)
    }

    suspend fun getPlaylistById(playlistId: Long): Playlist? {
        return playlistDao.getPlaylistById(playlistId)
    }

    suspend fun addMusicToPlaylist(playlistId: Long, musicId: Long) {
        playlistDao.addMusicToPlaylist(
            PlaylistMusicCrossRef(playlistId, musicId)
        )
    }

    suspend fun removeMusicFromPlaylist(playlistId: Long, musicId: Long) {
        playlistDao.removeMusicFromPlaylist(playlistId, musicId)
    }

    fun getPlaylistWithMusics(playlistId: Long): Flow<PlaylistWithMusics?> {
        return playlistDao.getPlaylistWithMusics(playlistId)
    }

    suspend fun clearPlaylist(playlistId: Long) {
        playlistDao.clearPlaylist(playlistId)
    }
}


// 用户仓库
class UserRepository private constructor(
    private val userDao: UserDao
) {
    // 单例实例
    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(userDao: UserDao): UserRepository {
            return instance ?: synchronized(this) {
                instance ?: UserRepository(userDao).also { instance = it }
            }
        }
    }

    suspend fun registerUser(user: User): Boolean {
        // 检查用户名是否已存在
        val existingUser = userDao.getUserByUsername(user.username)
        return if (existingUser == null) {
            userDao.insertUser(user)
            true
        } else {
            false
        }
    }

    suspend fun login(username: String, password: String): User? {
        return userDao.login(username, password)
    }

    suspend fun getUserById(userId: Long): User? {
        return userDao.getUserById(userId)
    }

    fun getUserWithPlaylists(userId: Long): Flow<UserWithPlaylists?> {
        return userDao.getUserWithPlaylists(userId)
    }

    // 获取默认用户(0,0)
    suspend fun getDefaultUser(): User? {
        return userDao.getDefaultUser()
    }
}

