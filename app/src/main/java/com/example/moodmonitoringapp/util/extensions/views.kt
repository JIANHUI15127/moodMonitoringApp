package com.example.moodmonitoringapp.util.extensions

import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop





fun View.updateMargin(left: Int? = null, right: Int? = null, top: Int? = null, bottom: Int? = null) {
    val params = layoutParams
    if (params is MarginLayoutParams) {
        params.setMargins(left ?: marginLeft, top ?: marginTop,
            right ?: marginRight, bottom ?: marginBottom)
        layoutParams = params
        requestLayout()
    }
}