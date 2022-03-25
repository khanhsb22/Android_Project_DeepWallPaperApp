package khanhle.imageapp.adapter

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.room.Room
import khanhle.imageapp.R
import khanhle.imageapp.model.Album
import khanhle.imageapp.model.ImageInAlbum
import khanhle.imageapp.repository.ImageInAlbumDatabase

class SpinnerChooseAlbumAdapter : BaseAdapter {

    private var list: ArrayList<Album>
    private var context: Context
    private var imageID: String
    private var imgInAlbumDb: ImageInAlbumDatabase? = null

    constructor(list: ArrayList<Album>, context: Context, imageID: String) : super() {
        this.list = list
        this.context = context
        this.imageID = imageID

        imgInAlbumDb = Room.databaseBuilder(
            context, ImageInAlbumDatabase::class.java,
            "img_in_album_database"
        ).fallbackToDestructiveMigration().allowMainThreadQueries().build()
    }


    override fun getCount(): Int {
        if (this.list == null) {
            return 0
        }
        return this.list.size
    }

    override fun getItem(position: Int): Any {
        return this.list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var item = list[position]
        var rowView = LayoutInflater.from(parent!!.context).inflate(R.layout.row_spinner_album, parent, false)

        var text_albumName_spinner = rowView.findViewById(R.id.text_albumName_spinner) as TextView
        var image_check_albumName = rowView.findViewById(R.id.image_check_albumName) as ImageView

        text_albumName_spinner.text = item.albumName
        text_albumName_spinner.maxLines = 2
        text_albumName_spinner.ellipsize = TextUtils.TruncateAt.END

        val imgInAlbumDAO = imgInAlbumDb!!.callDAO()
        var list: List<ImageInAlbum> = imgInAlbumDAO.checkAlbumName(imageID)
        for (_item in list) {
            if (_item.albumName == item.albumName) {
                image_check_albumName.visibility = View.VISIBLE
            }
        }


        return rowView
    }
}