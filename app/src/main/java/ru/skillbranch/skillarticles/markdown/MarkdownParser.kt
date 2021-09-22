package ru.skillbranch.skillarticles.markdown

import java.util.regex.Pattern

object MarkdownParser {

    private val LINE_SEPARATOR = System.getProperty("line.separator") ?: "\n"

    private const val UNORDERED_LIST_ITEM_GROUP = ""

    const val MARKDOWN_GROUPS = "$UNORDERED_LIST_ITEM_GROUP"

    private val elementsPattern by lazy{Pattern.compile(MARKDOWN_GROUPS, Pattern.MULTILINE)}

    fun parse(string: String): MarkdownText{
        val elements = mutableListOf<Element>()
        elements.addAll(findElements(string))
        return MarkdownText(elements)
    }

    fun clear(string: String): String?{
        return null
    }

    private fun findElements(string: CharSequence): List<Element>{
        val parents = mutableListOf<Element>()
        val matcher = elementsPattern.matcher(string)
        var lastStartIndex = 0

        return parents
    }


}

data class MarkdownText(val elements: List<Element>)

sealed class Element(){

    abstract val text: CharSequence
    abstract val elements: List<Element>

    data class Text(
        override val text: CharSequence,
        override val elements: List<Element>
    ): Element()


    data class UnorderedListItem(
        override val text: CharSequence,
        override val elements: List<Element>
    ): Element()

}