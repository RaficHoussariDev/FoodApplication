package com.example.foodapp.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.*
import com.example.foodapp.R
import com.example.foodapp.data.Repository
import com.example.foodapp.data.database.entities.FavoritesEntity
import com.example.foodapp.data.database.entities.RecipesEntity
import com.example.foodapp.models.FoodRecipe
import com.example.foodapp.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
): AndroidViewModel(application) {

    /** ROOM DATABASE **/

    val readRecipes: LiveData<List<RecipesEntity>> = repository.local.readRecipes().asLiveData()
    val readFavoriteRecipes: LiveData<List<FavoritesEntity>> =
        repository.local.readFavoriteRecipes().asLiveData()

    private fun insertRecipes(recipesEntity: RecipesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertRecipes(recipesEntity)
        }

    fun insertFavoriteRecipe(favoritesEntity: FavoritesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertFavoriteRecipe(favoritesEntity)
        }

    fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteFavoriteRecipe(favoritesEntity)
        }

    private fun deleteAllFavoriteRecipe() =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteAllFavoriteRecipes()
        }

    /** RETROFIT **/

    var recipesResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()
    var searchedRecipesResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()

    fun getRecipes(queries: Map<String, String>) = viewModelScope.launch {
        getRecipesSafeCall(queries)
    }

    fun searchRecipes(searchQuery: Map<String, String>) = viewModelScope.launch {
        searchRecipesSafeCall(searchQuery)
    }

    private suspend fun getRecipesSafeCall(queries: Map<String, String>) {
        recipesResponse.value = NetworkResult.Loading()
        if(hasInternetConnection()) {
            try {
                val response = repository.remote.getRecipes(queries)
                recipesResponse.value = handleFoodRecipesResponse(response)

                val foodRecipe = recipesResponse.value!!.data
                if(foodRecipe != null) {
                    offlineCacheRecipes(foodRecipe)
                }
            }catch (e: Exception) {
                recipesResponse.value = NetworkResult.Error(getApplication<Application>()
                    .getString(R.string.recipes_not_found))
            }
        } else {
            recipesResponse.value = NetworkResult.Error(getApplication<Application>()
                .getString(R.string.no_internet_connection))
        }
    }

    private suspend fun searchRecipesSafeCall(searchQuery: Map<String, String>) {
        searchedRecipesResponse.value = NetworkResult.Loading()
        if(hasInternetConnection()) {
            try {
                val response = repository.remote.searchRecipes(searchQuery)
                searchedRecipesResponse.value = handleFoodRecipesResponse(response)
            }catch (e: Exception) {
                searchedRecipesResponse.value = NetworkResult.Error(getApplication<Application>()
                    .getString(R.string.recipes_not_found))
            }
        } else {
            searchedRecipesResponse.value = NetworkResult.Error(getApplication<Application>()
                .getString(R.string.no_internet_connection))
        }
    }

    private fun offlineCacheRecipes(foodRecipe: FoodRecipe) {
        val recipesEntity = RecipesEntity(foodRecipe)
        insertRecipes(recipesEntity)
    }

    private fun handleFoodRecipesResponse(response: Response<FoodRecipe>): NetworkResult<FoodRecipe> {
        when {
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error(getApplication<Application>().getString(R.string.timeout))
            }
            response.code() == 402 -> {
                return NetworkResult.Error(getApplication<Application>().getString(R.string.api_key_limited))
            }
            response.body()!!.results.isNullOrEmpty() -> {
                return NetworkResult.Error(getApplication<Application>().getString(R.string.recipes_not_found))
            }
            response.isSuccessful -> {
                val foodRecipes = response.body()
                return NetworkResult.Success(foodRecipes!!)
            }
            else -> {
                return NetworkResult.Error(response.message())
            }
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}