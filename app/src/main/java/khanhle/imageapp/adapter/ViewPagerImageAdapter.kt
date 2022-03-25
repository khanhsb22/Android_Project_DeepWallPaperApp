package khanhle.imageapp.adapter

import android.Manifest
import android.app.Activity
import android.app.WallpaperManager
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.squareup.picasso.Picasso
import com.squareup.picasso.Picasso.LoadedFrom
import com.squareup.picasso.Target
import khanhle.imageapp.R
import khanhle.imageapp.model.FavImage
import khanhle.imageapp.model.Image
import khanhle.imageapp.model.Wallpaper
import khanhle.imageapp.repository.*
import khanhle.imageapp.view.activity.ImageActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.lang.Exception
import java.util.*


class ViewPagerImageAdapter : PagerAdapter {

    private var context: Context
    private var list: ArrayList<Image>
    private var layoutInflater: LayoutInflater
    private var favImgdb: FavImageDatabase? = null
    private var albumDb: AlbumDatabase? = null
    private var wallPaperDb: WallpaperDatabase? = null
    private var imgInAlbumDb: ImageInAlbumDatabase? = null
    private var imageActivity: ImageActivity

    constructor(context: Context, list: ArrayList<Image>, imageActivity: ImageActivity) : super() {
        this.context = context
        this.list = list
        this.imageActivity = imageActivity

        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        favImgdb = Room.databaseBuilder(context, FavImageDatabase::class.java, "fav_image_database")
            .fallbackToDestructiveMigration().allowMainThreadQueries().build()

        albumDb = Room.databaseBuilder(context, AlbumDatabase::class.java, "album_database")
            .fallbackToDestructiveMigration().allowMainThreadQueries().build()

        imgInAlbumDb = Room.databaseBuilder(
            context, ImageInAlbumDatabase::class.java,
            "img_in_album_database"
        ).fallbackToDestructiveMigration().allowMainThreadQueries().build()

        wallPaperDb = Room.databaseBuilder(
            context, WallpaperDatabase::class.java,
            "wall_paper_db"
        ).fallbackToDestructiveMigration().allowMainThreadQueries().build()
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == (`object` as FrameLayout)
    }


    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var itemView = layoutInflater.inflate(R.layout.viewpager_image_item, container, false)
        var item = list[position]

        var image_single = itemView.findViewById(R.id.image_single) as ImageView
        var image_share_picture = itemView.findViewById(R.id.image_share_picture) as ImageButton
        var linear_single_image = itemView.findViewById(R.id.linear_single_image) as LinearLayout
        var toggle_fav_image = itemView.findViewById(R.id.toggle_fav_image) as ToggleButton
        var image_add_to_album = itemView.findViewById(R.id.image_add_to_album) as ImageView
        var image_download = itemView.findViewById(R.id.image_download) as ImageView
        var image_set_background = itemView.findViewById(R.id.image_set_background) as ImageView
        var image_back_viewpager = itemView.findViewById(R.id.image_back_viewpager) as ImageButton


        GlobalScope.launch(Dispatchers.IO) {
            GlobalScope.launch(Dispatchers.Main) {
                val requestOptions = RequestOptions()
                requestOptions.placeholder(R.drawable.place_holder)
                Glide.with(context).setDefaultRequestOptions(requestOptions).load(item.filename)
                    .into(image_single)
            }
        }

        checkIsFavorite(item.id, toggle_fav_image)
        checkImageInAlbum(item.id, image_add_to_album)

        image_share_picture.visibility = View.GONE
        linear_single_image.visibility = View.GONE
        image_back_viewpager.visibility = View.GONE

