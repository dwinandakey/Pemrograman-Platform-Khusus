package com.example.apipractice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BookAdapter : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {
    private var books = mutableListOf<Book>()
    private var onItemLongClickListener: ((Book) -> Unit)? = null

    fun setOnItemLongClickListener(listener: (Book) -> Unit) {
        onItemLongClickListener = listener
    }

    class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(android.R.id.text1)
        val authorText: TextView = view.findViewById(android.R.id.text2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        holder.titleText.text = book.title
        holder.authorText.text = book.author

        // Tambahkan long click listener
        holder.itemView.setOnLongClickListener {
            onItemLongClickListener?.invoke(book)
            true
        }
    }

    override fun getItemCount() = books.size

    fun updateBooks(newBooks: List<Book>) {
        books.clear()
        books.addAll(newBooks)
        notifyDataSetChanged()
    }
}