package khanhle.imageapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.paperdb.Paper
import khanhle.imageapp.databinding.RowImageOfCategoryBinding
import khanhle.imageapp.model.Image
import khanhle.imageapp.view.activity.ImageOfCategoryActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class ImageOfCategoryAdapter : RecyclerView.Adapter<ImageOfCategoryAdapter.ItemHolder> {

    private var lists: ArrayList<Image>
    private var context: Context
    private var imageOfCategoryActivity: ImageOfCategoryActivity

    companion object {
        var clickListener: ClickListener? = null
    }

    constructor(lists: ArrayList<Image>, context: Context, imageOfCategoryActivity: ImageOfCategoryActivity) : super() {
        this.lists = lists
        this.context = context
        this.imageOfCategoryActivity = imageOfCategoryActivity
    }

    class ItemHolder : RecyclerView.ViewHolder, View.OnClickListener{
        var image_of_category: ImageView
        var linear_border_image: LinearLayout

        constructor(itemView: View) : super(itemView) {
            val binding = RowImageOfCategoryBinding.bind(itemView)
            image_of_category = binding.imageOfCategory
            linear_border_image = binding.linearBorderImage

            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            clickListener!!.onItemClick(adapterPosition, v!!)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        var v: View = LayoutInflater.from(parent.context).inflate(R.layout.row_image_of_category, parent, false)
        var itemHolder = ItemHolder(v)
        Paper.init(context)

        return itemHolder
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        var item = lists.get(holder.adapterPosition)

        // Using Kotlin Coroutine
        GlobalScope.launch (Dispatchers.IO){
            GlobalScope.launch (Dispatchers.Main) {
                val requestOptions = RequestOptions()
                requestOptions.placeholder(R.drawable.place_holder)
                Glide.with(context).setDefaultRequestOptions(requestOptions).load(item.filename).into(holder.image_of_category)
            }
        }

        // Get from ImageActivity
        var imageID = Paper.book().read("imageID", "0")

        if (item.id == imageID) {
            holder.linear_border_image.setBackgroundResource(R.drawable.custom_border_selected)
        } else {
            holder.linear_border_image.setBackgroundResource(R.drawable.custom_border_none)
        }


    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return lists.size
    }

    fun setOnItemClickListener(clickListener: ClickListener?) {
        ImageOfCategoryAdapter.clickListener = clickListener!!
    }

    interface ClickListener {
        fun onItemClick(position: Int, v: View)
    }

}
