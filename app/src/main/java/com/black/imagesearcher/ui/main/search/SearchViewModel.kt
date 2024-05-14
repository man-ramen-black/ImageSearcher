package com.black.imagesearcher.ui.main.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.black.imagesearcher.base.viewmodel.EventViewModel
import com.black.imagesearcher.model.SearchModel
import com.black.imagesearcher.util.JsonUtil
import com.black.imagesearcher.util.Log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import javax.inject.Inject

/**
 * [SearchFragment]
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val model: SearchModel
): EventViewModel() {
    companion object {
        const val EVENT_START_DETAIL = "EVENT_SHOW_DETAIL"

        private const val SEARCH_DELAY = 500L
    }

    val searchKeyword = MutableLiveData<String>()

    val searchFlow = searchKeyword.asFlow()
        .flatMapLatest {
            delay(SEARCH_DELAY)
            yield()
            Pager(PagingConfig(pageSize = 20)) {
                SearchPagingSource(model, it)
            }.flow
        }.cachedIn(viewModelScope)

    fun onClickFavorite(item: SearchItem.ContentItem) = viewModelScope.launch {
        Log.v(item)
        model.toggleFavorite(item.content)
    }

    fun onClickContent(item: SearchItem.ContentItem) {
        Log.v(item)
        sendEvent(EVENT_START_DETAIL, JsonUtil.to(item.content))
    }

    fun onClickDelete() {
        searchKeyword.value = ""
    }
}