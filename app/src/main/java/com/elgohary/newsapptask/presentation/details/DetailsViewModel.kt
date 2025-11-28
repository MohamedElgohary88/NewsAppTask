package com.elgohary.newsapptask.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elgohary.newsapptask.domain.model.Article
import com.elgohary.newsapptask.domain.usecase.SelectArticlesUseCase
import com.elgohary.newsapptask.domain.usecase.UpsertArticleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val upsertArticleUseCase: UpsertArticleUseCase,
    selectArticlesUseCase: SelectArticlesUseCase
) : ViewModel() {

    private val _savedUrls = MutableStateFlow<Set<String>>(emptySet())
    val savedUrls: StateFlow<Set<String>> = _savedUrls.asStateFlow()

    init {
        viewModelScope.launch {
            selectArticlesUseCase().collectLatest { list ->
                _savedUrls.value = list.mapNotNull { it.url }.toSet()
            }
        }
    }

    fun saveArticle(article: Article) {
        viewModelScope.launch {
            upsertArticleUseCase(article)
        }
    }
}
