package khanhle.imageapp.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import khanhle.imageapp.model.Album
import khanhle.imageapp.model.FavImage

@Dao
interface AlbumDAO {
    @Insert
    fun insertAlbumName(album: Album)

    /*@Query("SELECT COUNT(albumName) FROM Album")
    fun getRowCount(): Int*/

    @Query("SELECT * FROM Album")
    fun getAllAlbum(): List<Album>

    @Query("DELETE FROM Album WHERE albumName = :albumName")
    fun deleteAlbum(albumName: String)

    @Query("SELECT albumName FROM Album WHERE albumName = :albumName")
    fun getAlbumName(albumName: String): String

    @Query("SELECT COUNT(*) FROM Album")
    fun getRowCount(): Int
}