package khanhle.imageapp.model

import java.io.Serializable

class Image : Serializable {
    var id = ""
    var category_name = ""
    var filename = ""

    constructor(id: String, category_name: String, filename: String) {
        this.id = id
        this.category_name = category_name
        this.filename = filename
    }
}