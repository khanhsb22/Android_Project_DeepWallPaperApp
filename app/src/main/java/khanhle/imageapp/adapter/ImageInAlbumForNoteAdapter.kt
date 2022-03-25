package khanhle.imageapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import khanhle.imageapp.databinding.RowImageInAlbumForNoteBinding
import khanhle.imageapp.model.ImageInAlbum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ImageInAlbumForNoteAdapter : RecyclerView.Adapter<ImageInAlbumForNoteAdapter.ItemHolder> {

    private var lists: ArrayList<ImageInAlbum>
    private var context: Context

    companion object {
        var clickListener: ClickListener? = null
    }

    constructor(
        lists: ArrayList<ImageInAlbum>,
        context: Context
    ) : super() {
        this.lists = lists
        this.context = context

    }

    class ItemHolder : RecyclerView.ViewHolder, View.OnClickListener {
        var image_forNote: ImageView

        constructor(itemView: View) : super(itemView) {
            val binding = RowImageInAlbumForNoteBinding.bind(itemView)
            image_forNote = binding.imageForNote
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            clickListener!!.onItemClick(adapterPosition, p0!!)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        var v: View =
            LayoutInflater.from(parent.context).inflate(R.layout.row_image_in_album_for_note, parent, false)
        var itemHolder = ItemHolder(v)

        return itemHolder
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        var item = lists[holder.adapterPosition]

        // Using Kotlin Coroutine
        GlobalScope.launch (Dispatchers.IO){
            GlobalScope.launch (Dispatchers.Main) {
                val requestOptions = RequestOptions()
                requestOptions.placeholder(R.drawable.place_holder)
                Glide.with(context).setDefaultRequestOptions(requestOptions).load(item.imageUrl)
                    .into(holder.image_forNote)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return lists.size
    }

    interface ClickListener {
        fun onItemClick(position: Int, v: View)
    }

    fun setOnItemClickListener(clickListener: ClickListener?) {
        ImageInAlbumForNoteAdapter.clickListener = clickListener!!
    }


}




