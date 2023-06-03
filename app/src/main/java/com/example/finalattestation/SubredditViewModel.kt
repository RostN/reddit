package com.example.finalattestation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.finalattestation.api.Response
import com.example.finalattestation.api.SubredditPagingSource
import kotlinx.coroutines.flow.Flow
import kotlin.properties.Delegates

class SubredditViewModel : ViewModel() {
    var type: String = ""
    var mode by Delegates.notNull<Int>()
    var id: String = ""
    val pagedSubreddit: Flow<PagingData<Response>> =
        Pager(
            config = PagingConfig(pageSize = 25),
            pagingSourceFactory = { SubredditPagingSource(type, mode, id) }).flow.cachedIn(
            viewModelScope
        )
}