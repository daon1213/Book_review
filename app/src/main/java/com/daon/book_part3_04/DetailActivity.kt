package com.daon.book_part3_04

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.bumptech.glide.Glide
import com.daon.book_part3_04.databinding.ActivityDetailBinding
import com.daon.book_part3_04.model.Book
import com.daon.book_part3_04.model.Review

class DetailActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDetailBinding
    private lateinit var db : AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = getAppDatabase(this)

        val book = intent.getParcelableExtra<Book>("book")

        binding.titleTextView.text = book?.title.orEmpty()
        binding.descriptionTextView.text = book?.title.orEmpty()
        Glide.with(this)
            .load(book?.converSmallUrl.orEmpty())
            .into(binding.coverImageView)

        binding.saveButton.setOnClickListener {
            Thread {
                db.reviewDao().saveReview(
                    Review(
                        book?.id?.toInt() ?: 0,
                        binding.reviewEditText.text.toString()
                    )
                )
                runOnUiThread {
                    Toast.makeText(this, "저장되었습니다.",Toast.LENGTH_SHORT).show()
                }
            }.start()
        }

        Thread{
            val review = db.reviewDao().getOneReview(book?.id?.toInt() ?: 0)

            review?.let {
                runOnUiThread {
                    binding.reviewEditText.setText(review.review.orEmpty())
                }
            }
        }.start()
    }

}

