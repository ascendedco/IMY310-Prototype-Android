package co.ascended.waiterio.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Scroller

/**
 * A ViewPager that can be set to disregard touch input, allowing for
 * programmatic paging only. The ViewPager's scrolling duration can
 * also be modified at runtime.
 */
class ViewPager
/**
 * Standard View entry point.
 *
 * @param context Context the View was inflated in.
 * *
 * @param attrs XML attributes of the view.
 */
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null) : android.support.v4.view.ViewPager(context, attrs) {

    /**
     * Flag indicating whether the ViewPager can be paged via touch.
     */
    var canPage = false

    /**
     * SmoothScroller controlling the ViewPager's animation.
     */
    private var smoothScroller: SmoothScroller? = null

    init {
        setScroller()
    }

    /**
     * Overridden event that manages touch interceptions. This method
     * will only delegate to super if canPage is true and will modify
     * the scroll duration of the ViewPager accordingly.

     * @param event Motion event passed to the ViewPager.
     * *
     * @return Whether or not touch should be intercepted.
     */
    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (canPage) smoothScroller!!.scrollDuration = 250
        return canPage && super.onInterceptTouchEvent(event)
    }

    /**
     * Overridden event that manages touch. This method will only
     * delegate to super if canPage is true and will modify the
     * scroll duration of the ViewPager accordingly.

     * @param event Motion event passed to the ViewPager.
     * *
     * @return Whether or not touch is accepted.
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (canPage) smoothScroller!!.scrollDuration = 250
        return canPage && super.onTouchEvent(event)
    }

    /**
     * Method used to set the ViewPager's scroller to our custom
     * version. This method uses some reflection magic.
     */
    private fun setScroller() {
        smoothScroller = SmoothScroller(context)
        smoothScroller!!.scrollDuration = 250
        try {
            val viewpager = android.support.v4.view.ViewPager::class.java
            val scroller = viewpager.getDeclaredField("mScroller")
            scroller.isAccessible = true
            scroller.set(this, smoothScroller)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * A custom scroller that allows for changes to scroll duration
     * at runtime.
     */
    private inner class SmoothScroller
    /**
     * SmoothScroller constructor.

     * @param context Context the object is created in.
     */
    internal constructor(context: Context) : Scroller(context, AccelerateDecelerateInterpolator()) {

        /**
         * Scroll duration in milliseconds.
         */
        var scrollDuration: Int = 0

        /**
         * Overridden method that uses scrollDuration to scroll.
         */
        override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
            super.startScroll(startX, startY, dx, dy, scrollDuration)
        }
    }

    /**
     * Method that sets the scroll duration of the ViewPager dynamically.

     * @param duration Duration a scroll should take to complete.
     */
    fun setScrollDuration(duration: Int) {
        smoothScroller!!.scrollDuration = duration
    }
}