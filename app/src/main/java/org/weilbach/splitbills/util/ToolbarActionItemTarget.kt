package org.weilbach.splitbills.util

import android.graphics.Point
import com.github.amlcurran.showcaseview.targets.ViewTarget
import com.github.amlcurran.showcaseview.targets.Target
import androidx.annotation.IdRes
import androidx.appcompat.widget.Toolbar

class ToolbarActionItemTarget(private val toolbar: Toolbar, @param:IdRes private val menuItemId: Int) : Target {

    override fun getPoint(): Point {
        return ViewTarget(toolbar.findViewById(menuItemId)).point
    }

}