package co.ascended.waiterio.managers

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import co.ascended.waiterio.R
import co.ascended.waiterio.widget.ViewPagerAdapter
import co.ascended.waiterio.widget.tag
import kotlinx.android.synthetic.main.activity_manager.*
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ClipPagerTitleView

class ManagerActivity: AppCompatActivity() {

    private val titles = arrayOf("active tables", "menu items")
    private var search = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager)

        val fragment1 = TablesFragment.start()
        val fragment2 = ItemsFragment.start()

        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(fragment1, fragment1.tag())
        adapter.addFragment(fragment2, fragment2.tag())
        viewPager.adapter = adapter
        viewPager.canPage = true

        viewPager.addOnPageChangeListener(object: android.support.v4.view.ViewPager.OnPageChangeListener {

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                when (position) {
                    0 -> {
                        buttonSearch.alpha = positionOffset
                    }
                    1 -> {
                        buttonSearch.alpha = 1 - positionOffset
                    }
                }
            }

            override fun onPageSelected(position: Int) {
                buttonSearch.isEnabled = position == 1
                if (position == 1) buttonAction.text = "add item"
                else buttonAction.text = "remove table"
            }

            override fun onPageScrollStateChanged(state: Int) { }
        })

        val navigator = CommonNavigator(this)
        navigator.adapter = object: CommonNavigatorAdapter() {

            override fun getTitleView(context: Context?, index: Int): IPagerTitleView {
                val titleView = ClipPagerTitleView(context!!)
                titleView.text = titles[index]
                titleView.textColor = ContextCompat.getColor(context, R.color.white)
                titleView.clipColor = ContextCompat.getColor(context, R.color.blueLight)
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
                return indicator
            }
        }

        magicIndicator.navigator = navigator
        ViewPagerHelper.bind(magicIndicator, viewPager)
        viewPager.clipToOutline = true

        buttonAction.setOnClickListener {
            // showCancelOrder()
            showOrderServed()
        }

        buttonSearch.isEnabled = false
        buttonSearch.setOnClickListener {
            if (!search) {
                percentRelativeLayoutSearch.visibility = VISIBLE
                percentRelativeLayoutResults.visibility = VISIBLE
                percentRelativeLayoutSearch.animate().withLayer().alpha(1f).duration = 400
                percentRelativeLayoutResults.animate().withLayer().alpha(1f).withEndAction {
                    search = true
                }.duration = 400
            }
        }
    }

    override fun onBackPressed() {
        if (search) {
            percentRelativeLayoutSearch.animate().withLayer().alpha(0f).duration = 300
            percentRelativeLayoutResults.animate().withLayer().alpha(0f).withEndAction {
                percentRelativeLayoutSearch.visibility = INVISIBLE
                percentRelativeLayoutResults.visibility = INVISIBLE
                search = false
            }.duration = 300
        }
        else super.onBackPressed()
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
        actionShadow.animate().withLayer().alpha(0f).withEndAction {
           // action = OrdersFragment.Action.Cancel()
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
        successShadow.animate().withLayer().alpha(1f).duration = 300
        actionShadow.animate().withLayer().alpha(0f).withEndAction {
            // action = OrdersFragment.Action.Served()
        }.duration = 300
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ManagerActivity::class.java)
            context.startActivity(intent)
        }
    }
}
