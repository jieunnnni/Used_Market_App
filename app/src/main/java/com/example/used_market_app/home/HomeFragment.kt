package com.example.used_market_app.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.used_market_app.DBKey.Companion.CHILD_CHAT
import com.example.used_market_app.DBKey.Companion.DB_ARTICLES
import com.example.used_market_app.DBKey.Companion.DB_USERS
import com.example.used_market_app.R
import com.example.used_market_app.chatlist.ChatListItem
import com.example.used_market_app.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.FirebaseAuthKtxRegistrar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment(R.layout.fragment_home) {

    companion object {
        fun newInstance() = HomeFragment()
        const val TAG = "HomeFragment"
    }

    private var binding: FragmentHomeBinding? = null

    private lateinit var adapter: ArticleAdapter
    private lateinit var articleDB: DatabaseReference
    private lateinit var userDB: DatabaseReference

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    private val articleList = mutableListOf<ArticleModel>()
    private val listener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val articleModel = snapshot.getValue(ArticleModel::class.java)
            articleModel ?: return
            articleList.add(articleModel)
            adapter.submitList(articleList)
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onChildRemoved(snapshot: DataSnapshot) {}

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onCancelled(error: DatabaseError) {}
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentHomeBinding = FragmentHomeBinding.bind(view)
        binding = fragmentHomeBinding

        articleList.clear()
        userDB = Firebase.database.reference.child(DB_USERS)
        articleDB = Firebase.database.reference.child(DB_ARTICLES)

        adapter = ArticleAdapter(onItemClicked = { articleModel ->
            if (auth.currentUser != null) {
                if (auth.currentUser?.uid != articleModel.sellerId) {
                    val chatRoom = ChatListItem(
                        buyerId = auth.currentUser?.uid.orEmpty(),
                        sellerId = articleModel.sellerId,
                        itemTitle = articleModel.title,
                        key = System.currentTimeMillis()
                    )
                    userDB.child(auth.currentUser?.uid.orEmpty())
                        .child(CHILD_CHAT)
                        .push()
                        .setValue(chatRoom)

                    userDB.child(articleModel.sellerId)
                        .child(CHILD_CHAT)
                        .push()
                        .setValue(chatRoom)

                    Snackbar.make(view, "채팅방이 생성되었습니다. 채팅탭에서 확인해주세요.", Snackbar.LENGTH_LONG).show()
                } else {
                    Snackbar.make(view, "본인이 등록한 아이템입니다.", Snackbar.LENGTH_LONG).show()
                }
            } else {
                Snackbar.make(view, "로그인 후 사용해주세요.", Snackbar.LENGTH_LONG).show()
            }
        })

        fragmentHomeBinding.articleRecyclerView.layoutManager = LinearLayoutManager(context)
        fragmentHomeBinding.articleRecyclerView.adapter = adapter

        fragmentHomeBinding.addFloatingButton.setOnClickListener {
            context?.let {
                if (auth.currentUser != null) {
                    val intent = Intent(it, AddArticleActivity::class.java)
                    startActivity(intent)
                } else {
                    Snackbar.make(view, "로그인 후 사용하실 수 있습니다.", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
        articleDB.addChildEventListener(listener)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        articleDB.removeEventListener(listener)
    }
}