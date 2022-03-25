package khanhle.imageapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
class ImageInAlbum : Serializable {
    @PrimaryKey(autoGenerate = true)
    var imgAlbumID: Int? = null
    var imageID: String = ""
    var albumName: String = ""
    var imageUrl: String = ""

    constructor(imgAlbumID: Int?, imageID: String, albumName: String, imageUrl: String) {
        this.imgAlbumID = imgAlbumID
        this.imageID = imageID
        this.albumName = albumName
        this.imageUrl = imageUrl
    }
}