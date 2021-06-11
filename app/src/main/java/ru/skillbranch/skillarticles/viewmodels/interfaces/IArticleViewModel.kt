package ru.skillbranch.skillarticles.viewmodels.interfaces

import androidx.lifecycle.LiveData
import ru.skillbranch.skillarticles.data.ArticleData
import ru.skillbranch.skillarticles.data.ArticlePersonalInfo
import ru.skillbranch.skillarticles.data.SearchInfo

interface IArticleViewModel {

    fun getArticleContent(): LiveData<List<Any>?>

    fun getArticleData(): LiveData<ArticleData?>

    fun getArticlePersonalInfo(): LiveData<ArticlePersonalInfo?>

    fun getArticleSearchInfo(): LiveData<SearchInfo?>

    fun handleLike()

    fun handleBookmark()

    fun handleNightMode()

    fun handleShare()


    fun handleUpText()

    fun handleDownText()

    fun handleToggleMenu()

    fun handleSearchMenu(query: String?, resultList: List<Pair<Int, Int>>)

}