package com.example.lbwatch.adapter

import android.content.Context
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

class MainAdapter(var list: List<Movie>, var context: Context) : RecyclerView.Adapter<MainAdapter.MainHolder>() {

    // Множество для хранения выбранных фильмов
    val selectedMovies = mutableSetOf<Movie>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_movie_main, parent, false)
        return MainHolder(view)
    }

    // Метод для привязки данных к элементам ViewHolder
    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val movie = list[position]

        holder.titleTextView.text = movie.title
        holder.releaseDateTextView.text = movie.year

        // У фильма нет изображения
        if (movie.posterUrl?.isEmpty() == true) {
            holder.imageView.setImageDrawable(context.getDrawable(R.drawable.no_image))
        } else {
            // У фильма есть изображение
            Picasso.get().load(movie.posterUrl).into(holder.imageView)
        }
    }

    // Возвращаем количество элементов в списке
    override fun getItemCount(): Int = list.size

    inner class MainHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.title_textview)
        val releaseDateTextView: TextView = view.findViewById(R.id.release_date_textview)
        val imageView: ImageView = view.findViewById(R.id.movie_imageview)
        val checkBox: CheckBox = view.findViewById(R.id.checkbox)

        init {
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val movie = list[position]
                    // Добавляем или удаляем фильм из списка выбранных в зависимости от состояния чекбокса
                    if (isChecked) {
                        selectedMovies.add(movie)
                    } else {
                        selectedMovies.remove(movie)
                    }
                }
            }
        }
    }
}
