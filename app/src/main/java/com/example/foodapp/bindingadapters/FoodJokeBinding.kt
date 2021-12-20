package com.example.foodapp.bindingadapters

import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.foodapp.data.database.entities.FoodJokeEntity
import com.example.foodapp.models.FoodJoke
import com.example.foodapp.util.NetworkResult
import com.google.android.material.card.MaterialCardView

class FoodJokeBinding {

    companion object {

        @BindingAdapter("readApiResponse3", "readDatabase3", requireAll = false)
        @JvmStatic
        fun setCardAndProgressVisibility(
            view: View,
            apiResponse: NetworkResult<FoodJoke>?,
            database: List<FoodJokeEntity>?
        ) {
            when(apiResponse) {
                is NetworkResult.Loading -> {
                    when(view) {
                        is ProgressBar -> view.visibility = View.VISIBLE
                        is MaterialCardView -> view.visibility = View.INVISIBLE
                    }
                }
                is NetworkResult.Error -> {
                    when(view) {
                        is ProgressBar -> view.visibility = View.INVISIBLE
                        is MaterialCardView -> {
                            view.visibility = View.VISIBLE
                            if(database != null) {
                                if(database.isEmpty()) {
                                    view.visibility = View.INVISIBLE
                                }
                            }
                        }
                    }
                }
                is NetworkResult.Success -> {
                    when(view) {
                        is ProgressBar -> view.visibility = View.INVISIBLE
                        is MaterialCardView -> view.visibility = View.VISIBLE
                    }
                }
            }
        }

        @BindingAdapter("readApiResponse4", "readDatabase4", requireAll = true)
        @JvmStatic
        fun setErrorViewVisibility(
            view: View,
            apiResponse: NetworkResult<FoodJoke>?,
            database: List<FoodJokeEntity>?
        ) {
            if(database.isNullOrEmpty()) {
                view.visibility = View.VISIBLE
                if(view is TextView && apiResponse != null && !apiResponse.message.isNullOrBlank()) {
                    view.text = apiResponse.message.toString()
                }
            } else {
                view.visibility = View.INVISIBLE
            }
            if(apiResponse is NetworkResult.Success || apiResponse is NetworkResult.Loading) {
                view.visibility = View.INVISIBLE
            }
        }
    }
}