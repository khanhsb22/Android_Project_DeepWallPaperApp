import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.room.Room
import io.paperdb.Paper
import khanhle.imageapp.FavImageAdapter
import khanhle.imageapp.R
import khanhle.imageapp.databinding.FragmentFavoriteBinding
import khanhle.imageapp.model.FavImage
import khanhle.imageapp.repository.FavImageDatabase

class FavoriteFragment : Fragment() {

    private lateinit var v: View
    private lateinit var binding: FragmentFavoriteBinding
    private var db: FavImageDatabase? = null
    private lateinit var favImageAdapter: FavImageAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_favorite, container, false)
        binding = FragmentFavoriteBinding.bind(v)

        Paper.init(context)

        Paper.book().delete("scrolling_value")

        if (db == null) {
            db = Room.databaseBuilder(requireContext(), FavImageDatabase::class.java, "fav_image_database")
                .fallbackToDestructiveMigration().allowMainThreadQueries().build()
        }

        setupRecyclerView()

        if (db != null) {
            getAllImage()
        }

        checkEmptyFav()

        Paper.init(context)


        return v
    }

    private fun setupRecyclerView() {
        binding.recyclerFavImg.setHasFixedSize(true)
        var layoutManager = GridLayoutManager(context, 2)
        binding.recyclerFavImg.layoutManager = layoutManager
    }

    fun getAllImage() {
        val favImgDAO = db!!.callDAO()
        var imgList: List<FavImage> = favImgDAO.getAllFavImage()
        var images = ArrayList<FavImage>()

        for (item in imgList) {
            images.add(FavImage(item.favImageID, item.imageID, item.imageUrl))
        }

        favImageAdapter = FavImageAdapter(images, requireContext(), binding.fabDeleteAllFav,
            binding.imageCloseFav, binding.textTitleFav, binding.textCheckAll,this@FavoriteFragment,
            binding.linearEmptyFav, binding.recyclerFavImg)

        binding.recyclerFavImg.adapter = favImageAdapter
    }

    fun checkEmptyFav() {
        val favImgDAO = db!!.callDAO()
        var count = favImgDAO.getRowCount()
        if (count == 0) {
            binding.recyclerFavImg.visibility = View.GONE
            binding.linearEmptyFav.visibility = View.VISIBLE
        }
    }

    override fun onStart() {
        super.onStart()
        getAllImage()

        // Get from FavImageAdapter
        val scrolling_value = Paper.book().read("scrolling_value", -1)

        if (scrolling_value != -1) {
            if (scrolling_value != 0 && scrolling_value != 1) {
                favImageAdapter.notifyDataSetChanged()
                binding.recyclerFavImg.scrollToPosition(scrolling_value - 2)
            } else {
                favImageAdapter.notifyDataSetChanged()
                binding.recyclerFavImg.scrollToPosition(scrolling_value)
            }
        }

    }


}