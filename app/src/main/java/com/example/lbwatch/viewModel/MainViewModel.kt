package com.example.lbwatch.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.lbwatch.model.Movie
import com.example.lbwatch.model.MovieDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val movieDao = MovieDB.getDb(application).getDao()

    // LiveData для списка фильмов
    val movies: LiveData<List<Movie>> = movieDao.getAll().asLiveData()

    private val selectedMovies = mutableSetOf<Movie>()

    // Функция для удаления выбранных фильмов
    fun deleteMovies(moviesToDelete: List<Movie>) {
        viewModelScope.launch(Dispatchers.IO) {
            for (movie in moviesToDelete) {
                movieDao.delete(movie)
            }
        }
    }

    // Функция для добавления нового фильма
    fun addMovie(movie: Movie) {
        viewModelScope.launch(Dispatchers.IO) {
            movieDao.insert(movie)
        }
    }

    // Добавление или удаление фильма из выбранных
    fun toggleMovieSelection(movie: Movie, isSelected: Boolean) {
        if (isSelected) {
            selectedMovies.add(movie)
        } else {
            selectedMovies.remove(movie)
        }
    }

    // Получение списка выбранных фильмов
    fun getSelectedMovies(): List<Movie> {
        return selectedMovies.toList()
    }
}