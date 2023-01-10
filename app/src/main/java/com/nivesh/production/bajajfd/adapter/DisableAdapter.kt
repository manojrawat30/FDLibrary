package com.nivesh.production.bajajfd.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager


class DisableAdapter (context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {
    private var isPagingEnabled = true // change this value for enable and disable the viewpager swipe

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return this.isPagingEnabled && super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        return this.isPagingEnabled && super.onInterceptTouchEvent(event)
    }

    fun setPagingEnabled(b: Boolean) { isPagingEnabled = b
    }
}
