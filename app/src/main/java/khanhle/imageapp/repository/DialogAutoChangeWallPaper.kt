package khanhle.imageapp.repository

import android.R.attr
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.view.View
import android.view.Window
import android.widget.*
import androidx.room.Room
import khanhle.imageapp.R
import khanhle.imageapp.adapter.SpinnerAutoChangeWallPaperAdapter
import khanhle.imageapp.model.Album
import android.widget.RadioButton
import khanhle.imageapp.model.ImageInAlbum
import khanhle.imageapp.model.Wallpaper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList
import android.R.attr.src
import android.app.*
import android.content.Intent
import android.os.IBinder
import java.io.InputStream
import java.net.HttpURLConnection
import android.os.Binder
import io.paperdb.Paper
import khanhle.imageapp.view.activity.HomeActivity


class DialogAutoChangeWallPaper {
    private var albumDb: AlbumDatabase? = null
    private var imgInAlbumDb: ImageInAlbumDatabase? = null
    private var _context: Context
    private var spinnerItemText = ""
    private var radioButton_text = ""
    private var switchChecked = false
    private var set_auto_change = false
    private var images = ArrayList<ImageInAlbum>()

    constructor(_context: Context) {
        this._context = _context

        albumDb = Room.databaseBuilder(_context, AlbumDatabase::class.java, "album_database")
            .fallbackToDestructiveMigration().allowMainThreadQueries().build()

        imgInAlbumDb = Room.databaseBuilder(
            _context, ImageInAlbumDatabase::class.java,
            "img_in_album_database"
        ).fallbackToDestructiveMigration().allowMainThreadQueries().build()

        Paper.init(_context)
        switchChecked = Paper.book().read("switchChecked", false)
        radioButton_text = Paper.book().read("radioButton_text", "empty")
        spinnerItemText = Paper.book().read("spinnerItemText", "empty")
        set_auto_change = Paper.book().read("set_auto_change", false)
    }

