package com.example.moodmonitoringapp.fragments

import android.os.Build
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import androidx.core.view.updatePadding
import com.example.moodmonitoringapp.util.extensions.updateMargin
import com.google.android.material.floatingactionbutton.FloatingActionButton

//import com.leinardi.android.speeddial.SpeedDialView

object InsetsHelper {
    fun handleBottom(isPadding: Boolean, vararg views: View) {
        views.forEach { view ->
            val distance = if (isPadding) view.paddingBottom else view.marginBottom
            ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
                if ((v is FloatingActionButton) && Build.VERSION_CODES.Q <= Build.VERSION.SDK_INT) {
                    val tappable = insets.tappableElementInsets
                    v.updateMargin(
                        bottom = tappable.bottom + v.marginBottom
                    )
                } else {
                    if (isPadding) {
                        v.updatePadding(bottom = distance + insets.systemWindowInsetBottom)
                    } else {
                        v.updateMargin(bottom = distance + insets.systemWindowInsetBottom)
                    }
                }

                insets
            }
        }
    }
}
