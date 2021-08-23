package ru.skillbranch.skillarticles.extensions


fun String?.indexesOf(substr: String?, ignoreCase: Boolean = true): List<Int> {
    if(substr.isNullOrBlank()|| this.isNullOrBlank()) return emptyList()
    val result = arrayListOf<Int>()
    var i = -1
    while (this.indexOf(substr, i + 1, ignoreCase).also { i = it } != -1) {
        result.add(i)
        i++
    }
    return result
}