package khanhle.imageapp.view.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.paperdb.Paper
import khanhle.imageapp.ImageOfCategoryAdapter
import khanhle.imageapp.R
import khanhle.imageapp.databinding.ActivityImageOfCategoryBinding
import khanhle.imageapp.model.Image
import khanhle.imageapp.viewmodel.ImageOfCategoryViewModel
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.NestedScrollView
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import khanhle.imageapp.repository.CheckConnectInternet
import khanhle.imageapp.repository.DialogCheckConnectInternet
import android.util.TypedValue
import khanhle.imageapp.PremiumImageOfCategoryAdapter


class ImageOfCategoryActivity : AppCompatActivity() {

    private lateinit var images: List<Image>
    private lateinit var premiumImages: List<Image>
    private lateinit var imageOfCategoryAdapter: ImageOfCategoryAdapter
    private lateinit var premiumImageOfCategoryAdapter: PremiumImageOfCategoryAdapter
    private var category_name = ""
    private lateinit var binding: ActivityImageOfCategoryBinding
    private var premium = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageOfCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        category_name = intent.getStringExtra("category_name")!! // Get from CategoryFragment
        binding.textCategoryName2.text = category_name

        Paper.init(this@ImageOfCategoryActivity)

        // Delete Paper in ImageActivity
        Paper.book().delete("imageID")
        Paper.book().delete("position")

        // Get from DialogUpgradePremium
        premium = Paper.book().read("premium_state", false)

        handleWithView()
        handleWithEvent()

