package khanhle.imageapp

import FavoriteFragment
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Delete
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import io.paperdb.Paper
import khanhle.imageapp.databinding.FragmentFavoriteBinding
import khanhle.imageapp.databinding.RowCategoryBinding
import khanhle.imageapp.databinding.RowFavImageBinding
import khanhle.imageapp.model.Category
import khanhle.imageapp.model.FavImage
import khanhle.imageapp.repository.FavImageDatabase
import khanhle.imageapp.view.activity.ImageActivity
import khanhle.imageapp.view.activity.SingleImageActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL

class FavImageAdapter : RecyclerView.Adapter<FavImageAdapter.ItemHolder> {

    private var lists: ArrayList<FavImage>
    private var context: Context
    private var fab_delete_all_fav: FloatingActionButton
    private var image_close_fav: ImageView
    private var text_title_fav: TextView
    private var text_checkAll: TextView
    private var check = false
    private var checkAll = false
    private var listChecked = ArrayList<String>()
    private var db: FavImageDatabase? = null
    private var favFragment: FavoriteFragment
    private var linearEmptyFav: LinearLayout
    private var recyclerFavImg: RecyclerView

    constructor(
        lists: ArrayList<FavImage>,
        context: Context,
        fab_delete_all_fav: FloatingActionButton,
        image_close_fav: ImageView,
        text_title_fav: TextView,
        text_checkAll: TextView,
        favFragment: FavoriteFragment,
        linearEmptyFav: LinearLayout,
        recyclerFavImg: RecyclerView
    ) : super() {
        this.lists = lists
        this.context = context
        this.fab_delete_all_fav = fab_delete_all_fav
        this.image_close_fav = image_close_fav
        this.text_title_fav = text_title_fav
        this.text_checkAll = text_checkAll
        this.favFragment = favFragment
        this.linearEmptyFav = linearEmptyFav
        this.recyclerFavImg = recyclerFavImg

        Paper.init(context)
    }

    class ItemHolder : RecyclerView.ViewHolder {
        var image_fav: ImageView
        var checkbox_delete: CheckBox

        constructor(itemView: View) : super(itemView) {
            val binding = RowFavImageBinding.bind(itemView)
            image_fav = binding.imageFav
            checkbox_delete = binding.checkboxDelete
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        var v: View =
            LayoutInflater.from(parent.context).inflate(R.layout.row_fav_image, parent, false)
        var itemHolder = ItemHolder(v)

        db = Room.databaseBuilder(context, FavImageDatabase::class.java, "fav_image_database")
            .fallbackToDestructiveMigration().allowMainThreadQueries().build()

        return itemHolder
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        var item = lists.get(holder.adapterPosition)

        // Using Kotlin Coroutine
        GlobalScope.launch (Dispatchers.IO){
            GlobalScope.launch (Dispatchers.Main) {
                val requestOptions = RequestOptions()
                requestOptions.placeholder(R.drawable.place_holder)
                Glide.with(context).setDefaultRequestOptions(requestOptions).load(item.imageUrl).into(holder.image_fav)
            }
        }

        // Hide and show view base on check
        if (check) {
            holder.image_fav.setColorFilter(ContextCompat.getColor(context, R.color.dark_background))

            text_title_fav.visibility = View.GONE
            image_close_fav.visibility = View.VISIBLE
            text_checkAll.visibility = View.VISIBLE
            holder.checkbox_delete.visibility = View.VISIBLE

            holder.checkbox_delete.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
                override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                    if (isChecked) {
                        listChecked.add(item.imageID)
                    } else {
                        listChecked.remove(item.imageID)
                    }
                }
            })

            image_close_fav.setOnClickListener {
                check = false
                checkAll = false
                listChecked.clear()
                notifyDataSetChanged()
            }

            text_checkAll.setOnClickListener {
                checkAll = true
                notifyDataSetChanged()
            }

            // Delete image in checked list
            fab_delete_all_fav.setOnClickListener {
                for (item in listChecked) {
                    val favImgDAO = db!!.callDAO()
                    favImgDAO.deleteFavImage(item)
                }
                image_close_fav.visibility = View.GONE
                fab_delete_all_fav.visibility = View.GONE
                text_title_fav.visibility = View.VISIBLE
                text_checkAll.visibility = View.GONE
                favFragment.getAllImage()

                // Hide and show view
                val favImgDAO = db!!.callDAO()
                var count = favImgDAO.getRowCount()
                if (count == 0) {
                    recyclerFavImg.visibility = View.GONE
                    linearEmptyFav.visibility = View.VISIBLE
                }

            }
        } else {
            holder.image_fav.setColorFilter(ContextCompat.getColor(context, R.color.root_background))
            holder.checkbox_delete.isChecked = false
            holder.checkbox_delete.visibility = View.GONE
            image_close_fav.visibility = View.GONE
            fab_delete_all_fav.visibility = View.GONE
            text_title_fav.visibility = View.VISIBLE
            text_checkAll.visibility = View.GONE
        }

        if (checkAll) {
            holder.checkbox_delete.isChecked = true
        }

        holder.image_fav.setOnLongClickListener(object : View.OnLongClickListener{
            override fun onLongClick(v: View?): Boolean {
                check = true
                fab_delete_all_fav.visibility = View.VISIBLE
                notifyDataSetChanged()
                return true
            }
        })

        holder.image_fav.setOnClickListener {
            var item = lists[position]
            var intent = Intent(context, SingleImageActivity::class.java)
            intent.putExtra("id", item.imageID)
            intent.putExtra("filename", item.imageUrl)
            context.startActivity(intent)

            // Write Paper value for scrolling to position, using for FavFragment
            Paper.book().write("scrolling_value", position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return lists.size
    }


}




