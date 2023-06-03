package com.example.finalattestation.api

import com.example.finalattestation.accessTokenApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

var baseUrl = "https://oauth.reddit.com"

var retrofit = Retrofit
    .Builder()
    .baseUrl(baseUrl)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(Api::class.java)

interface Api {
    //Коментарии
    @GET("/r/{name}")
    suspend fun postComments(
        @Header("Authorization") Request: String? = "Bearer $accessTokenApi",
        @Path("name") name: String,
        @Query("after") page: String? = "",
        @Query("depth") depth: Int? = 1,
    ): List<Result>

    //Поиск
    @GET("/subreddits/search")
    suspend fun searchSubreddit(
        @Header("Authorization") Request: String? = "Bearer $accessTokenApi",
        @Query("q") query: String = "",
        @Query("after") page: String? = "",
    ): Result

    //Очистка списка сохранений
    @POST("/api/unsave")
    suspend fun clearSaved(
        @Header("Authorization") Request: String? = "Bearer $accessTokenApi",
        @Query("id") name: String? = "",
        )

    //Получение списка коментариев
    @GET("/user/{username}/comments")
    suspend fun loadComments(
        @Header("Authorization") Request: String? = "Bearer $accessTokenApi",
        @Path("username") username: String,
        @Query("after") page: String? = "",
    ): Result

    //Получение списка сохранений
    @GET("/user/{username}/saved")
    suspend fun loadSaved(
        @Header("Authorization") Request: String? = "Bearer $accessTokenApi",
        @Path("username") username: String,
        @Query("after") page: String? = "",
        @Query("type") type: String? = "",
    ): Result

    //Получение списка сабреддитов, на которые подписан
    @GET("/subreddits/mine/")
    suspend fun loadFavoriteSubredditAll(
        @Header("Authorization") Request: String? = "Bearer $accessTokenApi",
        @Query("after") page: String? = "",
    ): Result

    //Получение данных об аккаунте
    @GET("/api/v1/me")
    suspend fun loadMyAccount(
        @Header("Authorization") Request: String? = "Bearer $accessTokenApi",
    ): UserAccount

    //Сабреддиты пользователя
    @GET("/user/{user}/submitted")
    suspend fun loadUsersSubreddits(
        @Header("Authorization") Request: String? = "Bearer $accessTokenApi",
        @Path("user") user: String,
        @Query("after") page: String? = "",
    ): Result

    //Информация о пользователях
    @GET("/user/{user}/about")
    suspend fun loadUsersInfo(
        @Header("Authorization") Request: String? = "Bearer $accessTokenApi",
        @Path("user") user: String,
    ): Result

    //Сабреддит
    @GET("/subreddits/{type}")
    suspend fun loadSubreddit(
        @Header("Authorization") Request: String? = "Bearer $accessTokenApi",
        @Path("type") type: String,
        @Query("after") page: String? = "",
    ): Result

    //Подписаться или отписаться от сабреддита
    @POST("/api/subscribe")
    suspend fun subscribeOrUnsubscribe(
        @Header("Authorization") Request: String? = "Bearer $accessTokenApi",
        @Query("action") action: String,
        @Query("sr_name") sr_name: String,
    )

    //Детальная информация о сабреддите
    @GET("/r/{name}")
    suspend fun subredditDetails(
        @Header("Authorization") Request: String? = "Bearer $accessTokenApi",
        @Path("name") name: String,
        @Query("after") page: String? = "",
    ): Result
}

//Инфорация об аккаунте
data class UserAccount(
    val name: String, //идентификатор
    val created: Long, //Дата создания
    val total_karma: Int, //Карма
    val icon_img: String, //Аватарка
    val subreddit: Subreddit, //доп информация
    val num_friends: Int, //Количество друзей
)

//Модель
data class Subreddit(
    //Комментарии
    val link_title: String, //Заголовок поста
    val link_author: String, //Автор поста
    val body: String, //Текст комментария
    val link_url: String, //Картинка в коммент

    //Информация о пользователях
    val user_is_banned: Boolean, //Проверка блокировки пользователя

    //Детальная информация сабреддита
    val author: String, //Автор поста
    val permalink: String, //Ссылка на комменты
    val num_comments: Int, //Количество комментов
    val selftext: String, //Описание поста
    val subreddit: String, //Название сабреддита
    val media_metadata: Any, //Картинка вариант 1
    val preview: Any, //Картинка вариант 2
    val id: String, //ИД поста

    //База сабреддитов
    val title: String, //Заголовок, отображаемое имя пользователя
    val display_name: String, //Идентификатор реддита
    val subscribers: Int,  //Подписчиков
    val created: Long, //Дата создания
    val public_description: String, //Изначальное(краткое) описание
    val description: String, //Полное описание
    val header_img: String, //Какая-то картинка
    var user_is_subscriber: Boolean, //Состояние подписки пользователя
    val name: String, //Именной идентификатор
)

//Сами данные по сабредитам
data class Response(
    val data: Subreddit,
)

//Вторичные данные + данные о пользователях
data class Answer(
    //Информация о пользователх
    val icon_img: String, //Аватарка
    val total_karma: Int, //Карма
    val is_friend: Boolean, //В друзьях ли
    val is_blocked: Boolean, //В ЧС ли
    val subscribers: Int,  //Подписчиков
    val created: Long, //Дата создания

    //Вторичные данные
    val after: String, //
    val children: List<Response>,
    val subreddit: Subreddit,
)

//Первичные данные
data class Result(
    val data: Answer
)