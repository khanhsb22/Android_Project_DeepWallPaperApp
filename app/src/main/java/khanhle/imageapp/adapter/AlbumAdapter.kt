package khanhle.imageapp

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.paperdb.Paper
import khanhle.imageapp.databinding.RowAlbumBinding
import khanhle.imageapp.model.Album
import khanhle.imageapp.repository.AlbumDatabase
import khanhle.imageapp.repository.ImageInAlbumDatabase
import khanhle.imageapp.view.activity.ImageInAlbumActivity
import khanhle.imageapp.view.activity.YourAlbumActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AlbumAdapter : RecyclerView.Adapter<AlbumAdapter.ItemHolder> {

    private var lists: ArrayList<Album>
    private var context: Context
    private var text_title_album: TextView
    private var image_close_album: ImageView
    private var text_select_all_album: TextView
    private var fab_delete_album: FloatingActionButton
    private var fab_add_new_album: FloatingActionButton
    private var check = false
    private var listChecked = ArrayList<String>()
    private var albumDb: AlbumDatabase? = null
    private var imgInAlbumDb: ImageInAlbumDatabase? = null
    private var yourAlbumActivity: YourAlbumActivity
    private var image_back: ImageView
    private var checkAll = false
    private var recyclerAlbum: RecyclerView
    private var linearEmptyAlbum: LinearLayout

    constructor(
        lists: ArrayList<Album>,
        context: Context,
        text_title_album: TextView,
        image_close_album: ImageView,
        text_select_all_album: TextView,
        fab_delete_album: FloatingActionButton,
        fab_add_new_album: FloatingActionButton,
        yourAlbumActivity: YourAlbumActivity,
        image_back: ImageView,
        recyclerAlbum: RecyclerView,
        linearEmptyAlbum: LinearLayout
    ) : super() {
        this.lists = lists
        this.context = context
        this.text_title_album = text_title_album
        this.image_close_album = image_close_album
        this.text_select_all_album = text_select_all_album
        this.fab_delete_album = fab_delete_album
        this.fab_add_new_album = fab_add_new_album
        this.yourAlbumActivity = yourAlbumActivity
        this.image_back = image_back
        this.recyclerAlbum = recyclerAlbum
        this.linearEmptyAlbum = linearEmptyAlbum

        albumDb = Room.databaseBuilder(context, AlbumDatabase::class.java, "album_database")
            .fallbackToDestructiveMigration().allowMainThreadQueries().build()

        imgInAlbumDb = Room.databaseBuilder(
            context, ImageInAlbumDatabase::class.java,
            "img_in_album_database"
        ).fallbackToDestructiveMigration().allowMainThreadQueries().build()

        Paper.init(context)
    }


    class ItemHolder : RecyclerView.ViewHolder {
        var image_album: ImageView
        var text_album_name: TextView
        var text_number_image: TextView
        var checkbox_delete_album: CheckBox

        constructor(itemView: View) : super(itemView) {
            val binding = RowAlbumBinding.bind(itemView)
            image_album = binding.imageAlbum
            text_album_name = binding.textAlbumName
            text_number_image = binding.textNumberImage
            checkbox_delete_album = binding.checkboxDeleteAlbum

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        var v: View =
            LayoutInflater.from(parent.context).inflate(R.layout.row_album, parent, false)
        var itemHolder = ItemHolder(v)

        return itemHolder
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        var item = lists.get(holder.adapterPosition)

        val imgInAlbumDAO = imgInAlbumDb!!.callDAO()
        var count = imgInAlbumDAO.getRowCount(item.albumName)

        if (count == 0) {
            holder.image_album.setImageResource(R.drawable.place_holder_album)
        } else {
            // Using Kotlin Coroutine
            GlobalScope.launch(Dispatchers.IO) {
                val imageInAlbumDAO = imgInAlbumDb!!.callDAO()
                val firstImageURL = imageInAlbumDAO.getFirstImageInAlbum(item.albumName)
                GlobalScope.launch(Dispatchers.Main) {
                    Glide.with(context).load(firstImageURL).into(holder.image_album)
                }
            }
        }


        val imageInAlbumDAO = imgInAlbumDb!!.callDAO()
        val countImageInAlbum = imageInAlbumDAO.countAllImageInAlbum(item.albumName)
        holder.text_number_image.text = "$countImageInAlbum photo"

        holder.text_album_name.text = item.albumName
        holder.text_album_name.maxLines = 2
        holder.text_album_name.ellipsize = TextUtils.TruncateAt.END

        holder.itemView.setOnClickListener {
            var intent = Intent(context, ImageInAlbumActivity::class.java)
            intent.putExtra("albumName", item.albumName)
            context.startActivity(intent)

            // Using for YourAlbumActivity
            Paper.book().write("scrolling_value_album", position)
        }

        // Hide and show view base on check
        if (check) {
            holder.image_album.setColorFilter(
                ContextCompat.getColor(
                    context,
                    R.color.dark_background
                )
            )
            image_back.visibility = View.GONE
            fab_add_new_album.visibility = View.GONE
            text_title_album.visibility = View.GONE
            image_close_album.visibility = View.VISIBLE
            text_select_all_album.visibility = View.VISIBLE

            text_select_all_album.setOnClickListener {
                checkAll = true
                notifyDataSetChanged()
            }

            holder.checkbox_delete_album.visibility = View.VISIBLE
            holder.checkbox_delete_album.setOnCheckedChangeListener(object :
                CompoundButton.OnCheckedChangeListener {
                override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                    if (isChecked) {
                        listChecked.add(item.albumName)
                    } else {
                        listChecked.remove(item.albumName)
                    }
                }
            })

            text_select_all_album.setOnClickListener {
                checkAll = true
                notifyDataSetChanged()
            }

            image_close_album.setOnClickListener {
                check = false
                checkAll = false
                listChecked.clear()
                notifyDataSetChanged()
            }
            // Delete album and image in checked list
            fab_delete_album.setOnClickListener {
                for (item in listChecked) {
                    val albumDAO = albumDb!!.callDAO()
                    albumDAO.deleteAlbum(item)

                    val imgAlbumDAO = imgInAlbumDb!!.callDAO()
                    imgAlbumDAO.deleteImageInAlbum(item)
                }
                // Reload widgets
                fab_add_new_album.visibility = View.VISIBLE
                holder.checkbox_delete_album.isChecked = false
                holder.checkbox_delete_album.visibility = View.GONE
                image_back.visibility = View.VISIBLE
                image_close_album.visibility = View.GONE
                fab_delete_album.visibility = View.GONE
                text_title_album.visibility = View.VISIBLE
                text_select_all_album.visibility = View.GONE
                yourAlbumActivity.getAllAlbum()
                notifyDataSetChanged()

                val albumDAO = albumDb!!.callDAO()
                var count = albumDAO.getRowCount()
                if (count == 0) {
                    recyclerAlbum.visibility = View.GONE
                    linearEmptyAlbum.visibility = View.VISIBLE
                }
            }
        } else {
            fab_add_new_album.visibility = View.VISIBLE
            holder.checkbox_delete_album.isChecked = false
            holder.checkbox_delete_album.visibility = View.GONE
            image_close_album.visibility = View.GONE
            fab_delete_album.visibility = View.GONE
            text_title_album.visibility = View.VISIBLE
            text_select_all_album.visibility = View.GONE
            image_back.visibility = View.VISIBLE
            holder.image_album.setColorFilter(
                ContextCompat.getColor(
                    context,
                    R.color.root_background
                )
            )
        }

        if (checkAll) {
            holder.checkbox_delete_album.isChecked = true
        }

        holder.itemView.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(v: View?): Boolean {
                check = true
                fab_delete_album.visibility = View.VISIBLE
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




