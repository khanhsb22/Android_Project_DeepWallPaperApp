package khanhle.imageapp.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import khanhle.imageapp.model.Wallpaper

@Dao
interface WallpaperDAO {
    @Insert
    fun insertImageID(wallPaper: Wallpaper)

    @Query("DELETE FROM Wallpaper")
    fun deleteAll()
}