package ru.skillbranch.skillarticles.extensions.data

import ru.skillbranch.skillarticles.AppSettings
import ru.skillbranch.skillarticles.ArticlePersonalInfo
import ru.skillbranch.skillarticles.viewmodels.ArticleState

fun ArticleState.toAppSettings() : AppSettings {
    return AppSettings(isDarkMode,isBigText)
}

fun ArticleState.toArticlePersonalInfo(): ArticlePersonalInfo {
    return ArticlePersonalInfo(isLike, isBookmark)
}