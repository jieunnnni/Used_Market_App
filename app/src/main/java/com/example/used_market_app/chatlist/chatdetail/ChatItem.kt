package com.example.used_market_app.chatlist.chatdetail

data class ChatItem(
    val senderId: String,
    val message: String
) {
    constructor(): this("", "")
}