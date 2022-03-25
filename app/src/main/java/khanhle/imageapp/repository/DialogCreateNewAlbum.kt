package khanhle.imageapp.repository

import android.app.Dialog
import android.content.Context
import android.text.TextUtils
import android.view.Window
import android.widget.*
import androidx.room.Room
import io.paperdb.Paper
import khanhle.imageapp.R
import khanhle.imageapp.model.Album
import khanhle.imageapp.model.ImageInAlbum
import khanhle.imageapp.view.activity.YourAlbumActivity

class DialogCreateNewAlbum {

    private var _context: Context
    private var imageID = ""
    private var imageUrl = ""
    private var albumDb: AlbumDatabase? = null
    private var imgInAlbumDb: ImageInAlbumDatabase? = null
    private lateinit var yourAlbumActivity: YourAlbumActivity
    private lateinit var image_add_to_album: ImageView

    // Using in ViewPagerImageAdapter, SingleImageActivity
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

    // Using in YourAlbumActivity
    constructor(_context: Context, yourAlbumActivity: YourAlbumActivity) {
        this._context = _context
        this.yourAlbumActivity = yourAlbumActivity

        albumDb = Room.databaseBuilder(_context, AlbumDatabase::class.java, "album_database")
            .fallbackToDestructiveMigration().allowMainThreadQueries().build()

        imgInAlbumDb = Room.databaseBuilder(
            _context, ImageInAlbumDatabase::class.java,
            "img_in_album_database"
        ).fallbackToDestructiveMigration().allowMainThreadQueries().build()

        Paper.init(_context)
    }

    // Using in DialogSaveExternalImageInAlbum
    constructor(_context: Context) {
        this._context = _context

        albumDb = Room.databaseBuilder(_context, AlbumDatabase::class.java, "album_database")
            .fallbackToDestructiveMigration().allowMainThreadQueries().build()

        Paper.init(_context)
    }


    // Using in YourAlbumActivity
    fun showDialog2(context: Context) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true) // true or false to close dialog when touch in screen
        dialog.setContentView(R.layout.create_album_dialog)

        var button_create_album = dialog.findViewById(R.id.button_create_album) as Button
        var button_calcel_create_album = dialog.findViewById(R.id.button_calcel_create_album) as Button
        var edt_type_album_name = dialog.findViewById(R.id.edt_type_album_name) as EditText

        button_create_album.setOnClickListener {
            var albumName = edt_type_album_name.text.toString()
            if (TextUtils.isEmpty(albumName)) {
                Toast.makeText(context, "You must type album name !", Toast.LENGTH_SHORT).show()
            } else {
                // Create new album and insert first image
                val albumDAO = albumDb!!.callDAO()
                val _albumName = albumDAO.getAlbumName(albumName.trim())
                if (albumName.trim() == _albumName) {
                    Toast.makeText(context, "Album already exists !", Toast.LENGTH_SHORT).show()
                } else {
                    albumDAO.insertAlbumName(Album(null, albumName))
                    dialog.dismiss()
                    Paper.book().write("Album_created", "true")
                    yourAlbumActivity.getAllAlbum()
                }
            }
        }

        button_calcel_create_album.setOnClickListener {
            dialog.cancel()
        }

        dialog.show()

        var window = dialog.window
        window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }

    // Using in ViewPagerImageAdapter, SingleImageActivity
    fun showDialog(context: Context) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true) // true or false to close dialog when touch in screen
        dialog.setContentView(R.layout.create_album_dialog)

        var button_create_album = dialog.findViewById(R.id.button_create_album) as Button
        var button_calcel_create_album = dialog.findViewById(R.id.button_calcel_create_album) as Button
        var edt_type_album_name = dialog.findViewById(R.id.edt_type_album_name) as EditText

        button_create_album.setOnClickListener {
            var albumName = edt_type_album_name.text.toString()
            if (TextUtils.isEmpty(albumName)) {
                Toast.makeText(context, "You must type album name !", Toast.LENGTH_SHORT).show()
            } else {
                // Create new album and insert first image
                val albumDAO = albumDb!!.callDAO()
                val _albumName = albumDAO.getAlbumName(albumName.trim())
                if (albumName.trim() == _albumName) {
                    Toast.makeText(context, "Album already exists !", Toast.LENGTH_SHORT).show()
                } else {
                    albumDAO.insertAlbumName(Album(null, albumName))
                    val imgInAlbumDAO = imgInAlbumDb!!.callDAO()
                    imgInAlbumDAO.insertImage(ImageInAlbum(null, imageID, albumName, imageUrl))
                    dialog.dismiss()
                    image_add_to_album.setImageResource(R.drawable.ic_added_album)
                    Toast.makeText(context, "New album created !", Toast.LENGTH_SHORT).show()
                }

            }
        }

        button_calcel_create_album.setOnClickListener {
            dialog.cancel()
        }

        dialog.show()

        var window = dialog.window
        window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }

    // Using in DialogSaveExternalImageInAlbum
    fun showDialog3(context: Context) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true) // true or false to close dialog when touch in screen
        dialog.setContentView(R.layout.create_album_dialog)

        var button_create_album = dialog.findViewById(R.id.button_create_album) as Button
        var button_calcel_create_album = dialog.findViewById(R.id.button_calcel_create_album) as Button
        var edt_type_album_name = dialog.findViewById(R.id.edt_type_album_name) as EditText

        button_create_album.setOnClickListener {
            var albumName = edt_type_album_name.text.toString()
            if (TextUtils.isEmpty(albumName)) {
                Toast.makeText(context, "You must type album name !", Toast.LENGTH_SHORT).show()
            } else {
                // Create new album and insert first image
                val albumDAO = albumDb!!.callDAO()
                val _albumName = albumDAO.getAlbumName(albumName.trim())
                if (albumName.trim() == _albumName) {
                    Toast.makeText(context, "Album already exists !", Toast.LENGTH_SHORT).show()
                } else {
                    albumDAO.insertAlbumName(Album(null, albumName))
                    dialog.dismiss()
                    Paper.book().write("albumName", albumName)
                    Toast.makeText(context, "New album created !", Toast.LENGTH_SHORT).show()
                }

            }
        }

        button_calcel_create_album.setOnClickListener {
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