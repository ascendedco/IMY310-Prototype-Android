package co.ascended.waiterio.managers

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.ascended.waiterio.R
import co.ascended.waiterio.database.Database
import co.ascended.waiterio.entity.Item
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_items_m.*

class ItemsFragment: Fragment() {

    private lateinit var adapter: ItemsAdapter
    private lateinit var disposables: CompositeDisposable
    lateinit var listener: (Item, Boolean) -> Unit

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_items_m, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val height = (resources.displayMetrics.heightPixels * 0.57f) * 0.2f
        adapter = ItemsAdapter(height, listener)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        disposables = CompositeDisposable()

        Database.items.map { items -> items.sortedBy { item -> item.name } }
            .subscribe { items -> adapter.updateData(items) }
            .addTo(disposables)
    }

    fun removeItem(item: Item) {
        adapter.removeItem(item)
    }

    companion object {
        fun start(/*title: String*/): ItemsFragment {
            val fragment = ItemsFragment()
            // fragment.title = title
            return fragment
        }
    }
}