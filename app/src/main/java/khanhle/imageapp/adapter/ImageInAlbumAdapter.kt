package khanhle.imageapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.paperdb.Paper
import khanhle.imageapp.databinding.RowImageInAlbumBinding
import khanhle.imageapp.model.ImageInAlbum
import khanhle.imageapp.repository.ImageInAlbumDatabase
import khanhle.imageapp.view.activity.ImageInAlbumActivity
import khanhle.imageapp.view.activity.SingleImageActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ImageInAlbumAdapter : RecyclerView.Adapter<ImageInAlbumAdapter.ItemHolder> {

    private var lists: ArrayList<ImageInAlbum>
    private var context: Context
    private var text_title_image_in_album: TextView
    private var image_close_images_in_album: ImageView
    private var text_select_all_image_in_album: TextView
    private var fab_delete_images_in_album: FloatingActionButton
    private var imageInAlbumActivity: ImageInAlbumActivity
    private var check = false
    private var listChecked = ArrayList<String>()
    private var imgInAlbumDb: ImageInAlbumDatabase? = null
    private var checkAll = false
    private var image_back: ImageView
    private var recyclerImageAlbum: RecyclerView
    private var linearEmptyImageInAlbum: LinearLayout
    private var albumName: String

    constructor(
        lists: ArrayList<ImageInAlbum>,
        context: Context,
        text_title_image_in_album: TextView,
        image_close_images_in_album: ImageView,
        text_select_all_image_in_album: TextView,
        fab_delete_images_in_album: FloatingActionButton,
        imageInAlbumActivity: ImageInAlbumActivity,
        image_back: ImageView,
        recyclerImageAlbum: RecyclerView,
        linearEmptyImageInAlbum: LinearLayout,
        albumName: String
    ) : super() {
        this.lists = lists
        this.context = context
        this.text_title_image_in_album = text_title_image_in_album
        this.image_close_images_in_album = image_close_images_in_album
        this.text_select_all_image_in_album = text_select_all_image_in_album
        this.fab_delete_images_in_album = fab_delete_images_in_album
        this.imageInAlbumActivity = imageInAlbumActivity
        this.image_back = image_back
        this.recyclerImageAlbum = recyclerImageAlbum
        this.linearEmptyImageInAlbum = linearEmptyImageInAlbum
        this.albumName = albumName

        imgInAlbumDb = Room.databaseBuilder(
            context, ImageInAlbumDatabase::class.java,
            "img_in_album_database"
        ).fallbackToDestructiveMigration().allowMainThreadQueries().build()

        Paper.init(context)
    }

    class ItemHolder : RecyclerView.ViewHolder {
        var image_in_album: ImageView
        var checkbox_delete_image_in_album: CheckBox
        var frameLayout_image_in_album: FrameLayout

        constructor(itemView: View) : super(itemView) {
            val binding = RowImageInAlbumBinding.bind(itemView)
            image_in_album = binding.imageInAlbum
            checkbox_delete_image_in_album = binding.checkboxDeleteImageInAlbum
            frameLayout_image_in_album = binding.frameLayoutImageInAlbum
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        var v: View =
            LayoutInflater.from(parent.context).inflate(R.layout.row_image_in_album, parent, false)
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
                    .into(holder.image_in_album)
            }
        }

        // Hide and show view base on check
        if (check) {
            holder.image_in_album.setColorFilter(ContextCompat.getColor(context, R.color.dark_background))
            image_back.visibility = View.GONE
            text_title_image_in_album.visibility = View.GONE
            image_close_images_in_album.visibility = View.VISIBLE
            text_select_all_image_in_album.visibility = View.VISIBLE

            text_select_all_image_in_album.setOnClickListener {
                checkAll = true
                notifyDataSetChanged()
            }

            holder.checkbox_delete_image_in_album.visibility = View.VISIBLE
            holder.checkbox_delete_image_in_album.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
                override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                    if (isChecked) {
                        listChecked.add(item.imageID)
                    } else {
                        listChecked.remove(item.imageID)
                    }
                }
            })

            image_close_images_in_album.setOnClickListener {
                check = false
                checkAll = false
                listChecked.clear()
                notifyDataSetChanged()
            }
            // Delete image in checked list
            fab_delete_images_in_album.setOnClickListener {
                for (_item in listChecked) {
                    val imgInAlbumDAO = imgInAlbumDb!!.callDAO()
                    imgInAlbumDAO.deleteImageInAlbumByID(_item, item.albumName)
                }
                // Reload widgets
                holder.checkbox_delete_image_in_album.isChecked = false
                holder.checkbox_delete_image_in_album.visibility = View.GONE
                image_close_images_in_album.visibility = View.GONE
                image_back.visibility = View.VISIBLE
                fab_delete_images_in_album.visibility = View.GONE
                text_title_image_in_album.visibility = View.VISIBLE
                text_select_all_image_in_album.visibility = View.GONE
                imageInAlbumActivity.getAllImage(item.albumName)

                notifyDataSetChanged()

                val imageInAlbumDAO = imgInAlbumDb!!.callDAO()
                var count = imageInAlbumDAO.getRowCount(albumName)
                if (count == 0) {
                    recyclerImageAlbum.visibility = View.GONE
                    linearEmptyImageInAlbum.visibility = View.VISIBLE
                }
            }
        } else {
            holder.image_in_album.setColorFilter(ContextCompat.getColor(context, R.color.root_background))
            image_back.visibility = View.VISIBLE
            holder.checkbox_delete_image_in_album.isChecked = false
            holder.checkbox_delete_image_in_album.visibility = View.GONE
            image_close_images_in_album.visibility = View.GONE
            fab_delete_images_in_album.visibility = View.GONE
            text_title_image_in_album.visibility = View.VISIBLE
            text_select_all_image_in_album.visibility = View.GONE
        }

        if (checkAll) {
            holder.checkbox_delete_image_in_album.isChecked = true
        }

        holder.itemView.setOnClickListener {
            var intent = Intent(context, SingleImageActivity::class.java)
            intent.putExtra("id", item.imageID)
            intent.putExtra("filename", item.imageUrl)
            context.startActivity(intent)

            // Using for ImageInAlbumActivity
            Paper.book().write("scrolling_value_img_in_album", position)
        }

        holder.itemView.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(v: View?): Boolean {
                check = true
                fab_delete_images_in_album.visibility = View.VISIBLE
                notifyDataSetChanged()
                return true
            }
        })
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return lists.size
    }


}




