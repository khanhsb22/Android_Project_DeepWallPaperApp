package khanhle.imageapp.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import khanhle.imageapp.R
import khanhle.imageapp.model.Album

class SpinnerAutoChangeWallPaperAdapter : BaseAdapter {

    private var list: ArrayList<Album>
    private var context: Context

    constructor(list: ArrayList<Album>, context: Context) : super() {
        this.list = list
        this.context = context
    }


    override fun getCount(): Int {
        if (this.list == null) {
            return 0
        }
        return this.list.size
    }

    override fun getItem(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var item = list[position]
        var rowView = LayoutInflater.from(parent!!.context).inflate(R.layout.row_spinner_album, parent, false)

        var text_albumName_spinner = rowView.findViewById(R.id.text_albumName_spinner) as TextView

        text_albumName_spinner.text = item.albumName
        text_albumName_spinner.maxLines = 2
        text_albumName_spinner.ellipsize = TextUtils.TruncateAt.END

        return rowView
    }
}