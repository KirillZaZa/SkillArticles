package ru.skillbranch.skillarticles.ui

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.xeoh.android.texthighlighter.TextHighlighter
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
    private lateinit var textHighlighter: TextHighlighter

    companion object{
        private var flag: Int = 0
    }

    /**
     *
     * TODO:
     * сохранение состояния открытого searchView
     *
     * Найти баги
     *
     * Посмотреть дальнешие задания на сайте
     */

    @SuppressLint("ShowToast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)
        setupToolbar()
        setupBottombar()
        setupSubmenu()

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
                if(flag == 1){
                    textHighlighter.resetBackgroundColor()
                    textHighlighter.resetForegroundColor()
                    textHighlighter.resetTargets()
                    flag = 2
                }else if(flag == 2){
                    finish()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.appbar_menu, menu)
        return true
    }


    fun openSearch(item: MenuItem?) {
        flag = 1

        searchView = item?.actionView as SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        searchView.queryHint = "Search"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            /***
             *
             * Нужно создать список Pair <Int,Int> - каждое совпадение в тексте записать
             * в этот список. Далее передать этот список в handleSearchMenu
             */

            override fun onQueryTextChange(newText: String?): Boolean {
                val list = getWordsFound(newText)
                viewModel.handleSearchMenu(newText, list)

                return true
            }

        })
    }

    private fun getWordsFound(userQuery: String?): List<Pair<Int, Int>> {
        val resultList = emptyList<Pair<Int,Int>>().toMutableList()
        if(userQuery == null){
            resultList.add(0, 0 to 0)
        }
        tv_text_content.text.forEachIndexed { i, str ->
            val index = userQuery?.let { tv_text_content.text.toString().indexOf(str) } ?: -1
            if (index != -1) {
                val line = tv_text_content.layout.getLineForOffset(index)
                resultList.add(i, index to line)
            }
        }

        return resultList
    }

    private fun renderSearch(data: ArticleState) {
        // render UI
        textHighlighter = TextHighlighter()
        textHighlighter
            .addTarget(tv_text_content)
            .setBackgroundColor(getColor(R.color.color_accent))
            .setForegroundColor(getColor(R.color.color_primary_dark))
            .highlight(data.searchQuery, TextHighlighter.CASE_INSENSITIVE_MATCHER)
    }

}