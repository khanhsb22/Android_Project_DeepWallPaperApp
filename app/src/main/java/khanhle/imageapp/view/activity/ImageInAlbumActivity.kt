package khanhle.imageapp.view.activity

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.room.Room
import io.paperdb.Paper
import khanhle.imageapp.AlbumAdapter
import khanhle.imageapp.ImageInAlbumAdapter
import khanhle.imageapp.R
import khanhle.imageapp.databinding.ActivityImageInAlbumBinding
import khanhle.imageapp.model.Image
import khanhle.imageapp.model.ImageInAlbum
import khanhle.imageapp.repository.ImageInAlbumDatabase

class ImageInAlbumActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImageInAlbumBinding
    private var imgInAlbumDb: ImageInAlbumDatabase? = null
    private var albumName = ""
    private lateinit var imageInAlbumAdapter: ImageInAlbumAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageInAlbumBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Paper.book().delete("scrolling_value_img_in_album")

        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.BLACK

        imgInAlbumDb = Room.databaseBuilder(
            this@ImageInAlbumActivity, ImageInAlbumDatabase::class.java,
            "img_in_album_database"
        ).fallbackToDestructiveMigration().allowMainThreadQueries().build()

        albumName = intent.getStringExtra("albumName").toString()

        getAllImage(albumName)

        checkEmptyImage()

        Paper.init(this@ImageInAlbumActivity)

        handleWithEvent()
    }

    private fun handleWithEvent() {
        binding.imageBack.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerImageAlbum.setHasFixedSize(true)
        var layoutManager = GridLayoutManager(this@ImageInAlbumActivity, 3)
        binding.recyclerImageAlbum.layoutManager = layoutManager
    }

    fun getAllImage(albumName: String) {
        setupRecyclerView()

        val imgInAlbumDAO = imgInAlbumDb!!.callDAO()
        val list: List<ImageInAlbum> = imgInAlbumDAO.getAllImage(albumName)
        var imageInAlbums = ArrayList<ImageInAlbum>()

        for (item in list) {
            imageInAlbums.add(ImageInAlbum(null, item.imageID, item.albumName, item.imageUrl))
        }

        imageInAlbumAdapter = ImageInAlbumAdapter(imageInAlbums, this@ImageInAlbumActivity,
        binding.textTitleImageInAlbum, binding.imageCloseImagesInAlbum, binding.textSelectAllImageInAlbum,
        binding.fabDeleteImagesInAlbum, this@ImageInAlbumActivity, binding.imageBack,
        binding.recyclerImageAlbum, binding.linearEmptyImageInAlbum, albumName)

        binding.recyclerImageAlbum.adapter = imageInAlbumAdapter

    }

    private fun checkEmptyImage() {
        val imageInAlbumDAO = imgInAlbumDb!!.callDAO()
        var count = imageInAlbumDAO.getRowCount(albumName)
        if (count == 0) {
            binding.recyclerImageAlbum.visibility = View.GONE
            binding.linearEmptyImageInAlbum.visibility = View.VISIBLE
        }
    }

    override fun onStart() {
        super.onStart()
        getAllImage(albumName)

        // Get from ImageInAlbumAdapter
        val scrolling_value = Paper.book().read("scrolling_value_img_in_album", -1)
        if (scrolling_value != -1) {
            imageInAlbumAdapter.notifyDataSetChanged()
            binding.recyclerImageAlbum.scrollToPosition(scrolling_value)
        }

    }
}