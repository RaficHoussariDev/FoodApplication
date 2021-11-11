package com.example.foodapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.navArgs
import androidx.viewpager.widget.ViewPager
import com.example.foodapp.R
import com.example.foodapp.adapters.PagerAdapter
import com.example.foodapp.ui.fragments.ingredients.IngredientsFragment
import com.example.foodapp.ui.fragments.instructions.InstructionsFragment
import com.example.foodapp.ui.fragments.overview.OverviewFragment
import com.google.android.material.tabs.TabLayout

class DetailsActivity : AppCompatActivity() {

    private val args by navArgs<DetailsActivityArgs>()

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}