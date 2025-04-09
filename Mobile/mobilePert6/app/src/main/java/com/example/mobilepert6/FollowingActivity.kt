package com.example.mobilepert6

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class FollowingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_following)

        supportActionBar?.title = "Following"
        val dbHelper = DatabaseHelper(this)
        //dbHelper.followUser(1, 2) // User 1 mengikuti User 2


        val userId = 1

        val followingList = dbHelper.getFollowing(userId).map {
            FollowingCustomView.User(it.name, it.username, it.bio ?: "")
        }

        val followingCustomView = findViewById<FollowingCustomView>(R.id.followingcustomView)
        followingCustomView.setFollowingList(followingList)

    }
}