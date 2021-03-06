package ru.skillbranch.skillarticles.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import ru.skillbranch.skillarticles.data.ArticleData
import ru.skillbranch.skillarticles.data.ArticlePersonalInfo
import ru.skillbranch.skillarticles.data.repositories.ArticleRepository
import ru.skillbranch.skillarticles.extensions.toAppSettings
import ru.skillbranch.skillarticles.extensions.toArticlePersonalInfo
import ru.skillbranch.skillarticles.extensions.format
import ru.skillbranch.skillarticles.extensions.indexesOf
import java.io.Serializable

class ArticleViewModel(private val articleId: String, savedStateHandle: SavedStateHandle) :
    BaseViewModel<ArticleState>(ArticleState(), savedStateHandle), IArticleViewModel {
    private val repository = ArticleRepository()

    init {
        //subscribe on mutable data
        subscribeOnDataSource(getArticleData()) { article, state ->
            article ?: return@subscribeOnDataSource null
            Log.e("ArticleViewModel", "author: ${article.author}");
            state.copy(
                shareLink = article.shareLink,
                title = article.title,
                category = article.category,
                categoryIcon = article.categoryIcon,
                date = article.date.format(),
                author = article.author
            )
        }

        subscribeOnDataSource(getArticleContent()) { content, state ->
            content ?: return@subscribeOnDataSource null
            state.copy(
                isLoadingContent = false,
                content = content
            )
        }

        subscribeOnDataSource(getArticlePersonalInfo()) { info, state ->
            info ?: return@subscribeOnDataSource null
            state.copy(
                isBookmark = info.isBookmark,
                isLike = info.isLike
            )
        }

        subscribeOnDataSource(repository.getAppSettings()) { settings, state ->
            state.copy(
                isDarkMode = settings.isDarkMode,
                isBigText = settings.isBigText
            )
        }
    }

    //load text from network
    override fun getArticleContent(): LiveData<List<String>?> {
        return repository.loadArticleContent(articleId)
    }

    //load data from db
    override fun getArticleData(): LiveData<ArticleData?> {
        return repository.getArticle(articleId)
    }

    //load data from db
    override fun getArticlePersonalInfo(): LiveData<ArticlePersonalInfo?> {
        return repository.loadArticlePersonalInfo(articleId)
    }

    //app settings
    override fun handleNightMode() {
        val settings = currentState.toAppSettings()
        repository.updateSettings(settings.copy(isDarkMode = !settings.isDarkMode))
    }

    override fun handleUpText() {
        repository.updateSettings(currentState.toAppSettings().copy(isBigText = true))
    }

    override fun handleDownText() {
        repository.updateSettings(currentState.toAppSettings().copy(isBigText = false))
    }


    //personal article info
    override fun handleBookmark() {
        val info = currentState.toArticlePersonalInfo()
        repository.updateArticlePersonalInfo(info.copy(isBookmark = !info.isBookmark))

        val msg = if (currentState.isBookmark) "Add to bookmarks" else "Remove from bookmarks"
        notify(Notify.TextMessage(msg))
    }

    override fun handleLike() {
        Log.e("ArticleViewModel", "handle like: ");
        val isLiked = currentState.isLike
        val toggleLike = {
            val info = currentState.toArticlePersonalInfo()
            repository.updateArticlePersonalInfo(info.copy(isLike = !info.isLike))
        }

        toggleLike()

        val msg = if (!isLiked) Notify.TextMessage("Mark is liked")
        else {
            Notify.ActionMessage(
                "Don`t like it anymore", //message
                "No, still like it", //action label on snackbar
                toggleLike // handler function , if press "No, still like it" on snackbar, then toggle again
            )
        }

        notify(msg)
    }


    //not implemented
    override fun handleShare() {
        val msg = "Share is not implemented"
        notify(Notify.ErrorMessage(msg, "OK", null))
    }


    //session state
    override fun handleToggleMenu() {
        updateState { it.copy(isShowMenu = !it.isShowMenu) }
    }

    override fun handleSearchMode(isSearch: Boolean) {
        updateState { it.copy(isSearch = isSearch, isShowMenu = false, searchPosition = 0) }
    }

    override fun handleSearch(query: String?) {
        var result: List<Pair<Int, Int>> = emptyList()
        if (query == null) {
            return
        } else {
            result = currentState.content.firstOrNull()?.let {
                it.indexesOf(query).map { it to it + query.length }
            } ?: arrayListOf(0 to 0)

        }



        updateState { it.copy(searchQuery = query, searchResults = result) }
    }

    override fun handleUpResult() {
        updateState { it.copy(searchPosition = it.searchPosition.dec()) }
    }

    override fun handleDownResult() {
        updateState { it.copy(searchPosition = it.searchPosition.inc()) }
    }
}

data class ArticleState(
    val isAuth: Boolean = false, //???????????????????????? ??????????????????????
    val isLoadingContent: Boolean = true, //?????????????? ??????????????????????
    val isLoadingReviews: Boolean = true, //???????????? ??????????????????????
    val isLike: Boolean = false, //???????????????? ?????? Like
    val isBookmark: Boolean = true, //?? ??????????????????
    val isShowMenu: Boolean = false, //???????????????????????? ????????
    val isBigText: Boolean = false, //?????????? ????????????????
    val isDarkMode: Boolean = false, //???????????? ??????????
    val isSearch: Boolean = false, //?????????? ????????????
    val searchQuery: String? = null, // ???????????????? ????????????
    val searchResults: List<Pair<Int, Int>> = emptyList(), //???????????????????? ???????????? (?????????????????? ?? ???????????????? ??????????????)
    val searchPosition: Int = 0, //?????????????? ?????????????? ???????????????????? ????????????????????
    val shareLink: String? = null, //???????????? Share
    val title: String? = null, //?????????????????? ????????????
    val category: String? = null, //??????????????????
    val categoryIcon: Any? = null, //???????????? ??????????????????
    val date: String? = null, //???????? ????????????????????
    val author: Any? = null, //?????????? ????????????
    val poster: String? = null, //?????????????? ????????????
    val content: List<String> = emptyList(), //??????????????
    val reviews: List<Any> = emptyList() //??????????????????????
) : Serializable

data class BottombarData(
    val isLike: Boolean = false,
    val isBookmark: Boolean = true,
    val isShowMenu: Boolean = false,
    val isSearch: Boolean = false,
    val resultCount: Int = 0,
    val searchPosition: Int = 0
)

data class SubmenuData(
    val isShownMenu: Boolean = false,
    val isBigText: Boolean = false,
    val isDarkMode: Boolean = false
)