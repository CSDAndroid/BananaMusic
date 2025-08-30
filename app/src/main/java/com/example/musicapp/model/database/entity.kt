package com.example.musicapp.model.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.musicapp.model.entity.Music
import java.io.Serializable

// 1. 用户实体（正常定义，无修改）
@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true) val userId: Long = 0,
    val username: String,
    val password: String,
    val user_img: String?
) : Serializable

// 2. 歌单实体（正常定义，无修改）
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
    val userId: Long, // 关联用户的外键
    val name: String, // 歌单名称
    val createTime: Long = System.currentTimeMillis()
) : Serializable

// 3. 歌单-歌曲关联表（多对多，正常定义）
@Entity(
    tableName = "playlist_music",
    primaryKeys = ["playlistId", "musicId"], // 复合主键
    foreignKeys = [
        ForeignKey(
            entity = Playlist::class,
            parentColumns = ["playlistId"],
            childColumns = ["playlistId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Music::class,
            parentColumns = ["db_id"], // 对应Music实体的主键
            childColumns = ["musicId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PlaylistMusicCrossRef(
    val playlistId: Long, // 关联歌单的ID
    val musicId: Long,    // 关联歌曲的ID（对应Music.db_id）
    val addTime: Long = System.currentTimeMillis()
) : Serializable

// 4. 歌单+歌曲列表（仅封装查询结果，无注解）
// 作用：单独查询某歌单时，封装“歌单信息+其包含的所有歌曲”
data class PlaylistWithMusics(
    @Embedded val playlist: Playlist, // 嵌入歌单实体
    @Relation(
        parentColumn = "playlistId",  // 歌单的主键（当前嵌入实体的主键）
        entityColumn = "db_id",       // 歌曲的主键（Music实体的主键）
        associateBy = Junction(       // 多对多关联的中间表
            value = PlaylistMusicCrossRef::class,
            parentColumn = "playlistId", // 中间表中关联歌单的字段
            entityColumn = "musicId"      // 中间表中关联歌曲的字段
        )
    )
    val musics: List<Music> // 该歌单包含的所有歌曲
) : Serializable

// 5. 用户+歌单列表（仅封装查询结果，无注解）
// 作用：查询用户时，封装“用户信息+其创建的所有歌单”（暂不包含歌曲）
data class UserWithPlaylists(
    @Embedded val user: User, // 嵌入用户实体
    @Relation(
        parentColumn = "userId",  // 用户的主键
        entityColumn = "userId"   // 歌单中关联用户的外键（Playlist.userId）
    )
    val playlists: List<Playlist> // 该用户创建的所有歌单（仅歌单信息，无歌曲）
) : Serializable