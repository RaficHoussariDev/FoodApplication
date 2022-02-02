package com.example.foodapp.viewmodels

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.foodapp.R
import com.example.foodapp.data.DataStoreRepository
import com.example.foodapp.data.MealAndDietType
import com.example.foodapp.util.Constants.Companion.API_KEY
import com.example.foodapp.util.Constants.Companion.DEFAULT_DIET_TYPE
import com.example.foodapp.util.Constants.Companion.DEFAULT_MEAL_TYPE
import com.example.foodapp.util.Constants.Companion.DEFAULT_RECIPES_NUMBER
import com.example.foodapp.util.Constants.Companion.QUERY_ADD_RECIPE_INFORMATION
import com.example.foodapp.util.Constants.Companion.QUERY_API_KEY
import com.example.foodapp.util.Constants.Companion.QUERY_DIET
import com.example.foodapp.util.Constants.Companion.QUERY_FILL_INGREDIENTS
import com.example.foodapp.util.Constants.Companion.QUERY_NUMBER
import com.example.foodapp.util.Constants.Companion.QUERY_SEARCH
import com.example.foodapp.util.Constants.Companion.QUERY_TYPE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipesViewModel @Inject constructor(
    application: Application,
    private val dataStoreRepository: DataStoreRepository)
    : AndroidViewModel(application) {

    private lateinit var mealAndDiet: MealAndDietType


    var networkStatus = false
    var backOnline = false

    val readMealAndDietType = dataStoreRepository.readMealAndDietType
    val readBackOnline = dataStoreRepository.readBackOnline.asLiveData()

    fun saveMealAndDietType() =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveMealAndDietType(
                mealAndDiet.selectedMealType,
                mealAndDiet.selectedMealTypeId,
                mealAndDiet.selectedDietType,
                mealAndDiet.selectedDietTypeId
            )
        }

    fun saveMealAndDietTypeTemp(mealType: String, mealTypeId: Int, dietType: String, dietTypeId: Int) {
        mealAndDiet = MealAndDietType(
            mealType,
            mealTypeId,
            dietType,
            dietTypeId
        )
    }

    private fun saveBackOnline(backOnline: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveBackOnline(backOnline)
        }

    fun applyQueries(): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()

        queries[QUERY_NUMBER] = DEFAULT_RECIPES_NUMBER
        queries[QUERY_API_KEY] = API_KEY
        queries[QUERY_TYPE] = mealAndDiet.selectedMealType
        queries[QUERY_DIET] = mealAndDiet.selectedDietType
        queries[QUERY_ADD_RECIPE_INFORMATION] = "true"
        queries[QUERY_FILL_INGREDIENTS] = "true"

        return queries
    }

    fun applySearchQuery(searchQuery: String): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()

        queries[QUERY_SEARCH] = searchQuery
        queries[QUERY_NUMBER] = DEFAULT_RECIPES_NUMBER
        queries[QUERY_API_KEY] = API_KEY
        queries[QUERY_ADD_RECIPE_INFORMATION] = "true"
        queries[QUERY_FILL_INGREDIENTS] = "true"

        return queries
    }

    fun showNetworkStatus() {
        if(!networkStatus) {
            Toast.makeText(
                getApplication(),
                getApplication<Application>().getString(R.string.no_internet_connection_point),
                Toast.LENGTH_SHORT
            ).show()
            saveBackOnline(true)
        } else if(networkStatus && backOnline) {
            Toast.makeText(
                getApplication(),
                getApplication<Application>().getString(R.string.back_online),
                Toast.LENGTH_SHORT
            ).show()
            saveBackOnline(false)
        }
    }
}