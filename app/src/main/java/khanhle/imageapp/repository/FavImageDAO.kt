package khanhle.imageapp.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import khanhle.imageapp.model.FavImage

@Dao
interface FavImageDAO {
    @Insert
    fun insertFavImage(favImg: FavImage)

    @Query("SELECT * FROM FavImage")
    fun getAllFavImage(): List<FavImage>

    @Query("DELETE FROM FavImage WHERE imageID = :imageID")
    fun deleteFavImage(imageID: String)

    @Query("SELECT imageID FROM FavImage WHERE imageID = :imageID")
    fun checkIsFavourite(imageID: String): String

    @Query("SELECT COUNT(*) FROM FavImage")
    fun getRowCount(): Int
}