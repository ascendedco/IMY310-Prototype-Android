package co.ascended.waiterio.waiters

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.ascended.waiterio.R
import co.ascended.waiterio.entity.Item
import kotlinx.android.synthetic.main.fragment_review.*

class ReviewFragment: Fragment() {

    private lateinit var adapter: ItemReviewAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_review, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val height = (resources.displayMetrics.heightPixels * 0.57f) * 0.2f
        adapter = ItemReviewAdapter(height)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }

    fun setItems(items: List<Item>) {
        adapter.updateData(items)
        val total = items.sumByDouble { item -> item.price * item.quantity }
        textViewTotal.text = "R ${total}0"
    }

    companion object {
        fun start(/*title: String*/): ReviewFragment {
            val fragment = ReviewFragment()
            return fragment
        }
    }
}