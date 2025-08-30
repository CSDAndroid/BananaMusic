package com.example.musicapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.model.database.User
import com.example.musicapp.model.repository.LoginRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginStatus = MutableLiveData<LoginStatus>()
    private val _allUsers = MutableLiveData<List<User>>()
    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser
    private val _registerStatus = MutableLiveData<RegisterStatus>()
    val loginStatus: LiveData<LoginStatus> = _loginStatus
    val registerStatus: LiveData<RegisterStatus> = _registerStatus
    val allUsers: LiveData<List<User>> = _allUsers

    enum class LoginStatus {
        SUCCESS, FAILURE
    }

    enum class RegisterStatus {
        SUCCESS, FAILURE
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            val user = loginRepository.login(username, password)
            if (user != null) {
                _loginStatus.value = LoginStatus.SUCCESS
                _currentUser.value = user // 设置 currentUser
                Log.d("LoginViewModel", "Login successful: ${user.username}, user_img: ${user.user_img}")
            } else {
                _loginStatus.value = LoginStatus.FAILURE
            }
        }
    }

    fun register(username: String, password: String, user_img: String?) {
        viewModelScope.launch {
            val registerResult = loginRepository.register(username, password, user_img)
            _registerStatus.value = if (registerResult) RegisterStatus.SUCCESS else RegisterStatus.FAILURE
        }
        Log.d("LoginViewModel", "User:")
        fetchAllUsers()
    }

    private fun fetchAllUsers() {
        viewModelScope.launch {
            val users = loginRepository.getAllUsers()
            users.forEach { user ->
                Log.d("LoginViewModel", "User: ${user.username}, Password: ${user.password}, user_img: ${user.user_img}")
            }
        }
    }
}