        if (premium) {
            // For premium user
            observerDataPremium()
            observerData()
        } else {
            // For normal user
            observerData()
        }


    }


    private fun handleWithView() {
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.BLACK

        binding.fabScrollUp.visibility = View.GONE
    }

    private fun handleWithEvent() {
        binding.imageBack.setOnClickListener {
            finish()
        }

        binding.nestedScrollView.isSmoothScrollingEnabled = false

        binding.nestedScrollView.setOnScrollChangeListener(object : NestedScrollView.OnScrollChangeListener{
            override fun onScrollChange(
                v: NestedScrollView?,
                scrollX: Int,
                scrollY: Int,
                oldScrollX: Int,
                oldScrollY: Int
            ) {
                if (scrollY > oldScrollY) {
                    // When scroll down
                    binding.fabScrollUp.visibility = View.GONE
                } else if (scrollY < oldScrollY) {
                    binding.fabScrollUp.visibility = View.VISIBLE
                    Handler().postDelayed(object : Runnable {
                        override fun run() {
                            binding.fabScrollUp.visibility = View.GONE
                        }
                    }, 1500)
                }
            }
        })

        binding.fabScrollUp.setOnClickListener {
            binding.nestedScrollView.smoothScrollTo(0, 0)
        }
    }

    private fun setupRecyclerView() {
        var params: LinearLayout.LayoutParams = binding.recyclerImageOfCategory.layoutParams as LinearLayout.LayoutParams
        if (!premium) {
            params.setMargins(0, 16, 0, 5)
        } else {
            params.setMargins(0, 64, 0, 5)
        }
        binding.recyclerImageOfCategory.layoutParams = params

        binding.recyclerImageOfCategory.setHasFixedSize(true)
        var layoutManager = GridLayoutManager(this@ImageOfCategoryActivity, 2)
        binding.recyclerImageOfCategory.layoutManager = layoutManager
    }

    private fun setupPremiumRecyclerView() {
        binding.textPremiumTitle.visibility = View.VISIBLE
        binding.recyclerImageOfCategoryPremium.visibility = View.VISIBLE
        binding.dividerView.visibility = View.VISIBLE

        var params: LinearLayout.LayoutParams = binding.dividerView.layoutParams as LinearLayout.LayoutParams
        params.setMargins(0, 64, 0, 5)
        binding.dividerView.layoutParams = params

        var params2: LinearLayout.LayoutParams = binding.textPremiumTitle.layoutParams as LinearLayout.LayoutParams
        params2.setMargins(12, 32, 0, 32)
        binding.textPremiumTitle.layoutParams = params2

        binding.recyclerImageOfCategoryPremium.setHasFixedSize(true)
        var layoutManager = GridLayoutManager(this@ImageOfCategoryActivity, 2)
        binding.recyclerImageOfCategoryPremium.layoutManager = layoutManager

    }

    private fun observerData() {
        val model = ViewModelProvider(this@ImageOfCategoryActivity).get(ImageOfCategoryViewModel::class.java)
        model.getImages(category_name).observe(this@ImageOfCategoryActivity, object :
            Observer<List<Image>> {
            override fun onChanged(imageList: List<Image>?) {
                if (imageList != null) {
                    setupRecyclerView()
                    images = imageList
                    imageOfCategoryAdapter = ImageOfCategoryAdapter(images as ArrayList<Image>, this@ImageOfCategoryActivity,
                        this@ImageOfCategoryActivity)
                    binding.recyclerImageOfCategory.adapter = imageOfCategoryAdapter

                    imageOfCategoryAdapter.setOnItemClickListener(object : ImageOfCategoryAdapter.ClickListener{
                        override fun onItemClick(position: Int, v: View) {
                            var intent = Intent(this@ImageOfCategoryActivity, ImageActivity::class.java)
                            intent.putExtra("ImageList", images as ArrayList<Image>)
                            intent.putExtra("current_position", position) // Current position in images, used for ImageActivity
                            startActivity(intent)
                        }
                    })
                }
            }
        })
    }

    private fun observerDataPremium() {
        val model = ViewModelProvider(this@ImageOfCategoryActivity).get(ImageOfCategoryViewModel::class.java)
        model.getPremiumImages(category_name).observe(this@ImageOfCategoryActivity, object :
            Observer<List<Image>> {
            override fun onChanged(imageList: List<Image>?) {
                if (imageList != null) {
                    setupPremiumRecyclerView()
                    premiumImages = imageList
                    premiumImageOfCategoryAdapter = PremiumImageOfCategoryAdapter(premiumImages as ArrayList<Image>, this@ImageOfCategoryActivity,
                        this@ImageOfCategoryActivity)
                    binding.recyclerImageOfCategoryPremium.adapter = premiumImageOfCategoryAdapter

                    premiumImageOfCategoryAdapter.setOnItemClickListener(object : PremiumImageOfCategoryAdapter.ClickListener{
                        override fun onItemClick(position: Int, v: View) {
                            var intent = Intent(this@ImageOfCategoryActivity, ImageActivity::class.java)
                            intent.putExtra("ImageList", premiumImages as ArrayList<Image>)
                            intent.putExtra("current_position", position) // Current position in images, used for ImageActivity
                            intent.putExtra("_premium", true)
                            startActivity(intent)
                        }
                    })
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        // Get from ImageActivity
        val imageID = Paper.book().read("imageID", "0")
        val position = Paper.book().read("position", -1)
        val _premium = Paper.book().read("_premium", false)

        if (imageID != "0" && position != -1 && _premium == false) {
            imageOfCategoryAdapter.notifyDataSetChanged()
            binding.recyclerImageOfCategory.post {
                val y: Float = binding.recyclerImageOfCategory.y + binding.recyclerImageOfCategory.getChildAt(position).y
                binding.nestedScrollView.smoothScrollTo(0, y.toInt())
            }
        }
        if (imageID != "0" && position != -1 && _premium != false) {
            premiumImageOfCategoryAdapter.notifyDataSetChanged()
            binding.recyclerImageOfCategoryPremium.post {
                val y = binding.recyclerImageOfCategoryPremium.y + binding.recyclerImageOfCategoryPremium.getChildAt(position).y
                binding.nestedScrollView.smoothScrollTo(0, y.toInt())
            }
        }
    }

}


















