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

class DialogChooseAlbum {
    private var albumDb: AlbumDatabase? = null
    private var imgInAlbumDb: ImageInAlbumDatabase? = null
    private var _context: Context
    private var imageID = ""
    private var imageUrl = ""
    private var spinnerItemText = ""
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
        dialog.setContentView(R.layout.choose_album_dialog)

        var spinner_album = dialog.findViewById(R.id.spinner_album) as Spinner
        var button_add_album = dialog.findViewById(R.id.button_add_album) as Button
        var button_cancel_album = dialog.findViewById(R.id.button_cancel_album) as Button

        val albumDAO = albumDb!!.callDAO()
        val list: List<Album> = albumDAO.getAllAlbum()
        var albums = ArrayList<Album>()

        for (item in list) {
            albums.add(Album(null, item.albumName))
        }

        var spinnerChooseAlbumAdapter = SpinnerChooseAlbumAdapter(albums, context, imageID)
        spinner_album.adapter = spinnerChooseAlbumAdapter

        spinner_album.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                //spinnerItemText = parent!!.getItemAtPosition(position).toString()
                spinnerItemText = albums[position].albumName
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        })

        button_add_album.setOnClickListener {
            // Check same album
            val imgInAlbumDAO = imgInAlbumDb!!.callDAO()
            var list: List<ImageInAlbum> = imgInAlbumDAO.checkAlbumName(imageID)
            var checkAlbum = 0
            for (_item in list) {
                if (_item.albumName == spinnerItemText) {
                    checkAlbum++
                    Toast.makeText(context, "This image already exists in this album ! You can choose another album.",
                        Toast.LENGTH_SHORT).show()
                }
            }
            if (checkAlbum == 0) {
                imgInAlbumDAO.insertImage(ImageInAlbum(null, imageID, spinnerItemText, imageUrl))
                Toast.makeText(context, "Added image to $spinnerItemText album !", Toast.LENGTH_SHORT)
                    .show()
                image_add_to_album.setImageResource(R.drawable.ic_added_album)
                dialog.cancel()
            }

        }

        button_cancel_album.setOnClickListener {
            dialog.cancel()
        }

        dialog.show()

        var window = dialog.window
        window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }
}