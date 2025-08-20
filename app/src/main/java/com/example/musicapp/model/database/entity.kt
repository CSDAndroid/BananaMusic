package com.example.musicapp.model.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Relation
import com.example.musicapp.model.entity.Music
import java.io.Serializable

// 用户实体
@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true) val userId: Long = 0,
    val username: String,
    val password: String
) : Serializable

// 歌单实体
@Entity(
    tableName = "playlist",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["userId"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Playlist(
    @PrimaryKey(autoGenerate = true) val playlistId: Long = 0,
    val userId: Long, // 关联的用户ID
    val name: String, // 歌单名称
    val createTime: Long = System.currentTimeMillis() // 创建时间
) : Serializable

// 歌单歌曲关联表（多对多关系）
@Entity(
    tableName = "playlist_music",
    primaryKeys = ["playlistId", "musicId"],
    foreignKeys = [
        ForeignKey(
            entity = Playlist::class,
            parentColumns = ["playlistId"],
            childColumns = ["playlistId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Music::class,
            parentColumns = ["id"],
            childColumns = ["musicId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PlaylistMusicCrossRef(
    val playlistId: Long,
    val musicId: Long,
    val addTime: Long = System.currentTimeMillis() // 添加到歌单的时间
) : Serializable

// 带歌曲列表的歌单数据类（用于查询）
data class PlaylistWithMusics(
    @Embedded val playlist: Playlist,
    @Relation(
        parentColumn = "playlistId",
        entityColumn = "id",
        associateBy = androidx.room.Junction(PlaylistMusicCrossRef::class)
    )
    val musics: List<Music>
) : Serializable

// 带歌单列表的用户数据类（用于查询）
data class UserWithPlaylists(
    @Embedded val user: User,
    @Relation(
        parentColumn = "userId",
        entityColumn = "playlistId"
    )
    val playlists: List<PlaylistWithMusics>
) : Serializable