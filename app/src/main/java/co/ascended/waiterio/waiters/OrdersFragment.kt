package co.ascended.waiterio.waiters

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.PopupMenu
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.ascended.waiterio.R
import kotlinx.android.synthetic.main.fragment_orders.*

class OrdersFragment: Fragment() {

    sealed class Action {
        class Cancel: Action()
        class Served: Action()
    }

    lateinit var listener: (Action) -> Unit

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_orders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // textView.text = title

        item2.setOnClickListener {
            AlertDialog.Builder(context!!)
                    .setTitle("Order Status")
                    .setMessage("What should this order's status be updated to?")
                    .setPositiveButton("CANCELED", { _, _ -> listener(Action.Cancel()) })
                    .setNeutralButton("SERVED", { _, _ -> })
                    .show()
        }
        item3.setOnClickListener {
            AlertDialog.Builder(context!!)
                    .setTitle("Order Status")
                    .setMessage("What should this order's status be updated to?")
                    .setPositiveButton("CANCELED", { _, _ -> })
                    .setNeutralButton("SERVED", { _, _ -> listener(Action.Served()) })
                    .show()
        }
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
        fun start(/*title: String*/): OrdersFragment {
            val fragment = OrdersFragment()
            // fragment.title = title
            return fragment
        }
    }
}