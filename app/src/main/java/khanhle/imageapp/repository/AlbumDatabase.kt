package khanhle.imageapp.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import khanhle.imageapp.model.Album

@Database(entities = arrayOf(Album::class), version = 1)
abstract class AlbumDatabase : RoomDatabase() {
    abstract fun callDAO(): AlbumDAO
}