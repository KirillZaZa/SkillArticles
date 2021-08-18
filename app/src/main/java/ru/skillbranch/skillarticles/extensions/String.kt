package ru.skillbranch.skillarticles.extensions


fun String.indexesOf(substr: String, ignoreCase: Boolean = true): List<Int> {
    val result = arrayListOf<Int>()
    var index: Int = this.indexOf(substr, 0, ignoreCase)
    while (index != -1) {
        result.add(index)
        index = this.indexOf(substr, index + 1, ignoreCase)
    }
    return result
}