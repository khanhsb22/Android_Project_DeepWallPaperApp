package khanhle.imageapp.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import khanhle.imageapp.model.FavImage

@Database(entities = arrayOf(FavImage::class), version = 1)
abstract class FavImageDatabase : RoomDatabase() {
    abstract fun callDAO(): FavImageDAO
}