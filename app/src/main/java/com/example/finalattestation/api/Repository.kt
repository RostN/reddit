package com.example.finalattestation.api

class Repository {
    //комментарии
    suspend fun postComments(page:String, query: String): Answer {
        return retrofit.postComments(name = query, page = page)[1].data
    }

    //Поиск
    suspend fun searchSubreddit(query: String, page: String): Answer {
        return retrofit.searchSubreddit(query = query, page = page).data
    }

    //Получение списка комментариев пользователя
    suspend fun loadUserComment(page: String, username: String): Answer {
        return retrofit.loadComments(username = username, page = page).data
    }

    //Сохраненная пользователем информация
    suspend fun loadSavedData(page: String, username: String, type: String): Answer {
        return retrofit.loadSaved(username = username, page = page, type = type).data
    }

    //Мои избранные сабреддиты все
    suspend fun loadMyFavoriteSubredditAll(page: String): Answer {
        return retrofit.loadFavoriteSubredditAll(page = page).data
    }

    //Саббердиты стороннего пользователя
    suspend fun loadUsersSubreddits(name: String, page: String): Answer {
        return retrofit.loadUsersSubreddits(user = name, page = page).data
    }

    //Данные о пользователях
    suspend fun loadUsersAbout(name: String): Answer {
        return retrofit.loadUsersInfo(user = name).data
    }

    //Загрузка сабреддитов
    suspend fun loadSubreddit(type: String, page: String): Answer {
        return retrofit.loadSubreddit(type = type, page = page).data
    }

    //Загрузка детальной информации о сабреддите
    suspend fun subredditDetails(id: String, page: String): Answer {
        return retrofit.subredditDetails(name = id, page = page).data
    }
}