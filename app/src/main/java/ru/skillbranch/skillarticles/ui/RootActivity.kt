package ru.skillbranch.skillarticles.ui

import android.app.SearchManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.text.clearSpans
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_root.*
import kotlinx.android.synthetic.main.layout_bottombar.*
import kotlinx.android.synthetic.main.layout_submenu.*
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.viewmodels.*
import java.util.*

class RootActivity : AppCompatActivity() {

    private lateinit var viewModel: ArticleViewModel
    private lateinit var searchView: SearchView
    private var isSearch = false
    private var searchQuery = ""

    companion object {
        private var flag: Int = 1
    }

    /**
     *
     * TODO:
     * сохранение состояния открытого searchView
     *
     * Посмотреть дальнешие задания на сайте
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)

        setupToolbar()
        setupBottombar()
        setupSubmenu()
        Log.d("ONCREATE", "call")

        viewModel = ViewModelProvider(this, BaseViewModel.ViewModelFactory("0"))
            .get(ArticleViewModel::class.java)



        viewModel.observeState(this) {
            renderUi(it)
        }

        viewModel.observerNotifications(this) {
            renderNotifications(it)
        }


    }

    private fun renderNotifications(notify: Notify) {
        val snackbar = Snackbar.make(coordinator_container, notify.message, Snackbar.LENGTH_LONG)
            .setAnchorView(bottombar)
            .setActionTextColor(getColor(R.color.color_accent_dark))

        when (notify) {
            is Notify.TextMessage -> {
            }

            is Notify.ActionMessage -> {
                snackbar.setAction(notify.actionLabel) {
                    notify.actionHandler?.invoke()
                }
            }

            is Notify.ErrorMessage -> {
                with(snackbar) {
                    setBackgroundTint(getColor(R.color.design_default_color_error))
                    setActionTextColor(getColor(android.R.color.white))
                    setAction(notify.errLabel) {
                        notify.errHandler?.invoke()
                    }
                }
            }
        }

        snackbar.show()
    }


    private fun setupSubmenu() {
        btn_text_up.setOnClickListener { viewModel.handleUpText() }
        btn_text_down.setOnClickListener { viewModel.handleDownText() }
        switch_mode.setOnClickListener { viewModel.handleNightMode() }
    }

    private fun setupBottombar() {
        btn_like.setOnClickListener { viewModel.handleLike() }
        btn_bookmark.setOnClickListener { viewModel.handleBookmark() }
        btn_share.setOnClickListener { viewModel.handleShare() }
        btn_settings.setOnClickListener { viewModel.handleToggleMenu() }
    }

    private fun renderUi(data: ArticleState) {
        isSearch = data.isSearch
        searchQuery = data.searchQuery ?: ""

        btn_settings.isChecked = data.isShowMenu
        if (data.isShowMenu) submenu.open() else submenu.close()

        btn_like.isChecked = data.isLike
        btn_bookmark.isChecked = data.isBookmark

        switch_mode.isChecked = data.isDarkMode
        delegate.localNightMode =
            if (data.isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO

        if (data.isBigText) {
            tv_text_content.textSize = 18f
            btn_text_up.isChecked = true
            btn_text_down.isChecked = false
        } else {
            tv_text_content.textSize = 14f
            btn_text_up.isChecked = false
            btn_text_down.isChecked = true
        }

        tv_text_content.text =
            if (data.isLoadingContent) "loading" else data.content.first() as String

        toolbar.title = data.title ?: "loading"
        toolbar.subtitle = data.category ?: "loading"
        if (data.categoryIcon != null) toolbar.logo = getDrawable(data.categoryIcon as Int)

        renderSearch(data)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val logo = if (toolbar.childCount > 2) toolbar.getChildAt(2) as ImageView else null
        logo?.scaleType = ImageView.ScaleType.CENTER_CROP
        val lp = logo?.layoutParams as? Toolbar.LayoutParams
        lp?.let {
            it.width = this.dpToIntPx(40)
            it.height = this.dpToIntPx(40)
            it.marginEnd = this.dpToIntPx(16)
            logo.layoutParams = it
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                Log.d("Item Selected", "${item.itemId}")
                flag = 2
                if (flag == 2) {
                    finish()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.appbar_menu, menu)

        val item = menu!!.findItem(R.id.action_search)
        if (isSearch) {
            item.expandActionView()
        }
        searchView = item?.actionView as SearchView

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = "Search"
        searchView.setQuery(searchQuery, false)
        searchView.setOnSearchClickListener {
            viewModel.handleSearchMenu("", emptyList())
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d("OnChange", "onQueryTextChange: $newText")
                val list = getWordsFound(newText)
                isSearch = true
                viewModel.handleSearchMenu(newText, list)
                return true
            }

        })

        return true
    }


    private fun getWordsFound(userQuery: String?): List<Pair<Int, Int>> {
        val resultList = ArrayList<Pair<Int, Int>>() // first index, last index
        if (!userQuery.isNullOrEmpty()) {
            val originText = tv_text_content.text.toString().toLowerCase()
            val input = userQuery.toLowerCase()
            var coincidence = originText.indexOf(input, 0)
            var i = 0
            while (i < originText.length && coincidence != -1) {
                coincidence = originText.indexOf(input, i)
                if (coincidence == -1) {
                    break
                } else {
                    Log.d("GetWordsFound", "first: $i || second: ${i + input.length}")
                    resultList.add(coincidence to coincidence + input.length)
                    i++
                }
            }
        }

        return resultList
    }

    private fun renderSearch(data: ArticleState) {
        val searchQuery = data.searchQuery
        val spannable = SpannableString(tv_text_content.text)
        if (searchQuery.isNullOrEmpty()) {
            spannable.clearSpans()
        } else {
            data.searchResult.forEach { list ->
                spannable.setSpan(
                    BackgroundColorSpan(getColor(R.color.color_accent)),
                    list.first,
                    list.second,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                Log.d("RENDERSEARCH", "$list")
                tv_text_content.setText(spannable, TextView.BufferType.SPANNABLE)
            }
        }

    }

}