package khanhle.imageapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.paperdb.Paper
import khanhle.imageapp.databinding.RowImageOfCategoryBinding
import khanhle.imageapp.databinding.RowPremiumImageOfCategoryBinding
import khanhle.imageapp.model.Image
import khanhle.imageapp.view.activity.ImageOfCategoryActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class PremiumImageOfCategoryAdapter : RecyclerView.Adapter<PremiumImageOfCategoryAdapter.ItemHolder> {

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
        var imageOfCategoryPremium: ImageView
        var linearBorderImagePremium: LinearLayout

        constructor(itemView: View) : super(itemView) {
            val binding = RowPremiumImageOfCategoryBinding.bind(itemView)
            imageOfCategoryPremium = binding.imageOfCategoryPremium
            linearBorderImagePremium = binding.linearBorderImagePremium

            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            clickListener!!.onItemClick(adapterPosition, v!!)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        var v: View = LayoutInflater.from(parent.context).inflate(R.layout.row_premium_image_of_category, parent, false)
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
                Glide.with(context).setDefaultRequestOptions(requestOptions).load(item.filename).into(holder.imageOfCategoryPremium)
            }
        }

        // Get from ImageActivity
        var imageID = Paper.book().read("imageID", "0")

        if (item.id == imageID) {
            holder.linearBorderImagePremium.setBackgroundResource(R.drawable.custom_border_selected)
        } else {
            holder.linearBorderImagePremium.setBackgroundResource(R.drawable.custom_border_none)
        }


    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return lists.size
    }

    fun setOnItemClickListener(clickListener: ClickListener?) {
        PremiumImageOfCategoryAdapter.clickListener = clickListener!!
    }

    interface ClickListener {
        fun onItemClick(position: Int, v: View)
    }

}
