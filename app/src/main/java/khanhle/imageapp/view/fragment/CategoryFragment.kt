import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import khanhle.imageapp.CategoryAdapter
import khanhle.imageapp.R
import khanhle.imageapp.databinding.FragmentCategoryBinding
import khanhle.imageapp.model.Category
import khanhle.imageapp.view.activity.ImageOfCategoryActivity
import khanhle.imageapp.viewmodel.CategoryViewModel


class CategoryFragment : Fragment() {

    private lateinit var v: View
    private lateinit var categorys: List<Category>
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var binding: FragmentCategoryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_category, container, false)

        binding = FragmentCategoryBinding.bind(v)

        observerData()


        return v
    }


    private fun setupRecyclerView() {
        binding.recyclerCategory.setHasFixedSize(true)
        var layoutManager = GridLayoutManager(context, 2)
        binding.recyclerCategory.layoutManager = layoutManager
    }

    private fun observerData() {
        val model = ViewModelProvider(requireActivity()).get(CategoryViewModel::class.java)
        model.getCategorys().observe(requireActivity(), object : Observer<List<Category>> {
            override fun onChanged(categoryList: List<Category>?) {
                if (categoryList != null) {
                    setupRecyclerView()
                    categorys = categoryList
                    categoryAdapter = CategoryAdapter(categorys as ArrayList<Category>, context!!)
                    binding.recyclerCategory.adapter = categoryAdapter
                    categoryAdapter.setOnItemClickListener(object : CategoryAdapter.ClickListener{
                        override fun onItemClick(position: Int, v: View) {
                            var item = categorys[position]
                            var intent = Intent(context, ImageOfCategoryActivity::class.java)
                            intent.putExtra("category_name", item.name)
                            startActivity(intent)
                        }
                    })
                }
            }
        })
    }

}