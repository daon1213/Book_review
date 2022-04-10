package com.daon.book_part3_04.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.daon.book_part3_04.databinding.ItemHistoryBinding
import com.daon.book_part3_04.model.History

class HistoryAdapter(
    val historyDeleteClickedListener: (String) -> Unit,
    historyKeywordClickedListener: () -> Unit
) : ListAdapter<History, HistoryAdapter.HistoryViewHolder>(diffUtill) {

    inner class HistoryViewHolder(
        private val binding : ItemHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind (history : History) {
            binding.historyKeywordTextView.text = history.keyword
            binding.historyKeywordTextView.setOnClickListener {
                historyKeywordClickedListener(history.keyword.orEmpty())
            }
            binding.historyKeywordDeleteButton.setOnClickListener {
                historyDeleteClickedListener(history.keyword.orEmpty())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        return HistoryViewHolder(ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtill = object : DiffUtil.ItemCallback<History>() {
            override fun areItemsTheSame(oldItem: History, newItem: History) = oldItem == newItem

            override fun areContentsTheSame(oldItem: History, newItem: History) = oldItem.uid == newItem.uid
        }
    }
}