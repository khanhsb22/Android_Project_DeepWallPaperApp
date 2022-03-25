package khanhle.imageapp.view.activity

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.room.Room
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import io.paperdb.Paper
import khanhle.imageapp.AlbumAdapter
import khanhle.imageapp.R
import khanhle.imageapp.databinding.ActivityYourAlbumBinding
import khanhle.imageapp.model.Album
import khanhle.imageapp.repository.AlbumDatabase
import khanhle.imageapp.repository.DialogCreateNewAlbum

class YourAlbumActivity : AppCompatActivity() {

    private lateinit var binding: ActivityYourAlbumBinding
    private var albumDb: AlbumDatabase? = null
    private lateinit var albumAdapter: AlbumAdapter
    private var chooseAlbumForNote = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityYourAlbumBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Paper.init(this@YourAlbumActivity)

        Paper.book().delete("scrolling_value_album")
        Paper.book().delete("Album_created")
        chooseAlbumForNote = Paper.book().read("Choose album for note", "false")

        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.BLACK

        albumDb =
            Room.databaseBuilder(applicationContext, AlbumDatabase::class.java, "album_database")
                .fallbackToDestructiveMigration().allowMainThreadQueries().build()
        getAllAlbum()
        checkEmptyAlbum()
        handleWithEvents()
        handleWithViews()

    }

    private fun setupRecyclerView() {
        binding.recyclerAlbum.setHasFixedSize(true)
        var layoutManager = GridLayoutManager(this@YourAlbumActivity, 2)
        binding.recyclerAlbum.layoutManager = layoutManager
    }

    fun getAllAlbum() {
        setupRecyclerView()

        val albumDAO = albumDb!!.callDAO()
        var list: List<Album> = albumDAO.getAllAlbum()
        var albums = ArrayList<Album>()

        for (item in list) {
            albums.add(Album(null, item.albumName))
        }

        albumAdapter = AlbumAdapter(
            albums, this@YourAlbumActivity, binding.textTitleAlbum, binding.imageCloseAlbum,
            binding.textSelectAllAlbum, binding.fabDeleteAlbum, binding.fabAddNewAlbum,
            this@YourAlbumActivity, binding.imageBack, binding.recyclerAlbum, binding.linearEmptyAlbum
        )
        binding.recyclerAlbum.adapter = albumAdapter

        // Get from DialogCreateNewAlbum
        var album_created = Paper.book().read("Album_created", "false")
        if (album_created == "true") {
            binding.recyclerAlbum.visibility = View.VISIBLE
            binding.linearEmptyAlbum.visibility = View.GONE
        }
    }

    private fun checkEmptyAlbum() {
        val albumDAO = albumDb!!.callDAO()
        var count = albumDAO.getRowCount()
        if (count == 0) {
            binding.recyclerAlbum.visibility = View.GONE
            binding.linearEmptyAlbum.visibility = View.VISIBLE
        }
    }

    private fun handleWithEvents() {
        binding.fabAddNewAlbum.setOnClickListener {
            val createNewAlbum =
                DialogCreateNewAlbum(this@YourAlbumActivity, this@YourAlbumActivity)
            createNewAlbum.showDialog2(this@YourAlbumActivity)
        }

        binding.imageBack.setOnClickListener {
            finish()
        }
    }

    private fun handleWithViews() {
        if (chooseAlbumForNote == "true") {
            binding.textTitleAlbum.visibility = View.GONE
            binding.imageBack.visibility = View.GONE
            binding.textChooseAlbumForNote.visibility = View.VISIBLE
            binding.fabAddNewAlbum.visibility = View.GONE
        }
    }

    override fun onStart() {
        super.onStart()
        getAllAlbum()

        // Get from AlbumAdapter
        val scrolling_value = Paper.book().read("scrolling_value_album", -1)
        if (scrolling_value != -1) {
            albumAdapter.notifyDataSetChanged()
            binding.recyclerAlbum.scrollToPosition(scrolling_value)
        }

    }

}