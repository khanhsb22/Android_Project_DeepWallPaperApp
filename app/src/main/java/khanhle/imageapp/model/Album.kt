package khanhle.imageapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Album {
    @PrimaryKey(autoGenerate = true)
    var albumID: Int? = null
    var albumName: String = ""

    constructor(albumID: Int?, albumName: String) {
        this.albumID = albumID
        this.albumName = albumName
    }

}