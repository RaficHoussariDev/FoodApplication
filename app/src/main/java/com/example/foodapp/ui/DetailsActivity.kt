package com.example.foodapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.navArgs
import androidx.viewpager.widget.ViewPager
import com.example.foodapp.R
import com.example.foodapp.adapters.PagerAdapter
import com.example.foodapp.data.database.entities.FavoritesEntity
import com.example.foodapp.ui.fragments.ingredients.IngredientsFragment
import com.example.foodapp.ui.fragments.instructions.InstructionsFragment
import com.example.foodapp.ui.fragments.overview.OverviewFragment
import com.example.foodapp.viewmodels.MainViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception

@AndroidEntryPoint
class DetailsActivity : AppCompatActivity() {

    private val args by navArgs<DetailsActivityArgs>()
    private val mainViewModel: MainViewModel by viewModels()

    private var recipeSaved = false
    private var recipeSavedId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        setSupportActionBar(findViewById(R.id.toolbar))
        findViewById<Toolbar>(R.id.toolbar).setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val fragments = ArrayList<Fragment>()
        fragments.add(OverviewFragment())
        fragments.add(IngredientsFragment())
        fragments.add(InstructionsFragment())

        val titles = ArrayList<String>()
        titles.add(application.getString(R.string.overview))
        titles.add(application.getString(R.string.ingredients))
        titles.add(application.getString(R.string.instructions))

        val resultBundle = Bundle()
        resultBundle.putParcelable("resultBundle", args.result)

        val adapter = PagerAdapter(
            resultBundle,
            fragments,
            titles,
            supportFragmentManager
        )

        findViewById<ViewPager>(R.id.viewPager).adapter = adapter
        findViewById<TabLayout>(R.id.tabLayout).setupWithViewPager(findViewById(R.id.viewPager))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.details_menu, menu)
        val menuItem = menu?.findItem(R.id.save_to_favorites_menu)
        checkSavedRecipes(menuItem!!)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home) {
            finish()
        } else if(item.itemId == R.id.save_to_favorites_menu && !recipeSaved) {
            saveToFavorites(item)
        } else if(item.itemId == R.id.save_to_favorites_menu && recipeSaved) {
            removeFromFavorites(item)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveToFavorites(item: MenuItem) {
        val favoritesEntity = FavoritesEntity(0, args.result)
        mainViewModel.insertFavoriteRecipe(favoritesEntity)

        changeMenuItemColor(item, R.color.yellow)
        showSnackBar(application.getString(R.string.recipe_saved))

        recipeSaved = true
    }

    private fun removeFromFavorites(item: MenuItem) {
        val favoritesEntity = FavoritesEntity(recipeSavedId, args.result)
        mainViewModel.deleteFavoriteRecipe(favoritesEntity)

        changeMenuItemColor(item, R.color.white)
        showSnackBar(application.getString(R.string.recipe_removed))

        recipeSaved = false
    }

    private fun checkSavedRecipes(menuItem: MenuItem) {
        mainViewModel.readFavoriteRecipes.observe(this, { favoritesEntity ->
            try {
                for(savedRecipe in favoritesEntity) {
                    if(savedRecipe.result.id == args.result.id) {
                        changeMenuItemColor(menuItem, R.color.yellow)
                        recipeSavedId = savedRecipe.id
                        recipeSaved = true
                    } else {
                        changeMenuItemColor(menuItem, R.color.white)
                    }
                }
            } catch(e: Exception) {
                Log.d("DetailsActivity", e.message.toString())
            }
        })
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(
            findViewById(R.id.detailsLayout),
            message,
            Snackbar.LENGTH_SHORT
        ).setAction(application.getString(R.string.okay)){}
         .show()
    }

    private fun changeMenuItemColor(item: MenuItem, color: Int) {
        item.icon.setTint(ContextCompat.getColor(this, color))
    }
}