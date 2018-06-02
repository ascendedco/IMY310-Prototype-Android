package co.ascended.waiterio.waiters

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.ascended.waiterio.R
import co.ascended.waiterio.database.Database
import co.ascended.waiterio.entity.Order
import co.ascended.waiterio.entity.Table
import co.ascended.waiterio.widget.tag
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_orders.*

class OrdersFragment: Fragment() {

    sealed class Action(val order: Order) {
        class Cancel(order: Order): Action(order)
        class Served(order: Order): Action(order)
    }

    private var disposables = CompositeDisposable()
    lateinit var listener: (Action) -> Unit
    lateinit var adapter: OrdersAdapter
    private var manager = false
    lateinit var table: Table

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_orders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // textView.text = title
        val height = (resources.displayMetrics.heightPixels * 0.57f) * 0.2f

        adapter = OrdersAdapter(height, { order ->
            if (!manager) {
                AlertDialog.Builder(context!!)
                        .setTitle("Order Status")
                        .setMessage("What should this order's status be updated to?")
                        .setPositiveButton("CANCELED", { _, _ -> listener(Action.Cancel(order)) })
                        .setNeutralButton("SERVED", { _, _ -> listener(Action.Served(order)) })
                        .show()
            }
        })

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        Database.orders.map { orders -> orders.filter { order -> order.table == table } }
            // .distinctUntilChanged()
            .subscribe { orders ->
                Log.e(tag(), "Orders updated!!")
                adapter.updateData(orders)
                val total = orders.sumByDouble { order ->
                    when (order.status) {
                        "Canceled" -> 0.0
                        else -> order.items.sumByDouble { item -> item.price * item.quantity }
                    }
                }
                textViewTotal.text = "R ${total}0"
            }
            .addTo(disposables)
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
    }

    private fun showMenu(view: View) {
        val popupMenu = PopupMenu(context!!, view)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId){
                /*R.id.menu_open_website -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://resocoder.com"))
                    startActivity(intent)
                    true
                }
                R.id.menu_show_toast -> {
                    Toast.makeText(this, "Showing Toast!", Toast.LENGTH_LONG).show()
                    true
                }
                else -> false*/
            }
        }

        popupMenu.inflate(R.menu.menu_order)

        try {
            val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
            fieldMPopup.isAccessible = true
            val mPopup = fieldMPopup.get(popupMenu)
            mPopup.javaClass
                    .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(mPopup, true)
        } catch (e: Exception){
            Log.e("Main", "Error showing menu icons.", e)
        } finally {
            popupMenu.show()
        }
    }

    companion object {
        fun start(table: Table): OrdersFragment {
            val fragment = OrdersFragment()
            fragment.table = table
            return fragment
        }

        fun start(table: Table, manager: Boolean): OrdersFragment {
            val fragment = OrdersFragment()
            fragment.manager = manager
            fragment.table = table
            return fragment
        }
    }
}