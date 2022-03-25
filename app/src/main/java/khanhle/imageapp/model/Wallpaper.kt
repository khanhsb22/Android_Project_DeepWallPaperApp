package khanhle.imageapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Wallpaper {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
    var imageID: String = ""

    constructor(id: Int?, imageID: String) {
        this.id = id
        this.imageID = imageID
    }
}