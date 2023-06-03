package com.example.finalattestation.auth

import com.example.finalattestation.editor
import com.example.finalattestation.sharedPreferences
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.EndSessionRequest
import net.openid.appauth.TokenRequest
import timber.log.Timber

class AuthRepository {

    fun logout() {
        TokenStorage.accessToken = null
        TokenStorage.refreshToken = null
        TokenStorage.idToken = null
    }

    fun getAuthRequest(): AuthorizationRequest {
        return AppAuth.getAuthRequest()
    }

    fun getEndSessionRequest(): EndSessionRequest {
        return AppAuth.getEndSessionRequest()
    }

    suspend fun performTokenRequest(
        authService: AuthorizationService,
        tokenRequest: TokenRequest,
    ) {
        val tokens = AppAuth.performTokenRequestSuspend(authService, tokenRequest)
//        обмен кода на токен произошел успешно, сохраняем токены и завершаем авторизацию
        TokenStorage.accessToken = tokens.accessToken
        TokenStorage.refreshToken = tokens.refreshToken
        TokenStorage.idToken = tokens.idToken
        Timber.tag("Oauth")
            .d("5. Tokens accepted:\n access=${tokens.accessToken}\nrefresh=${tokens.refreshToken}\nidToken=${tokens.idToken}")
        editor = sharedPreferences.edit()
        val nowTime = System.currentTimeMillis() / 1000
        editor.putString("TOKEN", TokenStorage.accessToken)
        editor.putString("Token refresh",TokenStorage.refreshToken)
        editor.putLong("Token Life", nowTime + 86000)
        editor.apply()
        println("Токен сохранён")
    }
}

object TokenStorage {
    var accessToken: String? = null
    var refreshToken: String? = null
    var idToken: String? = null
}

data class TokensModel(
    val accessToken: String,
    val refreshToken: String,
    val idToken: String
)