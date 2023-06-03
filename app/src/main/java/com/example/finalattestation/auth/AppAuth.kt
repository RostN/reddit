package com.example.finalattestation.auth

import android.net.Uri
import androidx.core.net.toUri
import net.openid.appauth.*
import kotlin.coroutines.suspendCoroutine

object AppAuth {

    private val serviceConfiguration = AuthorizationServiceConfiguration(
        Uri.parse(AuthConfig.AUTH_URI),
        Uri.parse(AuthConfig.TOKEN_URI),
        null, // registration endpoint
        Uri.parse(AuthConfig.END_SESSION_URI)
    )

    fun getAuthRequest(): AuthorizationRequest {
        val redirectUri = AuthConfig.CALLBACK_URL.toUri()
        return AuthorizationRequest.Builder(
            serviceConfiguration,
            AuthConfig.CLIENT_ID,
            AuthConfig.RESPONSE_TYPE,
            redirectUri
        )
            .setScope(AuthConfig.SCOPE)
            .setState(AuthConfig.STATE)
            .setAdditionalParameters(mapOf("duration" to "permanent"))
            .build()
    }

    fun getEndSessionRequest(): EndSessionRequest {
        return EndSessionRequest.Builder(serviceConfiguration)
            .setPostLogoutRedirectUri(AuthConfig.LOGOUT_CALLBACK_URL.toUri())
            .build()
    }

    fun getRefreshTokenRequest(refreshToken: String): TokenRequest {

        return TokenRequest.Builder(
            serviceConfiguration,
            AuthConfig.CLIENT_ID
        )
            .setGrantType(GrantTypeValues.REFRESH_TOKEN)
            .setScopes(AuthConfig.SCOPE)
            .setGrantType(AuthConfig.GRANT_TYPE_CODE)
            .setRefreshToken(refreshToken)
            .build()
    }

    suspend fun performTokenRequestSuspend(
        authService: AuthorizationService,
        tokenRequest: TokenRequest,
    ): TokensModel {
        return suspendCoroutine { continuation ->
            authService.performTokenRequest(
                tokenRequest,
                ClientSecretBasic(AuthConfig.CLIENT_SECRET)
            ) { response, ex ->
                when {
                    response != null -> {
                        //получение токена произошло успешно
                        val tokens = TokensModel(
                            accessToken = response.accessToken.orEmpty(),
                            refreshToken = response.refreshToken.orEmpty(),
                            idToken = response.idToken.orEmpty()
                        )
                        continuation.resumeWith(Result.success(tokens))
                    }
                    //получение токенов произошло неуспешно, показываем ошибку
                    ex != null -> {
                        continuation.resumeWith(Result.failure(ex))
                    }
                    else -> error("unreachable")
                }
            }
        }
    }

    private fun getClientAuthentication(): ClientAuthentication {
        return ClientSecretPost(AuthConfig.CLIENT_SECRET)
    }

    object AuthConfig {
        const val CLIENT_SECRET = ""
        const val AUTH_URI = "https://old.reddit.com/api/v1/authorize"
        const val TOKEN_URI = "https://www.reddit.com/api/v1/access_token"
        const val END_SESSION_URI = "https://www.reddit.com/api/v1/revoke_token"
        const val RESPONSE_TYPE = ResponseTypeValues.CODE
        const val SCOPE = "identity edit flair history modconfig modflair " +
                "modlog modposts modwiki mysubreddits privatemessages read " +
                "report save submit subscribe vote wikiedit wikiread"
        const val CALLBACK_URL = "com.example.finalattestation://callback"
        const val LOGOUT_CALLBACK_URL = "com.example.finalattestation://callback"
        const val CLIENT_ID = "YOUR_CLIENT_ID"
        const val STATE = "com.example.finalattestation:state"
        const val GRANT_TYPE_CODE = "authorization_code"
    }
}