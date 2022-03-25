package khanhle.imageapp.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import khanhle.imageapp.model.Album
import khanhle.imageapp.model.FavImage
import khanhle.imageapp.model.ImageInAlbum

@Database(entities = arrayOf(ImageInAlbum::class), version = 1)
abstract class ImageInAlbumDatabase : RoomDatabase() {
    abstract fun callDAO(): ImageInAlbumDAO
}