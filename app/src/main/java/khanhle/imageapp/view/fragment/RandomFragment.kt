import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.paperdb.Paper
import khanhle.imageapp.R
import khanhle.imageapp.RandomImageAdapter
import khanhle.imageapp.databinding.FragmentRandomBinding
import khanhle.imageapp.model.Image
import khanhle.imageapp.view.activity.ImageActivity
import khanhle.imageapp.viewmodel.RandomImageViewModel
import java.util.*

class RandomFragment : Fragment() {

    lateinit var v: View

    private lateinit var binding: FragmentRandomBinding
    private lateinit var images: List<Image>
    private lateinit var randomImageAdapter: RandomImageAdapter
    private var premium = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_random, container, false)
        binding = FragmentRandomBinding.bind(v)

        Paper.init(context)

        // Delete Paper use in ImageActivity
        Paper.book().delete("imageID")
        Paper.book().delete("position")

        // Get from DialogUpgradePremium
        premium = Paper.book().read("premium_state", false)

        if (premium) {
            observerDataPremium()
        } else {
            observerData()
        }

        binding.swipeRefresh.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                Handler().postDelayed(object : Runnable {
                    override fun run() {
                        observerData()
                        binding.swipeRefresh.isRefreshing = false
                    }
                }, 500)
            }
        })

        binding.fabScrollUp.visibility = View.GONE

        binding.recyclerRandomImage.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    // When scroll down
                    binding.fabScrollUp.visibility = View.GONE
                } else if (dy < 0) {
                    // When scroll up
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
            binding.recyclerRandomImage.smoothScrollToPosition(0)
        }

        return v
    }


    override fun onStart() {
        super.onStart()
        // Get from ImageActivity
        val imageID = Paper.book().read("imageID", "0")
        val position = Paper.book().read("position", -1)
        if (imageID != "0" && position != -1) {
            randomImageAdapter.notifyDataSetChanged()
            binding.recyclerRandomImage.smoothScrollToPosition(position)
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerRandomImage.setHasFixedSize(true)
        var layoutManager = GridLayoutManager(context, 3)
        binding.recyclerRandomImage.layoutManager = layoutManager
    }

    private fun observerData() {
        val model = ViewModelProvider(requireActivity()).get(RandomImageViewModel::class.java)
        model.getImages().observe(requireActivity(), object : Observer<List<Image>> {
            override fun onChanged(imageList: List<Image>?) {
                if (imageList != null) {
                    setupRecyclerView()
                    images = imageList
                    randomImageAdapter = RandomImageAdapter(images as ArrayList<Image>, context!!)
                    binding.recyclerRandomImage.adapter = randomImageAdapter
                    randomImageAdapter.setOnItemClickListener(object :
                        RandomImageAdapter.ClickListener {
                        override fun onItemClick(position: Int, v: View) {
                            var intent = Intent(context, ImageActivity::class.java)
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
        val model = ViewModelProvider(requireActivity()).get(RandomImageViewModel::class.java)
        model.getAllImages().observe(requireActivity(), object : Observer<List<Image>> {
            override fun onChanged(imageList: List<Image>?) {
                if (imageList != null) {
                    setupRecyclerView()
                    images = imageList
                    randomImageAdapter = RandomImageAdapter(images as ArrayList<Image>, context!!)
                    binding.recyclerRandomImage.adapter = randomImageAdapter
                    randomImageAdapter.setOnItemClickListener(object :
                        RandomImageAdapter.ClickListener {
                        override fun onItemClick(position: Int, v: View) {
                            var intent = Intent(context, ImageActivity::class.java)
                            intent.putExtra("ImageList", images as ArrayList<Image>)
                            intent.putExtra("current_position", position) // Current position in images, used for ImageActivity
                            startActivity(intent)
                        }
                    })
                }
            }
        })
    }

}