package khanhle.imageapp.repository

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import androidx.room.Room
import com.squareup.picasso.Picasso
import io.paperdb.Paper
import khanhle.imageapp.R
import khanhle.imageapp.adapter.SpinnerAutoChangeWallPaperAdapter
import khanhle.imageapp.model.Album
import khanhle.imageapp.model.ImageInAlbum


class DialogSaveExternalImageInAlbum {
    private var albumDb: AlbumDatabase? = null
    private var imgInAlbumDb: ImageInAlbumDatabase? = null
    private var _context: Context
    private var imageUri = ""
    private var listImageUri = ArrayList<String>()
    private var spinnerItemText = ""

    constructor(_context: Context, imageUri: String) {
        this._context = _context
        this.imageUri = imageUri

        albumDb = Room.databaseBuilder(_context, AlbumDatabase::class.java, "album_database")
            .fallbackToDestructiveMigration().allowMainThreadQueries().build()

        imgInAlbumDb = Room.databaseBuilder(
            _context, ImageInAlbumDatabase::class.java,
            "img_in_album_database"
        ).fallbackToDestructiveMigration().allowMainThreadQueries().build()

        Paper.init(_context)

    }

    constructor(_context: Context, listImageUri: ArrayList<String>) {
        this._context = _context
        this.listImageUri = listImageUri

        albumDb = Room.databaseBuilder(_context, AlbumDatabase::class.java, "album_database")
            .fallbackToDestructiveMigration().allowMainThreadQueries().build()

        imgInAlbumDb = Room.databaseBuilder(
            _context, ImageInAlbumDatabase::class.java,
            "img_in_album_database"
        ).fallbackToDestructiveMigration().allowMainThreadQueries().build()

        Paper.init(_context)

    }

    fun showDialog(context: Context) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true) // true or false to close dialog when touch in screen
        dialog.setContentView(R.layout.save_external_image_dialog)

        var radioGroup_add_to_album = dialog.findViewById(R.id.radioGroup_add_to_album) as RadioGroup
        var radioButton_add_to_existing_album = dialog.findViewById(R.id.radioButton_add_to_existing_album) as RadioButton
        var spinner_choose_album_external_image = dialog.findViewById(R.id.spinner_choose_album_external_image) as Spinner
        var button_create_new_album_external_image = dialog.findViewById(R.id.button_create_new_album_external_image) as Button
        var button_add_external_image = dialog.findViewById(R.id.button_add_external_image) as Button
        var button_cancel_external_image = dialog.findViewById(R.id.button_cancel_external_image) as Button

        radioButton_add_to_existing_album.isChecked

        radioGroup_add_to_album.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                when (checkedId) {
                    R.id.radioButton_add_to_existing_album -> {
                        spinner_choose_album_external_image.visibility = View.VISIBLE
                        button_create_new_album_external_image.visibility = View.GONE
                    }
                    R.id.radioButton_add_to_new_album -> {
                        button_create_new_album_external_image.visibility = View.VISIBLE
                        spinner_choose_album_external_image.visibility = View.GONE
                        button_create_new_album_external_image.setOnClickListener {
                            val dialogCreateNewAlbum = DialogCreateNewAlbum(context)
                            dialogCreateNewAlbum.showDialog3(context)
                        }
                    }
                }
            }
        })

        val albumDAO = albumDb!!.callDAO()
        val list: List<Album> = albumDAO.getAllAlbum()
        var albums = ArrayList<Album>()

        for (item in list) {
            albums.add(Album(null, item.albumName))
        }

        // Reuse SpinnerAutoChangeWallPaperAdapter
        var spinnerAutoChangeWallPaperAdapter = SpinnerAutoChangeWallPaperAdapter(albums, context)
        spinner_choose_album_external_image.adapter = spinnerAutoChangeWallPaperAdapter

        /*for (i in 0 until albums.size) {
            if (spinnerItemText == albums[i].albumName) {
                var spinnerPosition = spinnerAutoChangeWallPaperAdapter.getItem(i)
                spinner_choose_album_external_image.setSelection(spinnerPosition)
            }
        }*/

        spinner_choose_album_external_image.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                spinnerItemText = albums[position].albumName
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        })

        button_add_external_image.setOnClickListener {

            if (radioButton_add_to_existing_album.isChecked) {
                val imgInAlbumDAO = imgInAlbumDb!!.callDAO()
                var albumDAO = albumDb!!.callDAO()
                var listAlbum = albumDAO.getAllAlbum()

                if (imageUri != "" && listAlbum.isNotEmpty()) {
                    // Add 1 image to database
                    imgInAlbumDAO.insertImage(ImageInAlbum(null, imageUri, spinnerItemText, imageUri))
                    Toast.makeText(context, "Image was added to album: $spinnerItemText", Toast.LENGTH_LONG).show()
                    dialog.dismiss()
                }

                if (listImageUri.size != 0 && listAlbum.isNotEmpty()) {
                    for (i in 0 until listImageUri.size) {
                        imgInAlbumDAO.insertImage(ImageInAlbum(null, listImageUri[i], spinnerItemText, listImageUri[i]))
                    }
                    Toast.makeText(context, "Image was added to album: $spinnerItemText", Toast.LENGTH_LONG).show()
                    dialog.dismiss()
                }

                if (listAlbum.isEmpty()) {
                    Toast.makeText(context, "You don't have any albums yet, create a new album before adding !", Toast.LENGTH_LONG).show()
                }
            } else {
                // albumName get from DialogCreateNewAlbum
                var albumName = Paper.book().read("albumName", "null")

                if (albumName == "null") {
                    Toast.makeText(context, "You need create new album before adding !", Toast.LENGTH_SHORT).show()
                } else {
                    val imgInAlbumDAO = imgInAlbumDb!!.callDAO()

                    // Add 1 image to database
                    if (imageUri != "") {
                        imgInAlbumDAO.insertImage(ImageInAlbum(null, imageUri, albumName, imageUri))
                        Toast.makeText(context, "Image was added to new album: $albumName", Toast.LENGTH_LONG).show()
                        dialog.dismiss()
                    }
                    // Add more 1 image to database
                    if (listImageUri.size != 0) {
                        for (i in 0 until listImageUri.size) {
                            imgInAlbumDAO.insertImage(ImageInAlbum(null, listImageUri[i], albumName, listImageUri[i]))
                        }
                        Toast.makeText(context, "Image was added to new album: $albumName", Toast.LENGTH_LONG).show()
                        dialog.dismiss()
                    }
                }
            }
        }

        button_cancel_external_image.setOnClickListener {
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