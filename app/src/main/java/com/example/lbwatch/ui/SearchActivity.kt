package com.example.lbwatch.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lbwatch.R
import com.example.lbwatch.adapter.SearchAdapter
import com.example.lbwatch.viewModel.SearchViewModel

class SearchActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SearchAdapter
    private lateinit var noMoviesTextView: TextView
    private lateinit var progressBar: ProgressBar
    private var query = ""

    private val searchViewModel: SearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        recyclerView = findViewById(R.id.search_results_recyclerview)
        progressBar = findViewById(R.id.progress_bar)
        noMoviesTextView = findViewById(R.id.no_movies_textview)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Получаем запрос для поиска
        val i = intent
        query = i.getStringExtra(SEARCH_QUERY) ?: ""

        // Инициализация адаптера
        adapter = SearchAdapter(this, itemListener)
        recyclerView.adapter = adapter

        // Наблюдаем за состоянием загрузки, ошибками и результатами
        searchViewModel.loading.observe(this, Observer { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
        })

        searchViewModel.error.observe(this, Observer { errorMessage ->
            if (errorMessage != null) {
                noMoviesTextView.visibility = View.VISIBLE
                noMoviesTextView.text = errorMessage
            }
        })

        searchViewModel.items.observe(this, Observer { items ->
            if (items?.isNotEmpty() == true) { // Проверка на null и пустоту
                noMoviesTextView.visibility = View.INVISIBLE
                recyclerView.visibility = View.VISIBLE
                adapter.updateData(items) // Обновляем данные в адаптере
            } else {
                recyclerView.visibility = View.INVISIBLE
                noMoviesTextView.visibility = View.VISIBLE
            }
        })

        // Выполняем запрос на получение фильмов
        searchViewModel.fetchMovies(query)
    }

    internal var itemListener: SearchAdapter.RecyclerItemListener = object : SearchAdapter.RecyclerItemListener {
        override fun onItemClick(view: View, position: Int) {
            val movie = adapter.getItemAtPosition(position)
            val replyIntent = Intent()
            replyIntent.putExtra(EXTRA_TITLE, movie.title)
            replyIntent.putExtra(EXTRA_RELEASE_DATE, movie.getReleaseYearFromDate().toString())
            replyIntent.putExtra(EXTRA_POSTER_PATH, movie.posterPath)
            setResult(RESULT_OK, replyIntent)
            finish()
        }
    }

    companion object {
        const val SEARCH_QUERY = "searchQuery"
        const val EXTRA_TITLE = "SearchActivity.TITLE_REPLY"
        const val EXTRA_RELEASE_DATE = "SearchActivity.RELEASE_DATE_REPLY"
        const val EXTRA_POSTER_PATH = "SearchActivity.POSTER_PATH_REPLY"
    }
}
