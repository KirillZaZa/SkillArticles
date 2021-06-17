package ru.skillbranch.skillarticles.viewmodels

import androidx.lifecycle.LiveData
import ru.skillbranch.skillarticles.data.ArticleData
import ru.skillbranch.skillarticles.data.ArticlePersonalInfo

interface IArticleViewModel {

    fun getArticleContent(): LiveData<List<Any>?>

    fun getArticleData(): LiveData<ArticleData?>

    fun getArticlePersonalInfo(): LiveData<ArticlePersonalInfo?>

    fun handleLike()

    fun handleBookmark()

    fun handleNightMode()

    fun handleShare()


    fun handleUpText()

    fun handleDownText()

    fun handleToggleMenu()

    fun handleSearchMenu(query: String?, resultList: List<Pair<Int, Int>>)

}