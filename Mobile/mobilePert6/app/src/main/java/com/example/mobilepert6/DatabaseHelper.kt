package com.example.mobilepert6

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE UserProfile (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                username TEXT UNIQUE NOT NULL,
                bio TEXT,
                location TEXT,
                website TEXT,
                join_date TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
            );
            """
        )

        db.execSQL(
            """
            CREATE TABLE UserRelation (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                follower_id INTEGER NOT NULL,
                following_id INTEGER NOT NULL,
                followed_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (follower_id) REFERENCES UserProfile(id),
                FOREIGN KEY (following_id) REFERENCES UserProfile(id)
            );
            """
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS UserProfile")
        db.execSQL("DROP TABLE IF EXISTS UserRelation")
        onCreate(db)
    }

    companion object {
        private const val DATABASE_NAME = "mobilepert6.db"
        private const val DATABASE_VERSION = 1
    }

    fun addUser(name: String, username: String, bio: String?, location: String?, website: String?) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("name", name)
            put("username", username)
            put("bio", bio)
            put("location", location)
            put("website", website)
        }
        db.insert("UserProfile", null, values)
        db.close()
    }

    fun getAllUsers(): List<UserProfile> {
        val db = readableDatabase
        val userList = mutableListOf<UserProfile>()
        val cursor = db.rawQuery("SELECT * FROM UserProfile", null)

        while (cursor.moveToNext()) {
            val user = UserProfile(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                username = cursor.getString(cursor.getColumnIndexOrThrow("username")),
                bio = cursor.getString(cursor.getColumnIndexOrThrow("bio")),
                location = cursor.getString(cursor.getColumnIndexOrThrow("location")),
                website = cursor.getString(cursor.getColumnIndexOrThrow("website")),
                joinDate = cursor.getString(cursor.getColumnIndexOrThrow("join_date"))
            )
            userList.add(user)
        }
        cursor.close()
        db.close()
        return userList
    }

    fun getUserProfile(id: Int): UserProfile? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM UserProfile WHERE id = ?", arrayOf(id.toString()))
        var userProfile: UserProfile? = null

        if (cursor.moveToFirst()) {
            userProfile = UserProfile(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                username = cursor.getString(cursor.getColumnIndexOrThrow("username")),
                bio = cursor.getString(cursor.getColumnIndexOrThrow("bio")),
                location = cursor.getString(cursor.getColumnIndexOrThrow("location")),
                website = cursor.getString(cursor.getColumnIndexOrThrow("website")),
                joinDate = cursor.getString(cursor.getColumnIndexOrThrow("join_date"))
            )
        }

        cursor.close()
        db.close()
        return userProfile
    }

    data class UserProfile(
        val id: Int,
        val name: String,
        val username: String,
        val bio: String?,
        val location: String?,
        val website: String?,
        val joinDate: String?
    )

    fun followUser(followerId: Int, followingId: Int): Boolean {
        val db = writableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM UserRelation WHERE follower_id = ? AND following_id = ?",
            arrayOf(followerId.toString(), followingId.toString())
        )

        if (cursor.count > 0) {
            cursor.close()
            db.close()
            return false
        }

        cursor.close()

        val values = ContentValues().apply {
            put("follower_id", followerId)
            put("following_id", followingId)
        }

        val success = db.insert("UserRelation", null, values) > 0
        db.close()
        return success
    }

    fun getFollowing(userId: Int): List<UserProfile> {
        val db = readableDatabase
        val followingList = mutableListOf<UserProfile>()

        val query = """
        SELECT u.id, u.name, u.username, u.bio
        FROM UserRelation r
        JOIN UserProfile u ON r.following_id = u.id
        WHERE r.follower_id = ?
    """

        val cursor = db.rawQuery(query, arrayOf(userId.toString()))

        while (cursor.moveToNext()) {
            val user = UserProfile(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                username = cursor.getString(cursor.getColumnIndexOrThrow("username")),
                bio = cursor.getString(cursor.getColumnIndexOrThrow("bio")),
                location = null,  // Tidak diambil
                website = null,   // Tidak diambil
                joinDate = null   // Tidak diambil
            )
            followingList.add(user)
        }

        cursor.close()
        db.close()
        return followingList
    }


    fun getFollowers(userId: Int): List<UserProfile> {
        val db = readableDatabase
        val followersList = mutableListOf<UserProfile>()

        val cursor = db.rawQuery(
            "SELECT u.* FROM UserProfile u INNER JOIN UserRelation r ON u.id = r.follower_id WHERE r.following_id = ?",
            arrayOf(userId.toString())
        )

        while (cursor.moveToNext()) {
            val user = UserProfile(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                username = cursor.getString(cursor.getColumnIndexOrThrow("username")),
                bio = cursor.getString(cursor.getColumnIndexOrThrow("bio")),
                location = cursor.getString(cursor.getColumnIndexOrThrow("location")),
                website = cursor.getString(cursor.getColumnIndexOrThrow("website")),
                joinDate = cursor.getString(cursor.getColumnIndexOrThrow("join_date"))
            )
            followersList.add(user)
        }

        cursor.close()
        db.close()
        return followersList
    }

    fun getFollowingCount(userId: Int): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM UserRelation WHERE user_id = ?",
            arrayOf(userId.toString())
        )

        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return count
    }

    fun getFollowersCount(userId: Int): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM UserRelation WHERE following_id = ?",
            arrayOf(userId.toString())
        )

        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return count
    }
}