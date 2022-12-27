package com.example.used_market_app.chatlist.chatdetail

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.used_market_app.DBKey.Companion.DB_CHATS
import com.example.used_market_app.R
import com.example.used_market_app.databinding.ActivityChatRoomBinding
import com.example.used_market_app.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatRoomActivity : AppCompatActivity() {

    private val binding by lazy { ActivityChatRoomBinding.inflate(layoutInflater) }

    private val auth: FirebaseAuth by lazy { Firebase.auth }

    private val chatList = mutableListOf<ChatItem>()
    private val adapter = ChatItemAdapter()
    private var chatDB: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val chatKey = intent.getLongExtra("chatKey", -1)

        chatDB = Firebase.database.reference.child(DB_CHATS).child("$chatKey")

        chatDB?.addChildEventListener(object : ChildEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatItem = snapshot.getValue(ChatItem::class.java)
                chatItem ?: return

                chatList.add(chatItem)
                adapter.submitList(chatList)
                adapter.notifyDataSetChanged()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })

        binding.chatRecyclerView.adapter = adapter
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)

        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.sendButton.setOnClickListener {
            val chatItem = ChatItem(
                senderId = auth.currentUser!!.uid,
                message = binding.messageEditText.text.toString()
            )
            chatDB?.push()?.setValue(chatItem)
        }
    }
}