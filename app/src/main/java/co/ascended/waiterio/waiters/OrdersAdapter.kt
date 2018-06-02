package co.ascended.waiterio.waiters

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import co.ascended.waiterio.R
import co.ascended.waiterio.entity.Order

class OrdersAdapter constructor(val height: Float, val clickOrder: (Order) -> Unit):
        RecyclerView.Adapter<OrdersAdapter.ViewHolder>() {

    var orders: ArrayList<Order> = arrayListOf()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders[position]
        holder.bind(order)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_order, parent, false)
        val layoutParams = itemView.layoutParams
        layoutParams.height = height.toInt()
        itemView.layoutParams = layoutParams
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int = orders.size

    fun updateData(orders: List<Order>) {
        if (this.orders.isEmpty()) {
            this.orders.addAll(orders)
            notifyDataSetChanged()
        }
        else {
            orders.forEach { o ->
                // Update existing instances of the order.
                val query = this.orders.filter { order -> order.number == o.number }
                if (query.size == 1) {
                    val index = this.orders.indexOf(query[0])
                    this.orders.removeAt(index)
                    this.orders.add(index, o)
                    notifyItemChanged(index)
                }
                // Or just insert a new order.
                else {
                    this.orders.add(0, o)
                    notifyItemInserted(0)
                }
            }
        }
    }

    fun clear() {
        orders = arrayListOf()
        notifyDataSetChanged()
    }

    inner class ViewHolder(var view: View): RecyclerView.ViewHolder(view) {

        private lateinit var order: Order

        private var number: TextView = view.findViewById(R.id.textViewOrder)
        private var status: TextView = view.findViewById(R.id.textViewStatus)
        private var background: View = view.findViewById(R.id.background)

        fun bind(order: Order) {
            val context = view.context
            this.order = order

            number.text = "Order #${this.order.number}"
            status.text = this.order.status
            status.setTextColor(when (this.order.status) {
                "Canceled" -> ContextCompat.getColor(context, R.color.redDark)
                "Served" -> ContextCompat.getColor(context, R.color.greenDark)
                "Delayed" -> ContextCompat.getColor(context, R.color.orange)
                "Pending" -> ContextCompat.getColor(context, R.color.gray)
                else -> ContextCompat.getColor(context, R.color.blueLight)
            })

            background.setOnClickListener {
                if (this.order.status != "Canceled" && this.order.status != "Served") {
                    clickOrder(this.order)
                }
            }
        }
    }
}