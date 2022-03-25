package khanhle.imageapp.view.activity

import CategoryFragment
import FavoriteFragment
import MenuFragment
import RandomFragment
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationBarView
import khanhle.imageapp.R
import khanhle.imageapp.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showFirstPage()
        handleWithEvents()

    }

    private fun showFirstPage() {
        showPage(CategoryFragment())
    }

    private fun handleWithEvents() {
        binding.bottomNavigationView.itemIconTintList = null
        val menu = binding.bottomNavigationView.menu
        menu.findItem(R.id.nav_category).setIcon(R.drawable.select_category)
        menu.findItem(R.id.nav_random).setIcon(R.drawable.unselect_random_photo)
        menu.findItem(R.id.nav_favorite).setIcon(R.drawable.unselect_fav)
        menu.findItem(R.id.nav_menu).setIcon(R.drawable.unselect_menu)

        binding.bottomNavigationView.setOnItemSelectedListener(object : NavigationBarView.OnItemSelectedListener{
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                val menu = binding.bottomNavigationView.menu
                when (item.itemId) {
                    R.id.nav_category -> {
                        showPage(CategoryFragment())
                        binding.bottomNavigationView.itemIconTintList = null
                        menu.findItem(R.id.nav_category).setIcon(R.drawable.select_category)
                        menu.findItem(R.id.nav_random).setIcon(R.drawable.unselect_random_photo)
                        menu.findItem(R.id.nav_favorite).setIcon(R.drawable.unselect_fav)
                        menu.findItem(R.id.nav_menu).setIcon(R.drawable.unselect_menu)
                    }
                    R.id.nav_random -> {
                        showPage(RandomFragment())
                        binding.bottomNavigationView.itemIconTintList = null
                        menu.findItem(R.id.nav_random).setIcon(R.drawable.select_random)
                        menu.findItem(R.id.nav_category).setIcon(R.drawable.unselect_category)
                        menu.findItem(R.id.nav_favorite).setIcon(R.drawable.unselect_fav)
                        menu.findItem(R.id.nav_menu).setIcon(R.drawable.unselect_menu)
                    }
                    R.id.nav_favorite -> {
                        showPage(FavoriteFragment())
                        binding.bottomNavigationView.itemIconTintList = null
                        menu.findItem(R.id.nav_favorite).setIcon(R.drawable.select_fav)
                        menu.findItem(R.id.nav_category).setIcon(R.drawable.unselect_category)
                        menu.findItem(R.id.nav_random).setIcon(R.drawable.unselect_random_photo)
                        menu.findItem(R.id.nav_menu).setIcon(R.drawable.unselect_menu)
                    }
                    R.id.nav_menu -> {
                        showPage(MenuFragment())
                        binding.bottomNavigationView.itemIconTintList = null
                        menu.findItem(R.id.nav_menu).setIcon(R.drawable.select_menu)
                        menu.findItem(R.id.nav_category).setIcon(R.drawable.unselect_category)
                        menu.findItem(R.id.nav_random).setIcon(R.drawable.unselect_random_photo)
                        menu.findItem(R.id.nav_favorite).setIcon(R.drawable.unselect_fav)
                    }
                }
                return true
            }
        })
    }

    private fun showPage(page: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, page)
            .addToBackStack(null)
            .commit()
    }
}























