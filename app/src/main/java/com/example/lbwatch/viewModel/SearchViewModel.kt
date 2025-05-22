package com.example.lbwatch.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.lbwatch.api.ClientAPI
import com.example.lbwatch.model.Item
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val _items = MutableLiveData<List<Item>?>()
    val items: MutableLiveData<List<Item>?> = _items

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val clientApi: ClientAPI = Retrofit.Builder()
        .baseUrl("https://www.omdbapi.com/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(ClientAPI::class.java)

    // Запрос на получение фильмов по запросу
    fun fetchMovies(query: String) {
        _loading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = clientApi.fetchResponse("c54d9bf7", query)
                if (response.items.isNullOrEmpty()) {
                    _error.postValue("Нет фильмов по запросу")
                } else {
                    _items.postValue(response.items)
                }
            } catch (e: Exception) {
                _error.postValue("Ошибка при загрузке данных: ${e.message}")
            } finally {
                _loading.postValue(false)
            }
        }
    }
}
