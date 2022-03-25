import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import io.paperdb.Paper
import khanhle.imageapp.MenuAdapter
import khanhle.imageapp.R
import khanhle.imageapp.databinding.FragmentMenuBinding
import khanhle.imageapp.model.Menu
import khanhle.imageapp.repository.*
import khanhle.imageapp.view.activity.YourAlbumActivity

class MenuFragment : Fragment() {

    private lateinit var v: View
    private lateinit var binding: FragmentMenuBinding
    private var addImageFromGalleryResultLauncher: ActivityResultLauncher<Intent>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_menu, container, false)
        binding = FragmentMenuBinding.bind(v)

        loadData()

        Paper.init(context)

        return v
    }

    private fun setupRecyclerView() {
        binding.recyclerMenu.setHasFixedSize(true)
        var layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerMenu.layoutManager = layoutManager

    }

    private fun loadData() {
        setupRecyclerView()

        val list = ArrayList<Menu>()
        list.add(Menu("Your album", R.drawable.ic_baseline_photo))
        list.add(Menu("Pick image from gallery", R.drawable.ic_baseline_folder))
        list.add(Menu("Set auto change wallpaper", R.drawable.ic_baseline_auto_change))
        list.add(Menu("Upgrade to Premium", R.drawable.ic_baseline_upgrade))
        list.add(Menu("About app", R.drawable.ic_baseline_info))

        val menuAdapter = MenuAdapter(list)
        binding.recyclerMenu.adapter = menuAdapter

        menuAdapter.setOnItemClickListener(object : MenuAdapter.ClickListener {
            override fun onItemClick(position: Int, v: View) {
                when (position) {
                    0 -> {
                        startActivity(Intent(context, YourAlbumActivity::class.java))
                        Paper.book().delete("Choose album for note")
                    }
                    1 -> {
                        // Start ActivityResult for chooser multiple image
                        // Long press to select multiple image
                        val intent = Intent()
                        intent.type = "image/*"
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                        intent.action = Intent.ACTION_GET_CONTENT
                        addImageFromGalleryResultLauncher!!.launch(intent)
                    }
                    2 -> {
                        val dialogAutoChangeWallPaper = DialogAutoChangeWallPaper(context!!)
                        dialogAutoChangeWallPaper.showDialog(context!!)
                    }
                    3 -> {
                        val dialogUpgradePremium = DialogUpgradePremium()
                        dialogUpgradePremium.showDialog(context!!)
                    }
                }
            }
        })

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        registerForAddImageFomGalleryActivityResult()
    }

    private fun registerForAddImageFomGalleryActivityResult() {
        addImageFromGalleryResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), object :
            ActivityResultCallback<ActivityResult> {

            override fun onActivityResult(result: ActivityResult?) {
                if (result!!.resultCode == Activity.RESULT_OK) {
                    var data = result.data
                    if (data!!.clipData != null) {
                        // For select multiple image
                        var listImageUri = ArrayList<String>()
                        var count = data.clipData!!.itemCount
                        for (i in 0 until count) {
                            var imageUri: Uri = data.clipData!!.getItemAt(i).uri
                            listImageUri.add(imageUri.toString())
                        }
                        Paper.book().delete("albumName")
                        val dialogSaveExternalImageInAlbum = DialogSaveExternalImageInAlbum(context!!, listImageUri)
                        dialogSaveExternalImageInAlbum.showDialog(context!!)
                    } else if(data.data != null) {
                        // For select one image
                        Paper.book().delete("albumName")
                        var imageUri: Uri = data.data!!
                        val dialogSaveExternalImageInAlbum = DialogSaveExternalImageInAlbum(context!!, imageUri.toString())
                        dialogSaveExternalImageInAlbum.showDialog(context!!)
                    }
                }
            }
        })
    }

}