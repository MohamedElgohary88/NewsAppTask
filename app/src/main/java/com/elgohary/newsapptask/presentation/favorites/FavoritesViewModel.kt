package com.elgohary.newsapptask.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elgohary.newsapptask.domain.model.Article
import com.elgohary.newsapptask.domain.usecase.DeleteArticleUseCase
import com.elgohary.newsapptask.domain.usecase.SelectArticlesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val selectArticlesUseCase: SelectArticlesUseCase,
    private val deleteArticleUseCase: DeleteArticleUseCase
) : ViewModel() {

    private val _favorites = MutableStateFlow<List<Article>>(emptyList())
    val favorites: StateFlow<List<Article>> = _favorites

    init {
        observeFavorites()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            selectArticlesUseCase().collectLatest { articles ->
                _favorites.value = articles
            }
        }
    }

    fun deleteArticle(article: Article) {
        viewModelScope.launch {
            deleteArticleUseCase(article)
        }
    }
}