    fun showDialog(context: Context) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true) // true or false to close dialog when touch in screen
        dialog.setContentView(R.layout.set_auto_change_wallpaper_dialog)

        var spinner_choose_album = dialog.findViewById(R.id.spinner_choose_album) as Spinner
        var radioButton_1_min = dialog.findViewById(R.id.radioButton_1_min) as RadioButton
        var radioButton_5_min = dialog.findViewById(R.id.radioButton_5_min) as RadioButton
        var radioButton_30_min = dialog.findViewById(R.id.radioButton_30_min) as RadioButton
        var radioButton_1_hour = dialog.findViewById(R.id.radioButton_1_hour) as RadioButton
        var radioButton_3_hour = dialog.findViewById(R.id.radioButton_3_hour) as RadioButton
        var radioButton_1_day = dialog.findViewById(R.id.radioButton_1_day) as RadioButton
        var button_apply = dialog.findViewById(R.id.button_apply) as Button
        var button_cancel_auto_change =
            dialog.findViewById(R.id.button_cancel_auto_change) as Button
        var switch_set_auto_change = dialog.findViewById(R.id.switch_set_auto_change) as Switch

        when (switchChecked) {
            false -> {
                switch_set_auto_change.isChecked = false
            }
            true -> {
                switch_set_auto_change.isChecked = true
            }
        }

        val albumDAO = albumDb!!.callDAO()
        val list: List<Album> = albumDAO.getAllAlbum()
        var albums = ArrayList<Album>()

        for (item in list) {
            albums.add(Album(null, item.albumName))
        }

        var spinnerAutoChangeWallPaperAdapter = SpinnerAutoChangeWallPaperAdapter(albums, context)
        spinner_choose_album.adapter = spinnerAutoChangeWallPaperAdapter

        for (i in 0 until albums.size) {
            if (spinnerItemText == albums[i].albumName) {
                var spinnerPosition = spinnerAutoChangeWallPaperAdapter.getItem(i)
                spinner_choose_album.setSelection(spinnerPosition)
            }
        }

        when (radioButton_text) {
            "1 minutes" -> {
                radioButton_1_min.isChecked = true
            }
            "5 minutes" -> {
                radioButton_5_min.isChecked = true
            }
            "30 minutes" -> {
                radioButton_30_min.isChecked = true
            }
            "1 hour" -> {
                radioButton_1_hour.isChecked = true
            }
            "3 hours" -> {
                radioButton_3_hour.isChecked = true
            }
            "1 day" -> {
                radioButton_1_day.isChecked = true
            }
            "empty" -> {
                radioButton_5_min.isChecked = false
                radioButton_30_min.isChecked = false
                radioButton_1_hour.isChecked = false
                radioButton_3_hour.isChecked = false
                radioButton_1_day.isChecked = false
                radioButton_1_min.isChecked = false
            }

        }

        radioButton_1_min.setOnClickListener {
            radioButton_5_min.isChecked = false
            radioButton_30_min.isChecked = false
            radioButton_1_hour.isChecked = false
            radioButton_3_hour.isChecked = false
            radioButton_1_day.isChecked = false
            radioButton_text = radioButton_1_min.text.toString()
        }

        radioButton_5_min.setOnClickListener {
            radioButton_30_min.isChecked = false
            radioButton_1_min.isChecked = false
            radioButton_1_hour.isChecked = false
            radioButton_3_hour.isChecked = false
            radioButton_1_day.isChecked = false
            radioButton_text = radioButton_5_min.text.toString()
        }

        radioButton_30_min.setOnClickListener {
            radioButton_1_min.isChecked = false
            radioButton_5_min.isChecked = false
            radioButton_1_hour.isChecked = false
            radioButton_3_hour.isChecked = false
            radioButton_1_day.isChecked = false
            radioButton_text = radioButton_30_min.text.toString()
        }

        radioButton_1_hour.setOnClickListener {
            radioButton_1_min.isChecked = false
            radioButton_5_min.isChecked = false
            radioButton_30_min.isChecked = false
            radioButton_3_hour.isChecked = false
            radioButton_1_day.isChecked = false
            radioButton_text = radioButton_1_hour.text.toString()
        }

        radioButton_3_hour.setOnClickListener {
            radioButton_1_min.isChecked = false
            radioButton_5_min.isChecked = false
            radioButton_30_min.isChecked = false
            radioButton_1_hour.isChecked = false
            radioButton_1_day.isChecked = false
            radioButton_text = radioButton_3_hour.text.toString()
        }

        radioButton_1_day.setOnClickListener {
            radioButton_1_min.isChecked = false
            radioButton_5_min.isChecked = false
            radioButton_30_min.isChecked = false
            radioButton_1_hour.isChecked = false
            radioButton_3_hour.isChecked = false
            radioButton_text = radioButton_1_day.text.toString()
        }

        spinner_choose_album.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
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

        switch_set_auto_change.setOnCheckedChangeListener(object :
            CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if (isChecked) {
                    switchChecked = true
                } else {
                    switchChecked = false
                }
            }
        })

        // Apply auto change wallpaper
        button_apply.setOnClickListener {
            if (!switchChecked && radioButton_text == "empty") {
                Toast.makeText(
                    context,
                    "Please enable the switch above to set up this feature !",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (switchChecked && radioButton_text == "empty") {
                Toast.makeText(context, "Please select a time !", Toast.LENGTH_SHORT).show()
            } else if (radioButton_text != "empty") {
                // Save all value in dialog
                Paper.book().write("switchChecked", switchChecked) // switchChecked value true
                Paper.book().write("radioButton_text", radioButton_text)
                Paper.book().write("spinnerItemText", spinnerItemText)

                if (!switchChecked) {
                    if (set_auto_change) {
                        Paper.book().write("set_auto_change", false)
                        var service = Intent(context, ChangeWallPaperService::class.java)
                        context.stopService(service)
                    } else {
                        Toast.makeText(
                            context,
                            "Please enable the switch above to set up this feature !",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else {
                    val check = CheckConnectInternet()
                    if (check.check(context)) {
                        val dialog = DialogCheckConnectInternet()
                        dialog.showDialog(context)
                    } else {
                        // Start Unbound Service
                        Paper.book().write("set_auto_change", true)
                        getImageInAlbum(spinnerItemText)
                        var service = Intent(context, ChangeWallPaperService::class.java)
                        service.putExtra("images", images)
                        service.putExtra("time", radioButton_text)
                        context.startService(service)
                        Toast.makeText(
                            context,
                            "Set automatically change the wallpaper after $radioButton_text success. Please close app.",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }


            }
        }

        button_cancel_auto_change.setOnClickListener {
            dialog.cancel()
        }

        dialog.show()

        var window = dialog.window
        window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }


    private fun getImageInAlbum(albumName: String) {
        val imgInAlbumDAO = imgInAlbumDb!!.callDAO()
        var list = imgInAlbumDAO.getAllImage(albumName)

        for (item in list) {
            images.add(ImageInAlbum(item.imgAlbumID, item.imageID, item.albumName, item.imageUrl))
        }
    }

}