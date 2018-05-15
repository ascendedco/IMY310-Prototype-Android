package co.ascended.waiterio.waiters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import co.ascended.waiterio.R
import co.ascended.waiterio.widget.ViewPagerAdapter
import co.ascended.waiterio.widget.tag
import kotlinx.android.synthetic.main.activity_order.*
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ClipPagerTitleView

class OrderActivity: AppCompatActivity() {

    private val titles = arrayOf("select table", "add items", "review order")
    private var search = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        val fragment1 = TablesFragment.start()
        val fragment2 = ItemsFragment.start()
        val fragment3 = ReviewFragment.start()

        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(fragment1, fragment1.tag())
        adapter.addFragment(fragment2, fragment2.tag())
        adapter.addFragment(fragment3, fragment3.tag())
        viewPager.adapter = adapter
        viewPager.canPage = true

        viewPager.addOnPageChangeListener(object: android.support.v4.view.ViewPager.OnPageChangeListener {

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                when (position) {
                    0 -> {
                        buttonAdd.alpha = positionOffset
                    }
                    1 -> {
                        buttonAdd.alpha = 1 - positionOffset
                    }
                }
            }

            override fun onPageSelected(position: Int) {
                buttonAdd.isEnabled = position == 1
                if (position == 2) buttonNext.text = "submit order"
                else buttonNext.text = "next"
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
                return 3
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

        buttonNext.setOnClickListener {
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

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, OrderActivity::class.java)
            context.startActivity(intent)
        }
    }
}
