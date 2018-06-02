package co.ascended.waiterio.waiters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.Snackbar.LENGTH_LONG
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
import co.ascended.waiterio.entity.Order
import co.ascended.waiterio.widget.ViewPagerAdapter
import co.ascended.waiterio.widget.tag
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_order.*
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ClipPagerTitleView
import java.util.concurrent.TimeUnit

class OrderActivity: AppCompatActivity() {

    private val titles = arrayOf("select table", "add items", "review order")
    private lateinit var itemSearchAdapter: ItemSearchAdapter
    private lateinit var disposables: CompositeDisposable
    private var search = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        val tablesFragment = TablesFragment.start(intent.getIntExtra("table", 2))
        val itemsFragment = ItemsFragment.start()
        val reviewFragment = ReviewFragment.start()

        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(tablesFragment, tablesFragment.tag())
        adapter.addFragment(itemsFragment, itemsFragment.tag())
        adapter.addFragment(reviewFragment, reviewFragment.tag())
        viewPager.offscreenPageLimit = 3
        viewPager.adapter = adapter
        viewPager.canPage = false

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
                // titleView.setOnClickListener { viewPager.setCurrentItem(index, true) }
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
            when (nextItem) {
                1 -> viewPager.setCurrentItem(nextItem, true)
                2 -> {
                    val items = itemsFragment.getItems()
                    if (items.isNotEmpty()) {
                        // Add items to review fragment.
                        reviewFragment.setItems(items)
                        viewPager.setCurrentItem(nextItem, true)
                    }
                    else {
                        Snackbar.make(root, "Please add items to continue.", LENGTH_LONG).show()
                    }
                }
                3 -> {
                    val table = tablesFragment.getTable()
                    val items = itemsFragment.getItems()
                    val total = items.sumByDouble { item -> item.price * item.quantity }
                    val order = Order(-1, table, items, total, "Pending")
                    Database.addOrder(order)
                    finish()
                }
            }
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

        // ======================= ITEM SEARCH ======================= //
        val height = (resources.displayMetrics.heightPixels * 0.57f) * 0.2f
        itemSearchAdapter = ItemSearchAdapter(height, { item ->
            // Add the selected item to the items/review fragments
            val copy = item.copy(quantity = 1)
            itemsFragment.addItem(copy)
            onBackPressed()
        })

        recyclerViewResults.layoutManager = LinearLayoutManager(this)
        recyclerViewResults.overScrollMode = View.OVER_SCROLL_NEVER
        recyclerViewResults.adapter = itemSearchAdapter

        disposables = CompositeDisposable()
        // Database.items

        val items = Database.items.map { items -> items.sortedBy { item -> item.name } }
        val filter = RxTextView.textChanges(editTextSearch)
            .map { chars -> chars.toString() }
            // .debounce(200, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()

        // RxTextView.textChanges(editTextSearch).subscribe { chars -> Log.e(tag(), "Query: ${chars.toString()}") }

        Observable.combineLatest(filter, items, BiFunction<String, List<Item>, List<Item>> { query, items ->
                // Log.e(tag(), "Query updated, isNotEmpty ${query.isNotEmpty()} $query")
                when (query.isNotEmpty()) {
                    true -> items.filter { item -> item.name.toLowerCase().contains(query.toLowerCase()) }
                    else -> items
                }
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { items ->
                itemSearchAdapter.updateData(items)
            }.addTo(disposables)
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
        else if (viewPager.currentItem > 0) {
            viewPager.setCurrentItem(viewPager.currentItem - 1, true)
        }
        else super.onBackPressed()
    }

    companion object {
        fun start(context: Context, table: Int) {
            val intent = Intent(context, OrderActivity::class.java)
            intent.putExtra("table", table)
            context.startActivity(intent)
        }
    }
}
