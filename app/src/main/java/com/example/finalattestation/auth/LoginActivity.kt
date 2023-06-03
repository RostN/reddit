package com.example.finalattestation.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.finalattestation.*
import com.example.finalattestation.auth.AppAuth.getRefreshTokenRequest
import com.example.finalattestation.databinding.ActivityLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse

class LoginActivity : AppCompatActivity() {
    private lateinit var onboardingItemsAdapter: OnBoardingItemsAdapter
    private lateinit var indicatorContainer: LinearLayout
    private lateinit var binding: ActivityLoginBinding
    private lateinit var replaceIntent: Intent
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) //включение светлой темы
        sharedPreferences = getSharedPreferences(mainConfiguration, Context.MODE_PRIVATE)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceIntent = Intent(this@LoginActivity, MainActivity::class.java)
        val nowTime = System.currentTimeMillis() / 1000
        val lifeToken = sharedPreferences.getLong("Token Life", 0)
        accessTokenApi = sharedPreferences.getString("TOKEN", "").toString()
        val refreshToken = sharedPreferences.getString("Token refresh", "").toString()

        if (refreshToken!=""){
            //Если время токена просрочено, стереть старый токен
            if (nowTime > lifeToken) {
                accessTokenApi = ""
                println("Токен просрочен")
//                getRefreshTokenRequest(refreshToken)
                checkAuthorization()
            }
        }

        //Потом удалить
        welcomeScreen = sharedPreferences.getBoolean("FIRST", false)

        //Если перввый запуск показываем онборд
        if (welcomeScreen) {
            exitOnBoarding()
            checkAuthorization()
        } else {
            setOnboardingItems()
        }
    }

    //Уходим на активити с доступом
    private fun changeActivity() {
        startActivity(replaceIntent)
        finish()
    }

    //Если токен есть, пропускаем авторизацию
    private fun checkAuthorization() {
        if (accessTokenApi == "") {
            bindViewModel()
        } else {
            changeActivity()
        }
    }

    //Выход из онбординга
    private fun exitOnBoarding() {
        binding.onBoardControl.isVisible = false
        binding.loginControl.isVisible = true
        editor = sharedPreferences.edit()
        editor.putBoolean("FIRST", true)
        editor.apply()
        checkAuthorization()
    }

    //онбординг начало
    private fun setOnboardingItems() {
        onboardingItemsAdapter = OnBoardingItemsAdapter(
            listOf(
                OnboardingItem(
                    onBoardingImage = R.drawable.onboard_one,
                    title = getString(R.string.title_onboard_one),
                    description = getString(R.string.description_onboard_one)
                ),
                OnboardingItem(
                    onBoardingImage = R.drawable.onboard_two,
                    title = getString(R.string.title_onboard_two),
                    description = getString(R.string.description_onboard_two)
                ),
                OnboardingItem(
                    onBoardingImage = R.drawable.onboard_three,
                    title = getString(R.string.title_onboard_three),
                    description = getString(R.string.description_onboard_three)
                )
            )
        )

        val onBoardingViewPager = binding.onBoardingViewPager
        onBoardingViewPager.adapter = onboardingItemsAdapter
        onBoardingViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })
        (onBoardingViewPager.getChildAt(0) as RecyclerView).overScrollMode =
            RecyclerView.OVER_SCROLL_NEVER
        binding.imageNext.setOnClickListener {
            if (onBoardingViewPager.currentItem + 1 < onboardingItemsAdapter.itemCount) {
                onBoardingViewPager.currentItem += 1
            } else {
                exitOnBoarding()
            }
        }
        binding.textSkip.setOnClickListener {
            exitOnBoarding()
        }
        setupIndicators()
    }

    //Установка индикатора страницы
    private fun setupIndicators() {
        indicatorContainer = binding.indicatorsContainer
        val indicators = arrayOfNulls<ImageView>(onboardingItemsAdapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams = LinearLayout
            .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext)
            indicators[i]?.let {
                it.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.onboard_indicator_inactive_background
                    )
                )
                it.layoutParams = layoutParams
                indicatorContainer.addView(it)
            }
        }
        setCurrentIndicator(0)
    }

    //Отслеживание положения страницы
    private fun setCurrentIndicator(position: Int) {
        val childCount = indicatorContainer.childCount
        for (i in 0 until childCount) {
            val imageView = indicatorContainer.getChildAt(i) as ImageView
            if (i == position) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.onboard_indicator_active_background
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.onboard_indicator_inactive_background
                    )
                )
            }
        }
    }    //онбординг конец

    //Получение кода авторизациии отправка его на обменя
    private val getAuthResponse =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val dataIntent = it.data ?: return@registerForActivityResult
            handleAuthResponseIntent(dataIntent)
        }

    private fun bindViewModel() {
        binding.loginBtn.setOnClickListener { viewModel.openLoginPage() }

        viewModel.loadingFlow.launchAndCollectIn(this) {
            updateIsLoading(it)
        }
        viewModel.openAuthPageFlow.launchAndCollectIn(this) {
            openAuthPage(it)
        }
        viewModel.authSuccessFlow.launchAndCollectIn(this) {
            changeActivity()
        }
    }

    //Блокировка кнопки пока идет авторизация
    private fun updateIsLoading(isLoading: Boolean) = with(binding) {
        binding.loginBtn.isVisible = !isLoading
    }

    //Открытие страницы для авторизации
    private fun openAuthPage(intent: Intent) {
        getAuthResponse.launch(intent)
    }

    //Обмен кода авторизации на токен
    private fun handleAuthResponseIntent(intent: Intent) {
        // пытаемся получить ошибку из ответа. null - если все ок
        val exception = AuthorizationException.fromIntent(intent)
        // пытаемся получить запрос для обмена кода на токен, null - если произошла ошибка
        val tokenExchangeRequest = AuthorizationResponse.fromIntent(intent)
            ?.createTokenExchangeRequest()
        when {
            // авторизация завершались ошибкой
            exception != null -> viewModel.onAuthCodeFailed(exception)
            // авторизация прошла успешно, меняем код на токен
            tokenExchangeRequest != null ->
                viewModel.onAuthCodeReceived(tokenExchangeRequest)
        }
    }
}

inline fun <T> Flow<T>.launchAndCollectIn(
    owner: LifecycleOwner,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline action: suspend CoroutineScope.(T) -> Unit
) = owner.lifecycleScope.launch {
    owner.repeatOnLifecycle(minActiveState) {
        collect {
            action(it)
        }
    }
}