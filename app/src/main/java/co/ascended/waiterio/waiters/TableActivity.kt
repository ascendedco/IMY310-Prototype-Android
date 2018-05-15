package co.ascended.waiterio.waiters

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import co.ascended.waiterio.R
import co.ascended.waiterio.TestFragment
import co.ascended.waiterio.widget.ViewPagerAdapter
import co.ascended.waiterio.widget.tag
import kotlinx.android.synthetic.main.activity_tables.*
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView
import android.view.animation.DecelerateInterpolator
import android.view.animation.AccelerateInterpolator

class TableActivity: AppCompatActivity() {

    private val titles = arrayOf("table 1", "table 2", "table 3", "table 4", "table 5", "table 6")
    private var action: OrdersFragment.Action? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tables)

        val fragment1 = OrdersFragment.start()
        val fragment2 = OrdersFragment.start()
        val fragment3 = OrdersFragment.start()
        val fragment4 = OrdersFragment.start()
        val fragment5 = OrdersFragment.start()
        val fragment6 = OrdersFragment.start()

        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(fragment1, fragment1.tag())
        adapter.addFragment(fragment2, fragment2.tag())
        adapter.addFragment(fragment3, fragment3.tag())
        adapter.addFragment(fragment4, fragment4.tag())
        adapter.addFragment(fragment5, fragment5.tag())
        adapter.addFragment(fragment6, fragment6.tag())
        viewPager.adapter = adapter
        viewPager.canPage = true

        val navigator = CommonNavigator(this)
        navigator.scrollPivotX = 0.65f
        navigator.adapter = object: CommonNavigatorAdapter() {

            override fun getTitleView(context: Context?, index: Int): IPagerTitleView {
                val titleView = ColorTransitionPagerTitleView(context!!)
                titleView.text = titles[index]
                titleView.normalColor = ContextCompat.getColor(context, R.color.white)
                titleView.selectedColor = ContextCompat.getColor(context, R.color.blueLight)
                titleView.setOnClickListener { viewPager.setCurrentItem(index, true) }
                return titleView
            }

            override fun getCount(): Int {
                return titles.size
            }

            override fun getIndicator(context: Context?): IPagerIndicator {
                val indicator = LinePagerIndicator(context!!)
                val lineHeight = (resources.displayMetrics.heightPixels * 0.07f) * 0.75f
                indicator.lineHeight = lineHeight
                indicator.roundRadius = lineHeight / 2
                indicator.setColors(ContextCompat.getColor(context, R.color.white))
                indicator.startInterpolator = AccelerateInterpolator()
                indicator.endInterpolator = DecelerateInterpolator(2.0f)
                return indicator
            }
        }

        magicIndicator.navigator = navigator
        ViewPagerHelper.bind(magicIndicator, viewPager)
        viewPager.clipToOutline = true

        buttonNew.setOnClickListener { OrderActivity.start(this) }

        fragment3.listener = { action ->
            when (action) {
                is OrdersFragment.Action.Cancel -> showCancelOrder()
                is OrdersFragment.Action.Served -> showOrderServed()
            }
        }

        /*buttonNext.setOnClickListener {
            val nextItem = viewPager.currentItem + 1
            if (nextItem <= 3) viewPager.setCurrentItem(nextItem, true)
        }

        buttonAdd.isEnabled = false
        buttonAdd.setOnClickListener {
            if (!search) {
                percentRelativeLayoutSearch.visibility = VISIBLE
                percentRelativeLayoutResults.visibility = VISIBLE
                percentRelativeLayoutSearch.animate().withLayer().alpha(1f).duration = 400
                percentRelativeLayoutResults.animate().withLayer().alpha(1f).withEndAction {
                    search = true
                }.duration = 400
            }
        }*/
    }

    override fun onBackPressed() {
        when (action) {
            is OrdersFragment.Action.Cancel -> cancelBack()
            is OrdersFragment.Action.Served -> servedBack()
            else -> super.onBackPressed()
        }
    }

    private fun showCancelOrder() {
        val blueDark = ContextCompat.getColor(this, R.color.blueDark)
        val blueLight = ContextCompat.getColor(this, R.color.blueLight)
        val redDark = ContextCompat.getColor(this, R.color.redDark)
        val redLight = ContextCompat.getColor(this, R.color.redLight)

        var colorBottom: Int
        var colorTop: Int

        val drawable = imageViewHeader.background as GradientDrawable
        val evaluator = ArgbEvaluator()

        val animation = ValueAnimator.ofFloat(0f, 1f)
        animation.duration = 300
        animation.addUpdateListener({ a ->
            colorTop = evaluator.evaluate(a.animatedValue as Float, blueLight, redLight) as Int
            colorBottom = evaluator.evaluate(a.animatedValue as Float, blueDark, redDark) as Int
            drawable.colors = intArrayOf(colorBottom, colorTop)
        })
        animation.start()

        buttonCancel.visibility = VISIBLE
        buttonCancel.animate().withLayer().alpha(1f).duration = 300
        cancelShadow.animate().withLayer().alpha(1f).duration = 300
        nextShadow.animate().withLayer().alpha(0f).withEndAction {
            action = OrdersFragment.Action.Cancel()
        }.duration = 300
    }

    private fun showOrderServed() {
        val blueDark = ContextCompat.getColor(this, R.color.blueDark)
        val blueLight = ContextCompat.getColor(this, R.color.blueLight)
        val greenDark = ContextCompat.getColor(this, R.color.greenDark)
        val greenLight = ContextCompat.getColor(this, R.color.greenLight)

        var colorBottom: Int
        var colorTop: Int

        val drawable = imageViewHeader.background as GradientDrawable
        val evaluator = ArgbEvaluator()

        val animation = ValueAnimator.ofFloat(0f, 1f)
        animation.duration = 300
        animation.addUpdateListener({ a ->
            colorTop = evaluator.evaluate(a.animatedValue as Float, blueLight, greenLight) as Int
            colorBottom = evaluator.evaluate(a.animatedValue as Float, blueDark, greenDark) as Int
            drawable.colors = intArrayOf(colorBottom, colorTop)
        })
        animation.start()

        buttonServed.visibility = VISIBLE
        buttonServed.animate().withLayer().alpha(1f).duration = 300
        servedShadow.animate().withLayer().alpha(1f).duration = 300
        nextShadow.animate().withLayer().alpha(0f).withEndAction {
            action = OrdersFragment.Action.Served()
        }.duration = 300
    }

    private fun cancelBack() {
        val blueDark = ContextCompat.getColor(this, R.color.blueDark)
        val blueLight = ContextCompat.getColor(this, R.color.blueLight)
        val redDark = ContextCompat.getColor(this, R.color.redDark)
        val redLight = ContextCompat.getColor(this, R.color.redLight)

        var colorBottom: Int
        var colorTop: Int

        val drawable = imageViewHeader.background as GradientDrawable
        val evaluator = ArgbEvaluator()

        val animation = ValueAnimator.ofFloat(0f, 1f)
        animation.duration = 300
        animation.addUpdateListener({ a ->
            colorTop = evaluator.evaluate(a.animatedValue as Float, redLight, blueLight) as Int
            colorBottom = evaluator.evaluate(a.animatedValue as Float, redDark, blueDark) as Int
            drawable.colors = intArrayOf(colorBottom, colorTop)
        })
        animation.start()

        buttonCancel.animate().withLayer().alpha(0f).duration = 300
        cancelShadow.animate().withLayer().alpha(0f).duration = 300
        nextShadow.animate().withLayer().alpha(1f).withEndAction {
            buttonCancel.visibility = INVISIBLE
        }.duration = 300
    }

    private fun servedBack() {
        val blueDark = ContextCompat.getColor(this, R.color.blueDark)
        val blueLight = ContextCompat.getColor(this, R.color.blueLight)
        val greenDark = ContextCompat.getColor(this, R.color.greenDark)
        val greenLight = ContextCompat.getColor(this, R.color.greenLight)

        var colorBottom: Int
        var colorTop: Int

        val drawable = imageViewHeader.background as GradientDrawable
        val evaluator = ArgbEvaluator()

        val animation = ValueAnimator.ofFloat(0f, 1f)
        animation.duration = 300
        animation.addUpdateListener({ a ->
            colorTop = evaluator.evaluate(a.animatedValue as Float, greenLight, blueLight) as Int
            colorBottom = evaluator.evaluate(a.animatedValue as Float, greenDark, blueDark) as Int
            drawable.colors = intArrayOf(colorBottom, colorTop)
        })
        animation.start()

        buttonServed.animate().withLayer().alpha(0f).duration = 300
        servedShadow.animate().withLayer().alpha(0f).duration = 300
        nextShadow.animate().withLayer().alpha(1f).withEndAction {
            buttonServed.visibility = INVISIBLE
        }.duration = 300
    }
}
