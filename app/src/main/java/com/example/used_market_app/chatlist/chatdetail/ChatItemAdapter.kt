package com.example.used_market_app.chatlist.chatdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.used_market_app.databinding.ItemChatBinding

class ChatItemAdapter() : ListAdapter<ChatItem, ChatItemAdapter.ChatITemViewHolder>(diffUtil) {

    inner class ChatITemViewHolder(private val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chatItem: ChatItem) {
            binding.senderTextView.text = chatItem.senderId
            binding.messageTextView.text = chatItem.message
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatITemViewHolder {
        return ChatITemViewHolder(
            ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ChatITemViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ChatItem>() {
            override fun areItemsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}