        // Set background image for phone
        image_set_background.setOnClickListener {
            var wallpaperManager =
                WallpaperManager.getInstance(context.applicationContext)
            try {
                var bitmapDrawable: BitmapDrawable =
                    image_single.drawable as BitmapDrawable
                var bitmap: Bitmap = bitmapDrawable.bitmap
                wallpaperManager.setBitmap(bitmap)

                // Delete old field in Wallpaper db and insert 1 field
                deleteAllField()
                insertField(Wallpaper(null, item.id))
                Toast.makeText(
                    context,
                    "Changed your phone background success.",
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        image_single.setOnClickListener {
            imageActivity.checked++
            if (imageActivity.checked % 2 == 0) {
                image_share_picture.visibility = View.GONE
                linear_single_image.visibility = View.GONE
                image_back_viewpager.visibility = View.GONE
            } else {
                image_share_picture.visibility = View.VISIBLE
                image_back_viewpager.visibility = View.VISIBLE

                // Share picture
                // To use share picture, Android device must install latest GooglePlay version
                image_share_picture.setOnClickListener {
                    val mDrawable: Drawable = image_single.getDrawable()
                    val mBitmap = (mDrawable as BitmapDrawable).bitmap

                    val path = Images.Media.insertImage(
                        context.getContentResolver(), mBitmap, "Image Description", null
                    )

                    val uri: Uri = Uri.parse(path)

                    if (uri != null) {
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "image/jpeg"
                        intent.putExtra(Intent.EXTRA_STREAM, uri)
                        context.startActivity(Intent.createChooser(intent, "Share Image"))
                    }
                }

                image_download.setOnClickListener {
                    var bitmapDrawable: BitmapDrawable = image_single.drawable as BitmapDrawable
                    var bitmap: Bitmap = bitmapDrawable.bitmap
                    saveImageToGallery(bitmap)
                }

                image_back_viewpager.setOnClickListener {
                    imageActivity.finish()
                }

                linear_single_image.visibility = View.VISIBLE
            }
        }

        // Hold widgets when click on it
        image_share_picture.setOnClickListener {
            image_share_picture.visibility = View.VISIBLE
            image_back_viewpager.visibility = View.VISIBLE
            linear_single_image.visibility = View.VISIBLE
        }

        linear_single_image.setOnClickListener {
            image_share_picture.visibility = View.VISIBLE
            image_back_viewpager.visibility = View.VISIBLE
            linear_single_image.visibility = View.VISIBLE
        }

        toggle_fav_image.setOnCheckedChangeListener(object :
            CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if (isChecked) {
                    // If imageID doesn't exist insert this image
                    val favImgDAO = favImgdb!!.callDAO()
                    var _imageID = favImgDAO.checkIsFavourite(item.id)
                    if (_imageID != item.id) {
                        insertImage(item.id, item.filename)
                    }
                } else {
                    deleteImage(item.id)
                }
            }

        })

        image_add_to_album.setOnClickListener {
            // Check if the image in the album exists or not
            // If no album exists yet
            val albumDAO = albumDb!!.callDAO()
            val list = albumDAO.getAllAlbum()
            if (list.isEmpty()) {
                val createNewAlbum =
                    DialogCreateNewAlbum(context, item.id, item.filename, image_add_to_album)
                createNewAlbum.showDialog(context)
            } else {
                val imgInAlbumDAO = imgInAlbumDb!!.callDAO()
                var _imageID = imgInAlbumDAO.checkImageInAlbum(item.id)
                if (_imageID != item.id) {
                    val chooseAlbum =
                        DialogChooseAlbum(context, item.id, item.filename, image_add_to_album)
                    chooseAlbum.showDialog(context)
                } else {
                    val questionAlbum =
                        DialogQuestionAlbum(context, item.id, item.filename, image_add_to_album)
                    questionAlbum.showDialog(context)
                }
            }
        }

        Objects.requireNonNull(container).addView(itemView)

        return itemView
    }


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as FrameLayout)
    }

    private fun insertField(wallpaper: Wallpaper) {
        val wallPaperDAO = wallPaperDb!!.callDAO()
        wallPaperDAO.insertImageID(wallpaper)
    }

    private fun deleteAllField() {
        val wallPaperDAO = wallPaperDb!!.callDAO()
        wallPaperDAO.deleteAll()
    }

    private fun insertImage(imageID: String, imageURL: String) {
        var favImg = FavImage(null, imageID, imageURL)
        val favImgDAO = favImgdb!!.callDAO()
        favImgDAO.insertFavImage(favImg)
    }

    private fun deleteImage(imageID: String) {
        val favImgDAO = favImgdb!!.callDAO()
        favImgDAO.deleteFavImage(imageID)
    }

    private fun checkIsFavorite(imageID: String, toggle_fav_image: ToggleButton) {
        val favImgDAO = favImgdb!!.callDAO()
        var _imageID = favImgDAO.checkIsFavourite(imageID)
        if (_imageID == imageID) {
            toggle_fav_image.isChecked = true
        } else {
            toggle_fav_image.isChecked = false
        }
    }

    private fun checkImageInAlbum(imageID: String, image_add_to_album: ImageView) {
        val imgInAlbumDAO = imgInAlbumDb!!.callDAO()
        var _imageID = imgInAlbumDAO.checkImageInAlbum(imageID)
        if (_imageID == imageID) {
            image_add_to_album.setImageResource(R.drawable.ic_added_album)
        } else {
            image_add_to_album.setImageResource(R.drawable.ic_add_to_album)
        }
    }

    private fun saveImageToGallery(bitmap: Bitmap) {
        val fileName = "Image_${Calendar.getInstance().time}"
        var fos: OutputStream?
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val resolver: ContentResolver = context.contentResolver

            val values = ContentValues()
            values.put(Images.Media.TITLE, "$fileName.jpg")
            values.put(Images.Media.DESCRIPTION, fileName)
            values.put(Images.Media.MIME_TYPE, "image/jpeg")
            values.put(Images.Media.DATE_ADDED, System.currentTimeMillis())
            values.put(Images.Media.DATE_TAKEN, System.currentTimeMillis())

            val imageUri: Uri = resolver.insert(Images.Media.EXTERNAL_CONTENT_URI, values)!!
            fos = resolver.openOutputStream(Objects.requireNonNull(imageUri))

            fos.use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }

            fos!!.flush()
            fos.close()

            Toast.makeText(context, "The image has been saved to the gallery.", Toast.LENGTH_SHORT)
                .show()
        } else {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                0
            )
        }
    }

}