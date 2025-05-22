package com.example.lbwatch.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lbwatch.R
import com.example.lbwatch.model.Movie
import com.squareup.picasso.Picasso

class MainAdapter(
    private var movieList: List<Movie>,
    private val onMovieSelected: (Movie, Boolean) -> Unit // Callback для выбранных фильмов
) : RecyclerView.Adapter<MainAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie_main, parent, false)
        return MainHolder(view)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val movie = movieList[position]
        holder.titleTextView.text = movie.title
        holder.releaseDateTextView.text = movie.year

        // У фильма нет изображения
        if (movie.posterUrl?.isEmpty() == true) {
            holder.imageView.setImageDrawable(holder.itemView.context.getDrawable(R.drawable.no_image))
        } else {
            // У фильма есть изображение
            Picasso.get().load(movie.posterUrl).into(holder.imageView)
        }

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            onMovieSelected(movie, isChecked)
        }
    }

    override fun getItemCount(): Int = movieList.size

    // Обновление данных в адаптере
    fun updateMovies(newMovieList: List<Movie>) {
        movieList = newMovieList
        notifyDataSetChanged()
    }

    inner class MainHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.title_textview)
        val releaseDateTextView: TextView = view.findViewById(R.id.release_date_textview)
        val imageView: ImageView = view.findViewById(R.id.movie_imageview)
        val checkBox: CheckBox = view.findViewById(R.id.checkbox)
    }
}