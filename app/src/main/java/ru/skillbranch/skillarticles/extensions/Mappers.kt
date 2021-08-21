package ru.skillbranch.skillarticles.extensions

import ru.skillbranch.skillarticles.data.AppSettings
import ru.skillbranch.skillarticles.data.ArticlePersonalInfo
import ru.skillbranch.skillarticles.viewmodels.ArticleState
import ru.skillbranch.skillarticles.viewmodels.BottombarData
import ru.skillbranch.skillarticles.viewmodels.SubmenuData


fun ArticleState.toAppSettings() : AppSettings {
    return AppSettings(isDarkMode,isBigText)
}

fun ArticleState.toArticlePersonalInfo(): ArticlePersonalInfo {
    return ArticlePersonalInfo(isLike, isBookmark)
}


fun ArticleState.toBottombarData(): BottombarData {
    return BottombarData(
        isLike,
        isBookmark,
        isShowMenu,
        isSearch,
        searchResults.size,
        searchPosition
    )
}

fun ArticleState.toSubmenuData(): SubmenuData {
    return SubmenuData(
        isShowMenu,
        isBigText,
        isDarkMode
    )
}


fun ArticleState.asMap(): Map<String, Any?> = mapOf(
    "isAuth" to isAuth,
    "isLoadingContent" to isLoadingContent,
    "isLoadingReviews" to isLoadingReviews,
    "isLike" to isLike,
    "isBookmark" to isBookmark,
    "isShowMenu" to isShowMenu,
    "isBigText" to isBigText,
    "isDarkMode" to isDarkMode,
    "isSearch" to isSearch,
    "isSearch" to searchQuery,
    "searchResults" to searchResults,
    "searchPosition" to searchPosition,
    "shareLink" to shareLink,
    "title" to title,
    "category" to category,
    "categoryIcon" to categoryIcon,
    "date" to date,
    "author" to author,
    "poster" to poster,
    "content" to content,
    "reviews" to reviews,



)