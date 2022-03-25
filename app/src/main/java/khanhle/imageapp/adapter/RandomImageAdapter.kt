package khanhle.imageapp

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import io.paperdb.Paper
import khanhle.imageapp.databinding.RowRandomImageBinding
import khanhle.imageapp.model.Image
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.*
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Base64
import android.util.Base64.NO_WRAP
import android.webkit.URLUtil.decode
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.lang.Byte.decode
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL


class RandomImageAdapter : RecyclerView.Adapter<RandomImageAdapter.ItemHolder> {

    private var lists: ArrayList<Image>
    private var context: Context

    companion object {
        var clickListener: ClickListener? = null
    }

    constructor(lists: ArrayList<Image>, context: Context) : super() {
        this.lists = lists
        // Random image for list
        lists.shuffle()
        this.context = context
    }

    class ItemHolder : RecyclerView.ViewHolder, View.OnClickListener{
        var image_random: ImageView
        var linear_random_image: LinearLayout

        constructor(itemView: View) : super(itemView) {
            val binding = RowRandomImageBinding.bind(itemView)
            image_random = binding.imageRandom
            linear_random_image = binding.linearRandomImage

            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            clickListener!!.onItemClick(adapterPosition, v!!)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        var v: View = LayoutInflater.from(parent.context).inflate(R.layout.row_random_image, parent, false)
        var itemHolder = ItemHolder(v)

        return itemHolder
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        var item = lists.get(holder.adapterPosition)

        // Using Kotlin Coroutine
        GlobalScope.launch (Dispatchers.IO){
            GlobalScope.launch (Dispatchers.Main) {
                val requestOptions = RequestOptions()
                requestOptions.placeholder(R.drawable.place_holder)
                Glide.with(context).setDefaultRequestOptions(requestOptions).load(item.filename).into(holder.image_random)
            }
        }

        // Get from ImageActivity
        var imageID = Paper.book().read("imageID", "0")

        if (item.id == imageID) {
            holder.linear_random_image.setBackgroundResource(R.drawable.custom_border_selected)
        } else {
            holder.linear_random_image.setBackgroundResource(R.drawable.custom_border_none)
        }

    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return lists.size
    }

    fun setOnItemClickListener(clickListener: ClickListener?) {
        RandomImageAdapter.clickListener = clickListener!!
    }

    interface ClickListener {
        fun onItemClick(position: Int, v: View)
    }

}
