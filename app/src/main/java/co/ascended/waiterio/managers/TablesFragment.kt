package co.ascended.waiterio.managers

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.ascended.waiterio.R
import co.ascended.waiterio.database.Database
import co.ascended.waiterio.entity.Order
import co.ascended.waiterio.entity.Table
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.combineLatest
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_tables_m.*

class TablesFragment: Fragment() {

    class TableTotal(val table: Table, val orders: List<Order>, val total: Double)

    private lateinit var disposables: CompositeDisposable
    private lateinit var adapter: TablesAdapter
    lateinit var listener: (Table) -> Unit

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tables_m, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val height = (resources.displayMetrics.heightPixels * 0.57f) * 0.2f
        adapter = TablesAdapter(height, listener)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        disposables = CompositeDisposable()

        Database.tables.subscribeOn(Schedulers.io())
            .map { tables -> tables.filter { table -> table.active } }
            .flatMap { tables ->
                tables.map { table ->
                    Database.orders.map { orders ->
                        val filtered = orders.filter { order -> order.table == table }
                        val total = filtered.sumByDouble { order ->
                            order.items.sumByDouble { item -> item.price * item.quantity }
                        }
                        TableTotal(table, filtered, total)
                    }
                }.combineLatest { tables -> tables }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { tables ->
                adapter.updateData(tables)
                val total = tables.sumByDouble { table -> table.total }
                textViewTotal.text = "R ${total}0"
            }
            .addTo(disposables)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposables.clear()
    }

    companion object {
        fun start(/*title: String*/): TablesFragment {
            val fragment = TablesFragment()
            // fragment.title = title
            return fragment
        }
    }
}