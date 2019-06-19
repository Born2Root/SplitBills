package org.weilbach.splitbills.util

import android.graphics.Point
import androidx.annotation.IdRes
import androidx.appcompat.widget.Toolbar
import com.github.amlcurran.showcaseview.targets.Target
import com.github.amlcurran.showcaseview.targets.ViewTarget

class ToolbarActionItemTarget(private val toolbar: Toolbar, @param:IdRes private val menuItemId: Int) : Target {

    override fun getPoint(): Point {
        return ViewTarget(toolbar.findViewById(menuItemId)).point
    }

}