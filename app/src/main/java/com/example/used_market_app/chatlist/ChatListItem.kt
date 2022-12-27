package com.example.used_market_app.chatlist

data class ChatListItem(
    val buyerId: String,
    val sellerId: String,
    val itemTitle: String,
    val key: Long
) {
    constructor(): this("", "", "", 0)
}