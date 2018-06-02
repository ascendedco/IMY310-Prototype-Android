package co.ascended.waiterio.managers

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import co.ascended.waiterio.R
import co.ascended.waiterio.database.Database
import co.ascended.waiterio.entity.Item
import co.ascended.waiterio.entity.Table
import co.ascended.waiterio.waiters.OrdersFragment
import co.ascended.waiterio.widget.ViewPagerAdapter
import co.ascended.waiterio.widget.tag
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_manager.*
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ClipPagerTitleView
import kotlin.math.log

class ManagerActivity: AppCompatActivity() {

    private val titles = arrayOf("active tables", "menu items")
    private lateinit var tablesAdapter: TablesInactiveAdapter
    private lateinit var disposables: CompositeDisposable
    private lateinit var adapterOrders: ViewPagerAdapter
    private lateinit var table: Table
    private lateinit var item: Item
    private var state = "normal"
    private var orders = false
    private var search = false
    private var tables = false

    private lateinit var itemFragment: ItemFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager)

        adapterOrders = ViewPagerAdapter(supportFragmentManager)
        itemFragment = ItemFragment.start()

        val fragment1 = TablesFragment.start()
        val fragment2 = ItemsFragment.start()

        fragment1.listener = { table -> showOrders(table) }
        fragment2.listener = { item, delete ->
            this.item = item
            if (delete) {
                buttonCancel.text = "remove item"
                showCancelOrder("remove item")
            }
            else {
                buttonServed.text = "update item"
                showItemForm("edit item", this.item)
            }
        }

        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(fragment1, fragment1.tag())
        adapter.addFragment(fragment2, fragment2.tag())
        viewPager.adapter = adapter
        viewPager.canPage = true

        buttonSearch.visibility = INVISIBLE
        viewPager.addOnPageChangeListener(object: android.support.v4.view.ViewPager.OnPageChangeListener {

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                /*when (position) {
                    0 -> {
                        buttonSearch.alpha = positionOffset
                    }
                    1 -> {
                        buttonSearch.alpha = 1 - positionOffset
                    }
                }*/
            }

            override fun onPageSelected(position: Int) {
                // buttonSearch.isEnabled = position == 1
                if (position == 1) buttonAction.text = "add item"
                else buttonAction.text = "add table"
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
            when (viewPager.currentItem) {
                0 -> {
                    if (orders) {
                        buttonCancel.text = "remove table"
                        showCancelOrder("remove table")
                    }
                    else {
                        percentRelativeLayoutInactive.visibility = VISIBLE
                        percentRelativeLayoutInactive.animate().withLayer().alpha(1f).withEndAction {
                            tables = true
                        }.duration = 400
                    }
                }
                1 -> {
                    buttonServed.text = "add item"
                    showItemForm("add item", null)
                }
            }
            // showCancelOrder()
            // showOrderServed()
        }

        buttonCancel.setOnClickListener {
            if (state == "remove table") {
                table.active = false
                Database.updateTable(table)
                cancelBack { onBackPressed() }
            }
            if (state == "remove item") {
                Database.removeItem(item)
                fragment2.removeItem(item)
                cancelBack { }
            }
        }

        buttonServed.setOnClickListener {
            itemFragment.addItem { item ->
                if (state == "add item") {
                    Database.addItem(item)
                    onBackPressed()
                }
                if (state == "edit item") {
                    Log.e(tag(), "updating item")
                    Database.updateItem(item)
                    onBackPressed()
                }
            }
        }

        /*buttonSearch.isEnabled = false
        buttonSearch.setOnClickListener {
            if (!search) {
                percentRelativeLayoutSearch.visibility = VISIBLE
                percentRelativeLayoutResults.visibility = VISIBLE
                percentRelativeLayoutSearch.animate().withLayer().alpha(1f).duration = 400
                percentRelativeLayoutResults.animate().withLayer().alpha(1f).withEndAction {
                    search = true
                }.duration = 400
            }
        }*/

        val height = (resources.displayMetrics.heightPixels * 0.57f) * 0.2f
        tablesAdapter = TablesInactiveAdapter(height, { table ->
            table.active = true
            Database.updateTable(table)
            onBackPressed()
        })

        recyclerViewTables.layoutManager = LinearLayoutManager(this)
        recyclerViewTables.overScrollMode = View.OVER_SCROLL_NEVER
        recyclerViewTables.adapter = tablesAdapter

        disposables = CompositeDisposable()

        Database.tables.map { tables -> tables.filter { table -> !table.active } }
            .subscribe { tables -> tablesAdapter.updateData(tables) }
            .addTo(disposables)
    }

    override fun onBackPressed() {
        if (state == "remove table" || state == "remove item") {
            cancelBack { }
        }
        else if (state == "add item") {
            hideOrders(false)
            itemFragment.clear()
            servedBack(true)
        }
        else if (state == "edit item") {
            hideOrders(false)
            itemFragment.clear()
            servedBack(false)
        }
        else if (search) {
            percentRelativeLayoutSearch.animate().withLayer().alpha(0f).duration = 300
            percentRelativeLayoutResults.animate().withLayer().alpha(0f).withEndAction {
                percentRelativeLayoutSearch.visibility = INVISIBLE
                percentRelativeLayoutResults.visibility = INVISIBLE
                search = false
            }.duration = 300
        }
        else if (tables) {
            percentRelativeLayoutInactive.animate().withLayer().alpha(0f).withEndAction {
                percentRelativeLayoutInactive.visibility = INVISIBLE
                tables = false
            }.duration = 300
        }
        else if (orders) {
            hideOrders(true)
        }
        else super.onBackPressed()
    }

    private fun showOrders(table: Table) {
        this.table = table
        val ordersFragment = OrdersFragment.start(table, true)
        adapterOrders.addFragment(ordersFragment, ordersFragment.tag())
        viewPagerOrders.clipToOutline = true
        viewPagerOrders.adapter = adapterOrders
        viewPagerOrders.canPage = false
        viewPagerOrders.visibility = VISIBLE
        buttonAction.text = "remove table"
        textViewIndicator.text = "Table ${table.number} - Orders"
        textViewIndicator.animate().withLayer().alpha(1.0f).duration = 400
        viewPagerOrders.animate().withLayer().alpha(1.0f).duration = 400
        magicIndicator.animate().withLayer().alpha(0.0f).duration = 400
        viewPager.animate().withLayer().alpha(0.0f).withEndAction {
            magicIndicator.visibility = INVISIBLE
            viewPager.visibility = INVISIBLE
            orders = true
        }.duration = 400
    }

    private fun hideOrders(changeText: Boolean) {
        textViewIndicator.animate().withLayer().alpha(0.0f).duration = 300
        viewPagerOrders.animate().withLayer().alpha(0.0f).duration = 300
        viewPager.visibility = VISIBLE
        magicIndicator.visibility = VISIBLE
        if (changeText) buttonAction.text = "add table"
        magicIndicator.animate().withLayer().alpha(1.0f).duration = 300
        viewPager.animate().withLayer().alpha(1.0f).withEndAction {
            viewPagerOrders.visibility = INVISIBLE
            adapterOrders.clear()
            orders = false
        }.duration = 300
    }

    private fun showItemForm(state: String, item: Item?) {
        if (item != null) {
            itemFragment.setItem(item)
        }
        // showOrderServed()
        /*itemFragment = if (item == null) ItemFragment.start()
        else ItemFragment.start(item)*/
        adapterOrders.addFragment(itemFragment, itemFragment.tag())
        viewPagerOrders.clipToOutline = true
        viewPagerOrders.adapter = adapterOrders
        viewPagerOrders.canPage = false
        viewPagerOrders.visibility = VISIBLE
        textViewIndicator.text = if (item == null) "Add Item" else "Edit Item"
        textViewIndicator.animate().withLayer().alpha(1.0f).duration = 400
        viewPagerOrders.animate().withLayer().alpha(1.0f).duration = 400
        magicIndicator.animate().withLayer().alpha(0.0f).duration = 400
        viewPager.animate().withLayer().alpha(0.0f).withEndAction {
            magicIndicator.visibility = INVISIBLE
            viewPager.visibility = INVISIBLE
        }.duration = 400
        showOrderServed(state)
    }

    private fun showCancelOrder(state: String) {
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
            this.state = state
           // action = OrdersFragment.Action.Cancel()
        }.duration = 300
    }

    private fun showOrderServed(state: String) {
        if (state == "add item") {
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
        }

        buttonServed.visibility = VISIBLE
        buttonServed.animate().withLayer().alpha(1f).duration = 300
        successShadow.animate().withLayer().alpha(1f).duration = 300
        actionShadow.animate().withLayer().alpha(0f).withEndAction {
            this.state = state
        }.duration = 300
    }

    private fun cancelBack(completion: () -> Unit) {
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
        actionShadow.animate().withLayer().alpha(1f).withEndAction {
            buttonCancel.visibility = INVISIBLE
            state = "normal"
            completion()
        }.duration = 300
    }


    private fun servedBack(gradient: Boolean) {
        if (gradient) {
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
        }

        buttonServed.animate().withLayer().alpha(0f).duration = 300
        successShadow.animate().withLayer().alpha(0f).duration = 300
        actionShadow.animate().withLayer().alpha(1f).withEndAction {
            buttonServed.visibility = INVISIBLE
            this.state = "normal"
        }.duration = 300
    }
    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ManagerActivity::class.java)
            context.startActivity(intent)
        }
    }
}
