package khanhle.imageapp.repository

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import android.widget.*
import androidx.room.Room
import khanhle.imageapp.R
import khanhle.imageapp.adapter.SpinnerChooseAlbumAdapter
import khanhle.imageapp.model.Album
import khanhle.imageapp.model.ImageInAlbum

class DialogQuestionAlbum {
    private var albumDb: AlbumDatabase? = null
    private var imgInAlbumDb: ImageInAlbumDatabase? = null
    private var _context: Context
    private var imageID = ""
    private var imageUrl = ""
    private var image_add_to_album: ImageView

    constructor(_context: Context, imageID: String, imageUrl: String, image_add_to_album: ImageView) {
        this._context = _context
        this.imageID = imageID
        this.imageUrl = imageUrl
        this.image_add_to_album = image_add_to_album

        albumDb = Room.databaseBuilder(_context, AlbumDatabase::class.java, "album_database")
            .fallbackToDestructiveMigration().allowMainThreadQueries().build()

        imgInAlbumDb = Room.databaseBuilder(
            _context, ImageInAlbumDatabase::class.java,
            "img_in_album_database"
        ).fallbackToDestructiveMigration().allowMainThreadQueries().build()
    }

    fun showDialog(context: Context) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true) // true or false to close dialog when touch in screen
        dialog.setContentView(R.layout.question_album_dialog)

        var button_choose_album = dialog.findViewById(R.id.button_choose_album) as Button
        var button_remove_album = dialog.findViewById(R.id.button_remove_album) as Button

        val albumDAO = albumDb!!.callDAO()
        val list: List<Album> = albumDAO.getAllAlbum()
        var albums = ArrayList<Album>()

        for (item in list) {
            albums.add(Album(null, item.albumName))
        }

        button_choose_album.setOnClickListener {
            dialog.cancel()
            val chooseAlbum = DialogChooseAlbum(context, imageID, imageUrl, image_add_to_album)
            chooseAlbum.showDialog(context)
        }

        button_remove_album.setOnClickListener {
            val imgInAlbumDAO = imgInAlbumDb!!.callDAO()
            imgInAlbumDAO.deleteImage(imageID)
            dialog.cancel()
            image_add_to_album.setImageResource(R.drawable.ic_add_to_album)
            Toast.makeText(context, "Removed images from all albums !", Toast.LENGTH_SHORT).show()
        }

        dialog.show()

        var window = dialog.window
        window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }
}