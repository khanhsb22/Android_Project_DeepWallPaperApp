package khanhle.imageapp.view.activity

import android.Manifest
import android.app.WallpaperManager
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import io.paperdb.Paper
import khanhle.imageapp.adapter.ViewPagerImageAdapter
import khanhle.imageapp.databinding.ActivityImageBinding
import khanhle.imageapp.model.Image
import khanhle.imageapp.repository.CheckConnectInternet
import khanhle.imageapp.repository.DialogCheckConnectInternet

class ImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImageBinding
    private lateinit var imageList: ArrayList<Image>
    private lateinit var viewPagerImageAdapter: ViewPagerImageAdapter
    private var current_position = -1
    var checked = 0
    private var imageID = ""
    private var premium = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Paper.init(this@ImageActivity)
        Paper.book().delete("imageID")
        Paper.book().delete("_premium")

        imageList = ArrayList()

        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.BLACK

        // Get from ImageOfCategoryActivity, RandomFragment
        imageList = intent.getSerializableExtra("ImageList") as ArrayList<Image>
        current_position = intent.getIntExtra("current_position", -1)
        premium = intent.getBooleanExtra("_premium", false)

        if (premium) {
            // Use for onStart() ImageOfCategoryActivity
            Paper.book().write("_premium", true)
        }

        viewPagerImageAdapter = ViewPagerImageAdapter(this@ImageActivity, imageList,
            this@ImageActivity)
        binding.viewPagerImage.adapter = viewPagerImageAdapter
        binding.viewPagerImage.currentItem = current_position


        // Using in ImageOfCategoryAdapter, RandomFragment
        // when clicking on image from activity and fragment
        Paper.book().write("imageID", imageList[current_position].id)
        Paper.book().write("position", current_position)

        binding.viewPagerImage.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                checked = 0 // Using in ViewPagerImageAdapter
                imageID = imageList[position].id

                // Using in ImageOfCategoryAdapter, RandomFragment
                Paper.book().write("imageID", imageID)
                Paper.book().write("position", position)
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })


    }






}