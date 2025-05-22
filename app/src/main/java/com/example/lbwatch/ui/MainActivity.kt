package com.example.lbwatch.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lbwatch.adapter.MainAdapter
import com.example.lbwatch.R
import com.example.lbwatch.model.MovieDB
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MainAdapter
    private lateinit var imageEmpty: LinearLayout
    private lateinit var dataBase: MovieDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageEmpty = findViewById(R.id.no_movies_layout)
        val deleteBtn = findViewById<ImageView>(R.id.img_delete)

        dataBase = MovieDB.getDb(this)
        recyclerView = findViewById(R.id.movies_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val addBtn = findViewById<FloatingActionButton>(R.id.fab)
        addBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, AddActivity::class.java)
            startActivityForResult(intent, ADD_VIEW_ACTIVITY_REQUEST_CODE)
        }

        loadView()

        deleteBtn.setOnClickListener {
            val movies = adapter.selectedMovies.toList()
            CoroutineScope(Dispatchers.IO).launch {
                for (movie in movies) {
                    dataBase.getDao().delete(movie)
                }
                launch(Dispatchers.Main) {
                    loadView()

                    if (movies.size == 1) {
                        showToast("Фильм успешно удален")
                    } else if (movies.size > 1) {
                        showToast("Фильмы успешно удалены")
                    }
                }
            }
        }
    }

    private fun loadView() {
        dataBase.getDao().getAll().asLiveData().observe(this@MainActivity) { movies ->
            if (movies.isNotEmpty()) {
                imageEmpty.visibility = View.INVISIBLE
                recyclerView.visibility = View.VISIBLE
                adapter = MainAdapter(movies, this@MainActivity)
                recyclerView.adapter = adapter
            } else {
                recyclerView.visibility = View.INVISIBLE
                imageEmpty.visibility = View.VISIBLE
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_VIEW_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                showToast("Фильм успешно добавлен")
                loadView()
            } else {
                showToast("Нет добавленных фильмов")
            }
        }
    }

    companion object {
        const val ADD_VIEW_ACTIVITY_REQUEST_CODE = 1
    }

    private fun showToast(str: String) {
        Toast.makeText(this, str, Toast.LENGTH_LONG).show()
    }
}
