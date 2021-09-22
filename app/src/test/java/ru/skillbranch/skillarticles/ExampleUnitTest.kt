package ru.skillbranch.skillarticles

import org.junit.Test

import org.junit.Assert.*
import ru.skillbranch.skillarticles.markdown.Element

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun parse_list_item() {

    }

    private fun Element.spread(): List<Element> {
        val elements = mutableListOf<Element>()
        elements.add(this)
        elements.addAll(this.elements.spread())
        return elements
    }

    private fun List<Element>.spread(): List<Element> {
        val elements = mutableListOf<Element>()

        if (this.isNotEmpty()) elements.addAll(
            this.fold(mutableListOf()) { acc, el -> acc.also { it.addAll(el.spread()) } }
        )


        return elements
    }

    private inline fun <reified T: Element> prepare(list: List<Element>): List<String>{
        return list
            .fold(mutableListOf<Element>()){ acc, el->
                acc.also{ it.addAll(el.spread())}
            }
            .filterIsInstance<T>()
            .map { it.text.toString() }
    }

}