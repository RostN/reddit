package com.example.finalattestation.api

import androidx.paging.PagingSource
import androidx.paging.PagingState

class SubredditPagingSource
    (
    private val type: String,
    private val mode: Int,
    private val id: String,
) : PagingSource<String, Response>() {
    private val firstPage: String = ""
    private val repository = Repository()

    override fun getRefreshKey(state: PagingState<String, Response>): String =
        firstPage

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Response> {
        val page = params.key ?: firstPage
        return kotlin.runCatching {
            /*
           mode 0 - сабреддиты популярные и новые
           mode 1 - посты в сабреддите
           mode 2 - сабреддиты стороннего пользователя
           mode 3 - избранные сабреддиты пользователя
           mode 4 - сохранённые посты пользователя
           mode 5 - комментарии мои
           mode 6 - поиск
           mode 7 - комменты
             */
            when (mode) {
                0 -> repository.loadSubreddit(page = page, type = type)
                1 -> repository.subredditDetails(page = page, id = id)
                2 -> repository.loadUsersSubreddits(page = page, name = id)
                3 -> repository.loadMyFavoriteSubredditAll(page = page)
                4 -> repository.loadSavedData(page = page, username = id, type = type)
                5 -> repository.loadUserComment(page = page, username = id)
                6 -> repository.searchSubreddit(page = page, query = id)
                else -> repository.postComments(page=page, query = type)
            }
        }.fold(
            onSuccess = {
                LoadResult.Page(
                    data = it.children,
                    prevKey = null,
                    nextKey = if (it.after == "") "" else it.after
                )
            },
            onFailure = {
                LoadResult.Error(it)
            }
        )
    }
}