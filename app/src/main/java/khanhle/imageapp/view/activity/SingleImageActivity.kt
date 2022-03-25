package khanhle.imageapp.view.activity

import android.app.WallpaperManager
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.squareup.picasso.Picasso
import khanhle.imageapp.R
import khanhle.imageapp.databinding.ActivitySingleImageBinding
import khanhle.imageapp.model.FavImage
import khanhle.imageapp.model.Wallpaper
import khanhle.imageapp.repository.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*

class SingleImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySingleImageBinding
    private var filename = ""
    private var imageID = ""
    private var click = 0
    private var favImgdb: FavImageDatabase? = null
    private var albumDb: AlbumDatabase? = null
    private var imgInAlbumDb: ImageInAlbumDatabase? = null
    private var wallPaperDb: WallpaperDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingleImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get from FavImageAdapter, ImageInAlbumAdapter
        filename = intent.getStringExtra("filename").toString()
        imageID = intent.getStringExtra("id").toString()

        setSingleImage(filename)
        handleWithEvents()

        favImgdb = Room.databaseBuilder(
            applicationContext,
            FavImageDatabase::class.java,
            "fav_image_database"
        )
            .fallbackToDestructiveMigration().allowMainThreadQueries().build()

        albumDb =
            Room.databaseBuilder(applicationContext, AlbumDatabase::class.java, "album_database")
                .fallbackToDestructiveMigration().allowMainThreadQueries().build()

        imgInAlbumDb = Room.databaseBuilder(
            this@SingleImageActivity, ImageInAlbumDatabase::class.java,
            "img_in_album_database"
        ).fallbackToDestructiveMigration().allowMainThreadQueries().build()

        wallPaperDb = Room.databaseBuilder(
            this@SingleImageActivity, WallpaperDatabase::class.java,
            "wall_paper_db"
        ).fallbackToDestructiveMigration().allowMainThreadQueries().build()

        checkIsFavorite()
        checkImageInAlbum()

        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.BLACK
    }

    private fun setSingleImage(_fileName: String) {
        GlobalScope.launch (Dispatchers.IO){
            GlobalScope.launch (Dispatchers.Main) {
                val requestOptions = RequestOptions()
                requestOptions.placeholder(R.drawable.place_holder)
                Glide.with(this@SingleImageActivity).setDefaultRequestOptions(requestOptions).load(_fileName).into(binding.imageSingle)
            }
        }
    }

    private fun deleteAllField() {
        val wallPaperDAO = wallPaperDb!!.callDAO()
        wallPaperDAO.deleteAll()
    }

    private fun insertField(wallpaper: Wallpaper) {
        val wallPaperDAO = wallPaperDb!!.callDAO()
        wallPaperDAO.insertImageID(wallpaper)
    }

    private fun handleWithEvents() {
        binding.imageShare.visibility = View.GONE
        binding.linearSingleImage.visibility = View.GONE
        binding.imageBackSingleImage.visibility = View.GONE

        binding.imageSetBackground.setOnClickListener {
            var wallpaperManager =
                WallpaperManager.getInstance(this@SingleImageActivity)
            try {
                var bitmapDrawable: BitmapDrawable =
                    binding.imageSingle.drawable as BitmapDrawable
                var bitmap: Bitmap = bitmapDrawable.bitmap
                wallpaperManager.setBitmap(bitmap)

                // Delete old field in Wallpaper db and insert 1 field
                deleteAllField()
                insertField(Wallpaper(null, imageID))
                Toast.makeText(
                    this@SingleImageActivity,
                    "Changed your phone background success.",
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        binding.imageSingle.setOnClickListener {
            click++
            if (click % 2 == 0) {
                binding.imageShare.visibility = View.GONE
                binding.linearSingleImage.visibility = View.GONE
                binding.imageBackSingleImage.visibility = View.GONE
            } else {
                binding.imageShare.visibility = View.VISIBLE
                binding.linearSingleImage.visibility = View.VISIBLE
                binding.imageBackSingleImage.visibility = View.VISIBLE

                binding.imageShare.setOnClickListener {
                    binding.imageShare.visibility = View.VISIBLE
                    binding.linearSingleImage.visibility = View.VISIBLE
                    binding.imageBackSingleImage.visibility = View.VISIBLE

                    // Share picture
                    // To use share picture, Android device must install latest GooglePlay version
                    val mDrawable: Drawable = binding.imageSingle.getDrawable()
                    val mBitmap = (mDrawable as BitmapDrawable).bitmap

                    val path = MediaStore.Images.Media.insertImage(
                        getContentResolver(), mBitmap, "Image Description", null
                    )

                    val uri: Uri = Uri.parse(path)

                    if (uri != null) {
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "image/jpeg"
                        intent.putExtra(Intent.EXTRA_STREAM, uri)
                        startActivity(Intent.createChooser(intent, "Share Image"))
                    }
                }

                binding.imageDownload.setOnClickListener {
                    var bitmapDrawable: BitmapDrawable = binding.imageSingle.drawable as BitmapDrawable
                    var bitmap: Bitmap = bitmapDrawable.bitmap
                    var bytes = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                    MediaStore.Images.Media.insertImage(
                        contentResolver,
                        bitmap,
                        "Image_" + Calendar.getInstance().time,
                        null
                    )
                    Toast.makeText(
                        this@SingleImageActivity,
                        "The image has been saved to the gallery.",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

                binding.imageBackSingleImage.setOnClickListener {
                    finish()
                }
            }
        }

        binding.linearSingleImage.setOnClickListener {
            binding.imageShare.visibility = View.VISIBLE
            binding.imageBackSingleImage.visibility = View.VISIBLE
            binding.linearSingleImage.visibility = View.VISIBLE
        }

        binding.toggleFavImage.setOnCheckedChangeListener(object :
            CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if (isChecked) {
                    // If imageID doesn't exist insert this image
                    val favImgDAO = favImgdb!!.callDAO()
                    var _imageID = favImgDAO.checkIsFavourite(imageID)
                    if (_imageID != imageID) {
                        insertImage()
                    }
                } else {
                    deleteImage()
                }
            }

        })

        binding.imageAddToAlbum.setOnClickListener {
            // Check if the image in the album exists or not
            // If no album exists yet
            val albumDAO = albumDb!!.callDAO()
            val list = albumDAO.getAllAlbum()
            if (list.isEmpty()) {
                val createNewAlbum =
                    DialogCreateNewAlbum(this@SingleImageActivity, imageID, filename, binding.imageAddToAlbum)
                createNewAlbum.showDialog(this@SingleImageActivity)
            } else {
                val imgInAlbumDAO = imgInAlbumDb!!.callDAO()
                var _imageID = imgInAlbumDAO.checkImageInAlbum(imageID)
                if (_imageID != imageID) {
                    val chooseAlbum = DialogChooseAlbum(
                        this@SingleImageActivity,
                        imageID,
                        filename,
                        binding.imageAddToAlbum
                    )
                    chooseAlbum.showDialog(this@SingleImageActivity)
                } else {
                    val questionAlbum = DialogQuestionAlbum(
                        this@SingleImageActivity,
                        imageID,
                        filename,
                        binding.imageAddToAlbum
                    )
                    questionAlbum.showDialog(this@SingleImageActivity)
                }
            }
        }

    }

    private fun checkIsFavorite() {
        val favImgDAO = favImgdb!!.callDAO()
        var _imageID = favImgDAO.checkIsFavourite(imageID)
        if (_imageID == imageID) {
            binding.toggleFavImage.isChecked = true
        } else {
            binding.toggleFavImage.isChecked = false
        }
    }

    private fun checkImageInAlbum() {
        val imgInAlbumDAO = imgInAlbumDb!!.callDAO()
        var _imageID = imgInAlbumDAO.checkImageInAlbum(imageID)
        if (_imageID == imageID) {
            binding.imageAddToAlbum.setImageResource(R.drawable.ic_added_album)
        } else {
            binding.imageAddToAlbum.setImageResource(R.drawable.ic_add_to_album)
        }
    }

    private fun insertImage() {
        var favImg = FavImage(null, imageID, filename)
        val favImgDAO = favImgdb!!.callDAO()
        favImgDAO.insertFavImage(favImg)
    }

    private fun deleteImage() {
        val favImgDAO = favImgdb!!.callDAO()
        favImgDAO.deleteFavImage(imageID)
    }


}
