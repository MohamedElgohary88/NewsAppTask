package com.elgohary.newsapptask.presentation.news_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.elgohary.newsapptask.domain.model.Article
import com.elgohary.newsapptask.domain.usecase.GetNewsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class NewsListViewModel @Inject constructor(
    getNewsUseCase: GetNewsUseCase
) : ViewModel() {

    val articles: Flow<PagingData<Article>> =
        getNewsUseCase().cachedIn(viewModelScope)
}

