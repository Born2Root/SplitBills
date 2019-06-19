package org.weilbach.splitbills.intro

import androidx.viewpager.widget.ViewPager
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import org.weilbach.splitbills.R


class IntroActivity : AppCompatActivity() {

    private var mViewPager: ViewPager? = null

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.intro_layout)

        mViewPager = findViewById(R.id.viewpager)

        // Set an Adapter on the ViewPager
        mViewPager!!.adapter = IntroAdapter(getSupportFragmentManager())

        // Set a PageTransformer
        mViewPager!!.setPageTransformer(false, IntroPageTransformer())
    }

}