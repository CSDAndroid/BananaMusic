package com.example.musicapp.view.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.musicapp.Factory.LoginViewModelFactory
import com.example.musicapp.R
import com.example.musicapp.model.database.AppDatabase
import com.example.musicapp.model.database.User
import com.example.musicapp.model.repository.LoginRepository
import com.example.musicapp.viewmodel.LoginViewModel
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var iv_logo: CircleImageView

    private var avatarPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        iv_logo = findViewById(R.id.iv_logo)
        val btn_login = findViewById<Button>(R.id.btn_login)
        val et_username = findViewById<EditText>(R.id.et_username)
        val et_password = findViewById<EditText>(R.id.et_password)
        val tv_error_message = findViewById<TextView>(R.id.tv_error_message)
        val btn_register = findViewById<Button>(R.id.btn_register)
        val userDao = AppDatabase.getInstance(this).userDao()
        val loginRepository = LoginRepository(userDao)
        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory(loginRepository)).get(LoginViewModel::class.java)




        iv_logo.setOnClickListener {
            selectImage()

        }

        btn_login.setOnClickListener {
            val username = et_username.text.toString()
            val password = et_password.text.toString()
            loginViewModel.login(username, password)
            lifecycleScope.launch {
                val nowUser = userDao.getUserByUsername(username)
                sharedPreferences.edit().apply {
                    putString("username", nowUser?.username)
                    putString("user_img", nowUser?.user_img)
                    Log.d("now_user", "onCreate:$nowUser ")
                }.apply()
            }


        }
        val username = sharedPreferences.getString("username", null)
        val userImg = sharedPreferences.getString("user_img", "1")
        Log.d("now_user_register", "onCreate:$userImg + $username ")
        userImg?.let {
            Glide.with(this)
                .load(it)
                .circleCrop()
                .into(iv_logo)
        }
        et_username.setText(username)

        btn_register.setOnClickListener {
            val username = et_username.text.toString()
            val password = et_password.text.toString()
            loginViewModel.register(username, password, avatarPath)
            lifecycleScope.launch {
                val nowUser = userDao.getUserByUsername(username)
                sharedPreferences.edit().apply {
                    putString("username", nowUser?.username)
                    putString("user_img", nowUser?.user_img)
                    Log.d("now_user", "onCreate:$nowUser ")
                }.apply()
            }
        }

        loginViewModel.registerStatus.observe(this) { status ->
            when (status) {
                LoginViewModel.RegisterStatus.SUCCESS -> {
                    Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show()
                }
                LoginViewModel.RegisterStatus.FAILURE -> {

                    Toast.makeText(this, "注册失败", Toast.LENGTH_SHORT).show()
                }
            }
        }



        loginViewModel.loginStatus.observe(this) { status ->
            when (status) {
                LoginViewModel.LoginStatus.SUCCESS -> {
                    val intent = Intent(this, MainActivity::class.java)
                    val username = sharedPreferences.getString("username", null) // 默认值为 null
                    val userImg = sharedPreferences.getString("user_img", null)
                    userImg?.let {
                        Glide.with(this)
                            .load(it)
                            .circleCrop()
                            .into(iv_logo)
                    }
                    startActivity(intent)

                }
                LoginViewModel.LoginStatus.FAILURE -> {
                    Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImage = data.data
            iv_logo.setImageURI(selectedImage)

            // Save the image to a file and get the path
            val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(selectedImage!!))
            val et_username = findViewById<EditText>(R.id.et_username)
            val username = et_username.text.toString()
            avatarPath = saveBitmapToFile(bitmap, username)
            Log.d("now_select", "onCreate: $avatarPath")
        }
    }

    private fun saveBitmapToFile(bitmap: Bitmap, username: String): String? {
        // 使用用户名和时间戳生成唯一的文件名
        val timestamp = System.currentTimeMillis()
        val fileName = "${username}_$timestamp.jpg"
        val file = File(avatarDir, fileName)
        return try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            file.absolutePath
        } catch (e: IOException) {
            Log.e("LoginActivity", "Error saving bitmap to file", e)
            null
        }
    }
    private val avatarDir: File
        get() {
            val dir = File(externalCacheDir, "avatars")
            if (!dir.exists()) {
                dir.mkdirs()
            }
            return dir
        }
}