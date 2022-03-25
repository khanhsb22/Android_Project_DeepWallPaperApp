package khanhle.imageapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import khanhle.imageapp.databinding.RowCategoryBinding
import khanhle.imageapp.model.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CategoryAdapter : RecyclerView.Adapter<CategoryAdapter.ItemHolder> {

    private var lists: ArrayList<Category>
    private var context: Context

    companion object {
        var clickListener: ClickListener? = null
    }

    constructor(lists: ArrayList<Category>, context: Context) : super() {
        this.lists = lists
        this.context = context
    }

    class ItemHolder : RecyclerView.ViewHolder, View.OnClickListener {
        var img_category: ImageView
        var text_category_name: TextView
        var frame_background_category: FrameLayout

        constructor(itemView: View) : super(itemView) {
            val binding = RowCategoryBinding.bind(itemView)
            img_category = binding.imgCategory
            text_category_name = binding.textCategoryName
            frame_background_category = binding.frameBackgroundCategory

            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            clickListener!!.onItemClick(adapterPosition, v!!)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        var v: View =
            LayoutInflater.from(parent.context).inflate(R.layout.row_category, parent, false)
        var itemHolder = ItemHolder(v)

        return itemHolder
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        var item = lists.get(holder.adapterPosition)

        holder.text_category_name.text = item.name

        when {
            item.name == "Animal" -> {
                holder.frame_background_category.background =
                    ContextCompat.getDrawable(context, R.drawable.border_animal)
                holder.img_category.setImageResource(R.drawable.animal_img)
            }
            item.name == "Flower" -> {
                holder.frame_background_category.background =
                    ContextCompat.getDrawable(context, R.drawable.border_flower)
                holder.img_category.setImageResource(R.drawable.flower_img)
            }
            item.name == "City" -> {
                holder.frame_background_category.background =
                    ContextCompat.getDrawable(context, R.drawable.border_city)
                holder.img_category.setImageResource(R.drawable.city_img)
            }
            item.name == "Sport" -> {
                holder.frame_background_category.background =
                    ContextCompat.getDrawable(context, R.drawable.border_sport)
                holder.img_category.setImageResource(R.drawable.sport_img)
            }
            item.name == "Countryside" -> {
                holder.frame_background_category.background =
                    ContextCompat.getDrawable(context, R.drawable.border_countryside)
                holder.img_category.setImageResource(R.drawable.country_img)
            }
            item.name == "Street" -> {
                holder.frame_background_category.background =
                    ContextCompat.getDrawable(context, R.drawable.border_street)
                holder.img_category.setImageResource(R.drawable.street_img)
            }
            item.name == "Abstract" -> {
                holder.frame_background_category.background =
                    ContextCompat.getDrawable(context, R.drawable.border_abstract)
                holder.img_category.setImageResource(R.drawable.abstract_img)
            }
            item.name == "Friends" -> {
                holder.frame_background_category.background =
                    ContextCompat.getDrawable(context, R.drawable.border_friends)
                holder.img_category.setImageResource(R.drawable.friend_img)
            }
        }


    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return lists.size
    }

    fun setOnItemClickListener(clickListener: ClickListener?) {
        CategoryAdapter.clickListener = clickListener!!
    }

    interface ClickListener {
        fun onItemClick(position: Int, v: View)
    }

}




