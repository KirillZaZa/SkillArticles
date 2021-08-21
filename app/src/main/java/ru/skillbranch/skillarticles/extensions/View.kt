package ru.skillbranch.skillarticles.extensions

import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop


fun View.setMarginOptionally(
    left: Int = marginLeft,
    top: Int = marginTop,
    right: Int = marginRight,
    bottom: Int = marginBottom,
) {
    val params = layoutParams
    val lp = params as CoordinatorLayout.LayoutParams
    lp.setMargins(left, top, right, bottom)
    this.layoutParams = lp
    this.requestLayout()
}