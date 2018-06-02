package co.ascended.waiterio.waiters

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.ascended.waiterio.R
import co.ascended.waiterio.entity.Item
import kotlinx.android.synthetic.main.fragment_items.*

class ItemsFragment: Fragment() {

    private lateinit var adapter: ItemAddAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_items, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val height = (resources.displayMetrics.heightPixels * 0.57f) * 0.2f
        adapter = ItemAddAdapter(height)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }

    fun addItem(item: Item) {
        adapter.updateData(listOf(item))
    }

    fun getItems(): List<Item> {
        return adapter.items
    }

    companion object {
        fun start(/*title: String*/): ItemsFragment {
            val fragment = ItemsFragment()
            return fragment
        }
    }
}