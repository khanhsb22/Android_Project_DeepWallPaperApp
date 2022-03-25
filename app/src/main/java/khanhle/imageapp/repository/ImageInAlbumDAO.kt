package khanhle.imageapp.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import khanhle.imageapp.model.Album
import khanhle.imageapp.model.FavImage
import khanhle.imageapp.model.ImageInAlbum

@Dao
interface ImageInAlbumDAO {
    @Insert
    fun insertImage(imgInAlbum: ImageInAlbum)

    @Query("SELECT imageID FROM imageinalbum WHERE imageID = :imageID")
    fun checkImageInAlbum(imageID: String): String

    @Query("DELETE FROM imageinalbum WHERE imageID = :imageID")
    fun deleteImage(imageID: String)

    @Query("SELECT * FROM imageinalbum WHERE albumName = :albumName")
    fun getAllImage(albumName: String): List<ImageInAlbum>

    @Query("DELETE FROM imageinalbum WHERE albumName = :albumName")
    fun deleteImageInAlbum(albumName: String)

    @Query("DELETE FROM imageinalbum WHERE imageID = :imageID AND albumName = :albumName")
    fun deleteImageInAlbumByID(imageID: String, albumName: String)

    @Query("SELECT imageUrl FROM imageinalbum WHERE albumName = :albumName ORDER BY imageID ASC LIMIT 1")
    fun getFirstImageInAlbum(albumName: String): String

    @Query("SELECT COUNT(imageID) FROM imageinalbum WHERE albumName = :albumName")
    fun countAllImageInAlbum(albumName: String): Int

    @Query("SELECT * FROM imageinalbum WHERE imageID = :imageID")
    fun checkAlbumName(imageID: String): List<ImageInAlbum>

    @Query("SELECT COUNT(*) FROM imageinalbum WHERE albumName = :albumName")
    fun getRowCount(albumName: String): Int


}