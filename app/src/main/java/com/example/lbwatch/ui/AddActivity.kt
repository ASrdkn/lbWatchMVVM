package com.example.lbwatch.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.lbwatch.R
import com.example.lbwatch.model.Movie
import com.example.lbwatch.model.MovieDB
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddActivity : AppCompatActivity() {
    lateinit var titleEditText: EditText
    lateinit var releaseDateEditText: EditText
    lateinit var movieImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val dataBase = MovieDB.getDb(this)

        titleEditText = findViewById(R.id.movie_title)
        val searchBtn = findViewById<ImageButton>(R.id.search_btn)
        val addBtn = findViewById<Button>(R.id.add_movie)
        releaseDateEditText = findViewById(R.id.movie_release_date)
        movieImageView = findViewById(R.id.movie_imageview)

        searchBtn.setOnClickListener {
            if (titleEditText.text.isEmpty()) {
                Toast.makeText(
                    this,
                    "Название фильма не может быть пустым",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val title = titleEditText.text.toString()
                val intent = Intent(this@AddActivity, SearchActivity::class.java)
                intent.putExtra(SearchActivity.SEARCH_QUERY, title)
                startActivityForResult(intent, SEARCH_ACTIVITY_REQUEST_CODE)
            }
        }

        addBtn.setOnClickListener {
            if (titleEditText.text.isEmpty() || releaseDateEditText.text.isEmpty()) {
                Toast.makeText(
                    this,
                    "Поля не могут быть пустыми",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                if (movieImageView.tag == null) movieImageView.tag = ""

                CoroutineScope(Dispatchers.IO).launch {
                    val movie = Movie(
                        null,
                        titleEditText.text.toString(),
                        releaseDateEditText.text.toString(),
                        movieImageView.tag.toString()
                    )
                    dataBase.getDao().insert(movie)
                }
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        this@AddActivity.runOnUiThread {
            titleEditText.setText(data?.getStringExtra(SearchActivity.EXTRA_TITLE))
            releaseDateEditText.setText(data?.getStringExtra(SearchActivity.EXTRA_RELEASE_DATE))
            movieImageView.tag = data?.getStringExtra(SearchActivity.EXTRA_POSTER_PATH) ?: ""
            if (movieImageView.tag !== "") {
                Picasso.get().load(data?.getStringExtra(SearchActivity.EXTRA_POSTER_PATH)).into(movieImageView)
            }
            Log.d("AddActivity", movieImageView.tag.toString())
        }
    }

    companion object {
        const val SEARCH_ACTIVITY_REQUEST_CODE = 2
    }
}
