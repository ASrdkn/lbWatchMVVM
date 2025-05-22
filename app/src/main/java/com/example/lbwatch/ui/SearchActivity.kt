package com.example.lbwatch.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lbwatch.R
import com.example.lbwatch.adapter.SearchAdapter
import com.example.lbwatch.api.ClientAPI
import com.example.lbwatch.model.Item
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class SearchActivity : AppCompatActivity() {
    private lateinit var list: List<Item>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SearchAdapter
    private lateinit var noMoviesTextView: TextView
    private lateinit var progressBar: ProgressBar
    private var query = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        recyclerView = findViewById(R.id.search_results_recyclerview)
        progressBar = findViewById(R.id.progress_bar)
        noMoviesTextView = findViewById(R.id.no_movies_textview)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val i = intent
        query = i.getStringExtra(SEARCH_QUERY) ?: ""

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://www.omdbapi.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        val clientApi: ClientAPI = retrofit.create(ClientAPI::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            val response = clientApi.fetchResponse("c54d9bf7", query)
            runOnUiThread {
                list = response.items ?: emptyList()
                if (list.isEmpty()) {
                    noMoviesTextView.visibility = View.VISIBLE
                } else {
                    noMoviesTextView.visibility = View.INVISIBLE
                }
                adapter = SearchAdapter(list, itemListener, this@SearchActivity)
                recyclerView.adapter = adapter
                progressBar.visibility = View.INVISIBLE
            }
        }
    }

    internal var itemListener: RecyclerItemListener = object : RecyclerItemListener {
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

    interface RecyclerItemListener {
        fun onItemClick(v: View, position: Int)
    }
}
