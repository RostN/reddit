package com.example.finalattestation

import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.finalattestation.api.retrofit
import com.example.finalattestation.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

//Основные настройки: токен, первый запуск
const val mainConfiguration = "kotlinsharedpreference"
lateinit var sharedPreferences: SharedPreferences
lateinit var editor: SharedPreferences.Editor
lateinit var formatter: SimpleDateFormat
var accessTokenApi = ""
var welcomeScreen = false
var subscribersText = ""
var subredditAuthor = ""
var sharedText = ""
var myUserName = ""

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)
        editor = sharedPreferences.edit()

        //Присвоение токена
        accessTokenApi = sharedPreferences.getString("TOKEN", "").toString()

        //Получение имени авторизованного пользователя
        myUserName = sharedPreferences.getString("MyUserName", "").toString()
        if (myUserName == "") {
            lifecycleScope.launch {
                myUserName = retrofit.loadMyAccount().name
                editor.putString("MyUserName", myUserName)
                editor.apply()
                println("Имя аккаунта сохранено")
            }
        }

        //Текстовые заглушки
        subscribersText = getString(R.string.subreddit_count_subscribers)
        subredditAuthor = getString(R.string.subreddit_author)
        sharedText = getString(R.string.subreddit_details_share_text)
        formatter = SimpleDateFormat(getString(R.string.time_pattern))

        println("TOKEN:\n$accessTokenApi")
        println("My username:\n$myUserName")
    }

    //Подписка или отписка
    fun subscribeOrUnsubscribe(action: String, sr_name: String) {
        lifecycleScope.launch {
            retrofit.subscribeOrUnsubscribe(action = action, sr_name = sr_name)
        }
    }
}