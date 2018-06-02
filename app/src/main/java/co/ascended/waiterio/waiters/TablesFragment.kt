package co.ascended.waiterio.waiters

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.ascended.waiterio.R
import co.ascended.waiterio.database.Database
import co.ascended.waiterio.entity.Table
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_orders.*

class TablesFragment: Fragment() {

    private var disposables = CompositeDisposable()
    lateinit var adapter: TablesAdapter
    var table: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tables, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val height = (resources.displayMetrics.heightPixels * 0.57f) * 0.25f
        val width = (resources.displayMetrics.widthPixels * 0.88f) * 0.5f

        adapter = TablesAdapter(height, width, table)
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        recyclerView.adapter = adapter

        Database.tables.map { tables -> tables.filter { table -> table.active } }
            .subscribe { tables -> adapter.updateData(tables) }.addTo(disposables)
    }

    fun getTable(): Table {
        return adapter.tables.find { table -> table.number == adapter.selected }!!
    }

    companion object {
        fun start(table: Int): TablesFragment {
            val fragment = TablesFragment()
            fragment.table = table
            return fragment
        }
    }
}