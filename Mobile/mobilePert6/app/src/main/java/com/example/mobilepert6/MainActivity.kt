package com.example.mobilepert6

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val dbHelper = DatabaseHelper(this)

        // Tambahkan Data Dummy
        //dbHelper.addUser("Reva D", "@revadam", "Orang baik", "Jakarta", "https://reva.com")

        val userList = dbHelper.getAllUsers()
        for (user in userList) {
            Log.d("DATABASE", "User: ${user.name} - ${user.username}")
        }

        val userProfile = dbHelper.getUserProfile(1) // Ambil user dengan ID 1

        userProfile?.let {
            val customView = findViewById<CustomView>(R.id.customView)
            customView.setUserProfile(it)
        }

        val success = dbHelper.followUser(1, 2)
        if (success) {
            println("Berhasil mengikuti user!")
        } else {
            println("Sudah mengikuti sebelumnya.")
        }
        val followingList = dbHelper.getFollowing(1)
        for (user in followingList) {
            println("Mengikuti: ${user.username}")
        }
        val followersList = dbHelper.getFollowers(1)
        for (user in followersList) {
            println("Diikuti oleh: ${user.username}")
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}