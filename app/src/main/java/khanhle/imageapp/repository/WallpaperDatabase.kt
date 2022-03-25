package khanhle.imageapp.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import khanhle.imageapp.model.Wallpaper

@Database(entities = arrayOf(Wallpaper::class), version = 1)
abstract class WallpaperDatabase : RoomDatabase() {
    abstract fun callDAO(): WallpaperDAO
}