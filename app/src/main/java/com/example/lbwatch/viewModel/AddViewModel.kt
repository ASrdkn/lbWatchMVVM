package com.example.lbwatch.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.lbwatch.model.Movie
import com.example.lbwatch.model.MovieDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddViewModel(application: Application) : AndroidViewModel(application) {

    private val movieDao = MovieDB.getDb(application).getDao()

    // LiveData для отслеживания состояния UI
    val title = MutableLiveData<String>()
    val releaseDate = MutableLiveData<String>()
    val moviePosterPath = MutableLiveData<String>()

    // LiveData для списка фильмов
    val allMovies = MutableLiveData<List<Movie>>()

    // Функция для добавления фильма в базу данных
    fun addMovie() {
        val title = title.value
        val releaseDate = releaseDate.value
        val posterPath = moviePosterPath.value

        if (!title.isNullOrEmpty() && !releaseDate.isNullOrEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                val movie = Movie(
                    null,
                    title,
                    releaseDate,
                    posterPath.orEmpty()
                )
                // Вставляем фильм в базу данных
                movieDao.insert(movie)
                // Загружаем все фильмы и обновляем LiveData
                loadMovies()
            }
        }
    }

    // Функция для загрузки всех фильмов
    private fun loadMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            // Собираем данные из Flow в список
            movieDao.getAll().collect { movies ->
                // Обновляем LiveData с новым списком фильмов
                allMovies.postValue(movies)
            }
        }
    }
}