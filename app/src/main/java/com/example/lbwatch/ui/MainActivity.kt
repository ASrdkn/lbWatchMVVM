package com.example.lbwatch.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lbwatch.adapter.MainAdapter
import com.example.lbwatch.R
import com.example.lbwatch.viewModel.MainViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MainAdapter
    private lateinit var imageEmpty: LinearLayout

    // Получаем экземпляр ViewModel
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageEmpty = findViewById(R.id.no_movies_layout)
        val deleteBtn = findViewById<ImageView>(R.id.img_delete)

        recyclerView = findViewById(R.id.movies_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val addBtn = findViewById<FloatingActionButton>(R.id.fab)
        addBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, AddActivity::class.java)
            startActivityForResult(intent, ADD_VIEW_ACTIVITY_REQUEST_CODE)
        }

        // Наблюдаем за списком фильмов из ViewModel
        mainViewModel.movies.observe(this, Observer { movies ->
            if (movies.isNotEmpty()) {
                imageEmpty.visibility = View.INVISIBLE
                recyclerView.visibility = View.VISIBLE
                adapter = MainAdapter(movies) { movie, isSelected ->
                    mainViewModel.toggleMovieSelection(movie, isSelected) // передаем состояние выбора в ViewModel
                }
                recyclerView.adapter = adapter
            } else {
                recyclerView.visibility = View.INVISIBLE
                imageEmpty.visibility = View.VISIBLE
            }
        })

        // Обработчик удаления выбранных фильмов
        deleteBtn.setOnClickListener {
            val selectedMovies = mainViewModel.getSelectedMovies()
            mainViewModel.deleteMovies(selectedMovies)
            showToast("Фильмы успешно удалены")
        }
    }

    companion object {
        const val ADD_VIEW_ACTIVITY_REQUEST_CODE = 1
    }

    private fun showToast(str: String) {
        Toast.makeText(this, str, Toast.LENGTH_LONG).show()
    }
}