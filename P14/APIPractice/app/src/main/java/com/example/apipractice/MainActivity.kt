package com.example.apipractice

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var apiService: BookApiService
    private lateinit var adapter: BookAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupRecyclerView()
        setupApiService()
        setupAddButton()
        setupSearchButton()
        setupUpdateDeleteButtons()
        getBooks()
    }

    private fun setupSearchButton() {
        findViewById<Button>(R.id.searchButton).setOnClickListener {
            showSearchDialog()
        }
    }

    private fun setupRecyclerView() {
        adapter = BookAdapter()
        findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        fun showUpdateBookDialog(book: Book) {
            // Tampilkan dialog pilih buku untuk diupdate
            apiService.getBooks().enqueue(object : Callback<List<Book>> {
                override fun onResponse(call: Call<List<Book>>, response: Response<List<Book>>) {
                    if (response.isSuccessful) {
                        val books = response.body() ?: return
                        val bookTitles = books.map { it.title }.toTypedArray()

                        AlertDialog.Builder(this@MainActivity)
                            .setTitle("Select Book to Update")
                            .setSingleChoiceItems(bookTitles, -1) { dialog, which ->
                                val selectedBook = books[which]
                                showEditBookDialog(selectedBook)
                                dialog.dismiss()
                            }
                            .setNegativeButton("Cancel", null)
                            .show()
                    }
                }

                override fun onFailure(call: Call<List<Book>>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "Failed to load books", Toast.LENGTH_SHORT).show()
                }
            })
        }

        // Tambahkan kemampuan long click untuk update dan delete
        adapter.setOnItemLongClickListener { book ->
            val options = arrayOf("Update", "Delete")
            AlertDialog.Builder(this)
                .setTitle("Choose Action")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> showUpdateBookDialog(book)
                        1 -> {
                            AlertDialog.Builder(this)
                                .setTitle("Confirm Delete")
                                .setMessage("Are you sure you want to delete this book?")
                                .setPositiveButton("Delete") { _, _ ->
                                    deleteBook(book.id)
                                }
                                .setNegativeButton("Cancel", null)
                                .show()
                        }
                    }
                }
                .show()
        }
    }

    private fun setupApiService() {
        apiService = ApiClient.getRetrofitInstance().create(BookApiService::class.java)
    }

    private fun setupAddButton() {
        findViewById<FloatingActionButton>(R.id.addButton).setOnClickListener {
            showAddBookDialog()
        }
    }

    private fun setupUpdateDeleteButtons() {
        findViewById<Button>(R.id.updateButton).setOnClickListener {
            showUpdateBookDialog()
        }

        findViewById<Button>(R.id.deleteButton).setOnClickListener {
            showDeleteBookDialog()
        }
    }

    private fun getBooks() {
        apiService.getBooks().enqueue(object : Callback<List<Book>> {
            override fun onResponse(call: Call<List<Book>>, response: Response<List<Book>>) {
                if (response.isSuccessful) {
                    response.body()?.let { adapter.updateBooks(it) }
                }
            }

            override fun onFailure(call: Call<List<Book>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error loading books", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showAddBookDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_add_book, null)
        AlertDialog.Builder(this)
            .setTitle("Add New Book")
            .setView(view)
            .setPositiveButton("Add") { _, _ ->
                val title = view.findViewById<EditText>(R.id.titleInput).text.toString()
                val author = view.findViewById<EditText>(R.id.authorInput).text.toString()
                addBook(title, author)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun addBook(title: String, author: String) {
        val book = Book(title, author)
        apiService.createBook(book).enqueue(object : Callback<Book> {
            override fun onResponse(call: Call<Book>, response: Response<Book>) {
                if (response.isSuccessful) {
                    getBooks() // Refresh list after successful add
                } else {
                    Toast.makeText(this@MainActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Book>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    // Method untuk update buku
    private fun updateBook(book: Book) {
        apiService.updateBook(book.id, book).enqueue(object : Callback<Book> {
            override fun onResponse(call: Call<Book>, response: Response<Book>) {
                if (response.isSuccessful) {
                    getBooks()
                } else {
                    Toast.makeText(this@MainActivity, "Error updating book", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Book>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Method untuk delete buku
    private fun deleteBook(bookId: Long) {
        apiService.deleteBook(bookId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    getBooks()
                } else {
                    Toast.makeText(this@MainActivity, "Error deleting book", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Method untuk search buku
    private fun searchBooks(keyword: String) {
        apiService.searchBooks(keyword).enqueue(object : Callback<List<Book>> {
            override fun onResponse(call: Call<List<Book>>, response: Response<List<Book>>) {
                if (response.isSuccessful) {
                    response.body()?.let { adapter.updateBooks(it) }
                } else {
                    Toast.makeText(this@MainActivity, "Error searching books", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Book>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Tambahkan method untuk menampilkan dialog search
    private fun showSearchDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_search_book, null)
        AlertDialog.Builder(this)
            .setTitle("Search Books")
            .setView(view)
            .setPositiveButton("Search") { _, _ ->
                val keyword = view.findViewById<EditText>(R.id.searchInput).text.toString()
                searchBooks(keyword)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showUpdateBookDialog() {
        // Tampilkan dialog pilih buku untuk diupdate
        apiService.getBooks().enqueue(object : Callback<List<Book>> {
            override fun onResponse(call: Call<List<Book>>, response: Response<List<Book>>) {
                if (response.isSuccessful) {
                    val books = response.body() ?: return
                    val bookTitles = books.map { it.title }.toTypedArray()

                    AlertDialog.Builder(this@MainActivity)
                        .setTitle("Select Book to Update")
                        .setSingleChoiceItems(bookTitles, -1) { dialog, which ->
                            val selectedBook = books[which]
                            showEditBookDialog(selectedBook)
                            dialog.dismiss()
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                }
            }

            override fun onFailure(call: Call<List<Book>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Failed to load books", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showEditBookDialog(book: Book) {
        val view = layoutInflater.inflate(R.layout.dialog_add_book, null)
        view.findViewById<EditText>(R.id.titleInput).setText(book.title)
        view.findViewById<EditText>(R.id.authorInput).setText(book.author)

        AlertDialog.Builder(this)
            .setTitle("Edit Book")
            .setView(view)
            .setPositiveButton("Update") { _, _ ->
                val updatedTitle = view.findViewById<EditText>(R.id.titleInput).text.toString()
                val updatedAuthor = view.findViewById<EditText>(R.id.authorInput).text.toString()

                val updatedBook = Book().apply {
                    id = book.id
                    title = updatedTitle
                    author = updatedAuthor
                }
                updateBook(updatedBook)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteBookDialog() {
        apiService.getBooks().enqueue(object : Callback<List<Book>> {
            override fun onResponse(call: Call<List<Book>>, response: Response<List<Book>>) {
                if (response.isSuccessful) {
                    val books = response.body() ?: return
                    val bookTitles = books.map { it.title }.toTypedArray()

                    AlertDialog.Builder(this@MainActivity)
                        .setTitle("Select Book to Delete")
                        .setSingleChoiceItems(bookTitles, -1) { dialog, which ->
                            val selectedBook = books[which]

                            AlertDialog.Builder(this@MainActivity)
                                .setTitle("Confirm Delete")
                                .setMessage("Are you sure you want to delete '${selectedBook.title}'?")
                                .setPositiveButton("Delete") { _, _ ->
                                    deleteBook(selectedBook.id)
                                }
                                .setNegativeButton("Cancel", null)
                                .show()

                            dialog.dismiss()
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                }
            }

            override fun onFailure(call: Call<List<Book>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Failed to load books", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
