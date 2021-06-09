package ru.skillbranch.skillarticles.viewmodels

import androidx.lifecycle.LiveData
import ru.skillbranch.skillarticles.data.ArticleData
import ru.skillbranch.skillarticles.data.ArticlePersonalInfo
import ru.skillbranch.skillarticles.data.SearchInfo
import ru.skillbranch.skillarticles.data.repositories.ArticleRepository
import ru.skillbranch.skillarticles.extensions.data.toAppSettings
import ru.skillbranch.skillarticles.extensions.data.toArticlePersonalInfo
import ru.skillbranch.skillarticles.extensions.data.toArticleSearchInfo
import ru.skillbranch.skillarticles.extensions.format
import ru.skillbranch.skillarticles.viewmodels.interfaces.IArticleViewModel

class ArticleViewModel(private val articleId: String) :
    BaseViewModel<ArticleState>(ArticleState()), IArticleViewModel {

    private val repository = ArticleRepository

    init {
        subscribeOnDataSource(getArticleData()) { article, state ->
            article ?: return@subscribeOnDataSource null
            state.copy(
                shareLink = article.shareLink,
                title = article.title,
                category = article.category,
                categoryIcon = article.categoryIcon,
                date = article.date.format(),
                isSearch = false,
                searchPosition = 0,
                searchQuery = null
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

        subscribeOnDataSource(repository.getSearchInfo()){searchInfo, state->
            searchInfo ?: return@subscribeOnDataSource null
            state.copy(
                searchResult = searchInfo.searchResult,
                isSearch = searchInfo.isSearch,
                searchQuery = searchInfo.searchQuery,
                searchPosition = searchInfo.searchPosition
            )
        }

    }

    override fun getArticleContent(): LiveData<List<Any>?> {
        return repository.loadArticleContent(articleId)
    }

    override fun getArticleData(): LiveData<ArticleData?> {
        return repository.getArticle(articleId)
    }

    override fun getArticlePersonalInfo(): LiveData<ArticlePersonalInfo?> {
        return repository.loadArticlePersonalInfo(articleId)
    }

    override fun getArticleSearchInfo(): LiveData<SearchInfo?> {
        return repository.getSearchInfo()
    }

    override fun handleLike() {
        val toggleLike = {
            val info = currentState.toArticlePersonalInfo()
            repository.updateArticlePersonalInfo(info.copy(isLike = !info.isLike))
        }

        toggleLike()
        val msg = if (currentState.isLike) Notify.TextMessage("Mark is liked")
        else {
            Notify.ActionMessage(
                "Dont like it anymore",
                "No still like it",
                toggleLike
            )
        }

        notify(msg)
    }

    override fun handleBookmark() {
        val toggleBookmark = {
            val info = currentState.toArticlePersonalInfo()
            repository.updateArticlePersonalInfo(info.copy(isBookmark = !info.isBookmark))

        }

        toggleBookmark()
        val msg = if (currentState.isLike) Notify.TextMessage("Mark is saved")
        else {
            Notify.ActionMessage(
                "Article removed from saved",
                "Article add to saved",
                toggleBookmark
            )
        }

        notify(msg)
    }

    override fun handleNightMode() {
        val settings = currentState.toAppSettings()
        repository.updateSettings(settings.copy(isDarkMode = !settings.isDarkMode))
    }

    override fun handleShare() {
        val msg = "Share is not implemented"
        notify(Notify.ErrorMessage(msg, "OK", null))
    }


    override fun handleUpText() {
        repository.updateSettings(currentState.toAppSettings().copy(isBigText = true))
    }

    override fun handleDownText() {
        repository.updateSettings(currentState.toAppSettings().copy(isBigText = false))

    }

    override fun handleToggleMenu() {
        updateState { it.copy(isShowMenu = !it.isShowMenu) }
    }


    /**
     *
     * TODO:
     * Апдейтить поиск (нахождение вариантов в тексте)
     */
    override fun handleSearchMenu(query: String?, index: Int, length: Int) {
        val searchInfo = currentState.toArticleSearchInfo()
        repository.updateSearchInfo(searchInfo.copy(
            searchQuery = query,
            isSearch = !searchInfo.isSearch,
            searchPosition = index,
            searchResult = index to length
        ))

    }

}

data class ArticleState(
    val isAuth: Boolean = false, // пользователь авторизован
    val isLoadingContent: Boolean = true, // Контент загружается
    val isLoadingReviews: Boolean = true, // Отзывы загружаются
    val isLike: Boolean = false, // Помечено как Like
    val isBookmark: Boolean = false, //В закладках
    val isShowMenu: Boolean = false, // Отображается меню
    val isBigText: Boolean = false, // Шрифт увеличен
    val isDarkMode: Boolean = false, // Темный режим
    val isSearch: Boolean = false, // Режим поиска
    val searchQuery: String? = null, // Поисковый запрос
    val searchResult: List<Pair<Int, Int>> = emptyList(), // Результаты поиска
    val searchPosition: Int = 0, //Текущая позиция найденного результата
    val shareLink: String? = null, // ссылка Share
    val title: String? = null, // заголовок статьи
    val category: String? = null, //категория
    val categoryIcon: Any? = null, // Иконка категории
    val date: String? = null, // Дата публикации
    val author: String? = null, //Автор статьи
    val poster: String? = null, // Обложка статьи
    val content: List<Any> = emptyList(), // Контент
    val reviews: List<Any> = emptyList() // Комментарии
)

