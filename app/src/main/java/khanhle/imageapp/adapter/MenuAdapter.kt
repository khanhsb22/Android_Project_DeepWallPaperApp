package khanhle.imageapp

import FavoriteFragment
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Delete
import androidx.room.Room
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import khanhle.imageapp.databinding.FragmentFavoriteBinding
import khanhle.imageapp.databinding.RowCategoryBinding
import khanhle.imageapp.databinding.RowFavImageBinding
import khanhle.imageapp.databinding.RowMenuBinding
import khanhle.imageapp.model.Category
import khanhle.imageapp.model.FavImage
import khanhle.imageapp.model.Menu
import khanhle.imageapp.repository.FavImageDatabase
import khanhle.imageapp.view.activity.ImageActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MenuAdapter : RecyclerView.Adapter<MenuAdapter.ItemHolder> {

    private var lists: ArrayList<Menu>

    constructor(
        lists: ArrayList<Menu>
    ) : super() {
        this.lists = lists
    }

    companion object {
        var clickListener: ClickListener? = null
    }

    class ItemHolder : RecyclerView.ViewHolder, View.OnClickListener {
        var text_title_menu: TextView
        var image_icon_menu: ImageView

        constructor(itemView: View) : super(itemView) {
            val binding = RowMenuBinding.bind(itemView)
            text_title_menu = binding.textTitleMenu
            image_icon_menu = binding.imageIconMenu

            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            clickListener!!.onItemClick(adapterPosition, v!!)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        var v: View =
            LayoutInflater.from(parent.context).inflate(R.layout.row_menu, parent, false)
        var itemHolder = ItemHolder(v)

        return itemHolder
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        var item = lists[holder.adapterPosition]
        holder.text_title_menu.text = item.title
        holder.image_icon_menu.setImageResource(item.icon)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return lists.size
    }

    fun setOnItemClickListener(clickListener: ClickListener?) {
        MenuAdapter.clickListener = clickListener!!
    }

    interface ClickListener {
        fun onItemClick(position: Int, v: View)
    }

}




