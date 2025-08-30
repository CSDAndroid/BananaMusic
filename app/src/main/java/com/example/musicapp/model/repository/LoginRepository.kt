package com.example.musicapp.model.repository

import android.content.SharedPreferences
import com.example.musicapp.model.database.User

import com.example.musicapp.model.database.UserDao
import kotlinx.coroutines.runBlocking

class LoginRepository(private val userDAO: UserDao) {
    fun login(username: String, password: String): User? {
        return runBlocking {
            val user = userDAO.getUserByUsername(username)
            if (user != null && user.password == password) {
                user
            } else {
                null
            }
        }
    }

    fun register(username: String, password: String, user_img: String? = null): Boolean {
        return runBlocking {
            val existingUser = userDAO.getUserByUsername(username)
            if (existingUser == null) {
                val newUser = User(username = username, password = password, user_img = user_img)
                userDAO.insertUser(newUser)
                true
            } else {
                false
            }
        }
    }

    fun getAllUsers(): List<User> {
        return runBlocking {
            userDAO.getAllUsers()
        }
    }
}