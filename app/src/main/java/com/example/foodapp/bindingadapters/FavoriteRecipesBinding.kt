package com.example.foodapp.bindingadapters

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foodapp.adapters.FavoriteRecipesAdapter
import com.example.foodapp.data.database.entities.FavoritesEntity

class FavoriteRecipesBinding {

    companion object {

        @BindingAdapter("viewVisibility", "setData", requireAll = false)
        @JvmStatic
        fun setDataAndViewVisibility(
            view: View,
            favoritesEntity: List<FavoritesEntity>?,
            mAdapter: FavoriteRecipesAdapter?
        ) {
            if(favoritesEntity.isNullOrEmpty()) {
                when(view) {
                    is RecyclerView -> view.visibility = View.INVISIBLE
                    else -> view.visibility = View.VISIBLE
                }
            } else {
                when(view) {
                    is RecyclerView -> {
                        view.visibility = View.VISIBLE
                        mAdapter?.setData(favoritesEntity)
                    }
                    else -> view.visibility = View.INVISIBLE
                }
            }
        }
    }
}