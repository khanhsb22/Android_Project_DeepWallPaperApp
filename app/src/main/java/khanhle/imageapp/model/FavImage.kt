package khanhle.imageapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class FavImage {
    @PrimaryKey(autoGenerate = true)
    var favImageID: Int? = null
    var imageID: String = ""
    var imageUrl: String = ""

    constructor(favImageID: Int?, imageID: String, imageUrl: String) {
        this.favImageID = favImageID
        this.imageID = imageID
        this.imageUrl = imageUrl
    }
}