package com.daon.book_part3_04

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.daon.book_part3_04.adapter.BookAdapter
import com.daon.book_part3_04.adapter.HistoryAdapter
import com.daon.book_part3_04.api.BookService
import com.daon.book_part3_04.databinding.ActivityMainBinding
import com.daon.book_part3_04.model.BestSellerDto
import com.daon.book_part3_04.model.History
import com.daon.book_part3_04.model.SearchBookDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var adapter: BookAdapter
    private lateinit var historyAdapter : HistoryAdapter
    private lateinit var bookService: BookService

    private lateinit var db : AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // activity 안에는 이미 layoutInflater 가 존재하므로 LayoutInfater.from()을 통해
        // 새로 만들어줄 필요가 없다.
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        initBookRecyclerView()
        initHistoryRecyclerView()

        db = getAppDatabase(this)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://book.interpark.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        bookService = retrofit.create(BookService::class.java)

        bookService.getBestSellerBooks(
            getString(R.string.interparkAPIKey)
        ).enqueue(object : Callback<BestSellerDto> {
            override fun onResponse(call: Call<BestSellerDto>, response: Response<BestSellerDto>) {
                // TODO 성공처리

                if (response.isSuccessful.not()) {
                    Log.e(TAG, "Not!! SUCCESS")
                    return
                }

                response.body()?.let {
                    Log.d(TAG, it.toString())

                    // 추가적인 notify 필요 없음
                    adapter.submitList(it.books)
                }
            }

            override fun onFailure(call: Call<BestSellerDto>, t: Throwable) {
                // TODO 실파처리
                Log.d(TAG, t.toString())
                Toast.makeText(this@MainActivity , "데이터를 받아오는데 실패하였습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun search (keyword : String) {
        bookService.getBooksByName(
            getString(R.string.interparkAPIKey),
            keyword
        ).enqueue(object : Callback<SearchBookDto> {
            override fun onResponse(call: Call<SearchBookDto>, response: Response<SearchBookDto>) {
                // TODO 성공처리

                hideHistoryView()

                if (response.isSuccessful.not()) {
                    Log.e(TAG, "Not!! SUCCESS")
                    return
                }

                saveSearchKeyword(keyword)
                adapter.submitList(response.body()?.books.orEmpty())
            }

            override fun onFailure(call: Call<SearchBookDto>, t: Throwable) {
                // TODO 실파처리
                Log.d(TAG, t.toString())
                hideHistoryView()
                Toast.makeText(this@MainActivity , "데이터를 받아오는데 실패하였습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun initBookRecyclerView () {
        adapter = BookAdapter(
            itemClickedListener = {
                val intent = Intent(this@MainActivity, DetailActivity::class.java)
                // 클래스를 직렬화하여 클래스 자체를 전달
                intent.putExtra("book", it)
                startActivity(intent)
            }
        )
        // recycler view 가 그려지는 방식 지정
        binding.booksRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.booksRecyclerView.adapter = adapter
    }

    private fun initHistoryRecyclerView () {
        historyAdapter = HistoryAdapter(
            historyDeleteClickedListener = { deleteSearchKeyword(it) }
        ) {
            val it = ""
            binding.searchEditText.setText(it)
            search(it)
        }
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.historyRecyclerView.adapter = historyAdapter

        initSearchEditText()
    }

    @SuppressLint("ClickableViewAccessibility")
    fun initSearchEditText () {
        binding.searchEditText.setOnKeyListener { v, keyCode, event ->
            // enter 키를 눌렀을 때 검색 수행 + 키를 누른 경우
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == MotionEvent.ACTION_DOWN) {
                search(binding.searchEditText.text.toString())
                true // 해당 event 를 처리함
            }
            false
        }
        binding.searchEditText.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                showHistoryView()
            }
            return@setOnTouchListener false
        }
    }

    private fun showHistoryView () {
        Thread {
            val keywords = db.historyDao().getAll().reversed()

            runOnUiThread {
                binding.historyRecyclerView.isVisible = true
                historyAdapter.submitList(keywords.orEmpty())
            }
        }.start()
    }
    private fun hideHistoryView () {
        binding.historyRecyclerView.isVisible = false
    }

    private fun saveSearchKeyword (keyword : String) {
        Thread {
            db.historyDao().insertHistory(History(null,keyword))
        }.start()
    }

    private fun deleteSearchKeyword (keyword : String) {
        Thread {
            db.historyDao().delete(keyword)
            // TODO view 갱신해주기
            showHistoryView()
        }.start()
    }

    companion object {
        private const val TAG = ".MainActivity"
    }
}