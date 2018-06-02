package co.ascended.waiterio.waiters

import android.support.percent.PercentRelativeLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import co.ascended.waiterio.R
import co.ascended.waiterio.entity.Item
import co.ascended.waiterio.widget.IconButton
import co.ascended.waiterio.widget.setMargins

class ItemReviewAdapter constructor(val height: Float): RecyclerView.Adapter<ItemReviewAdapter.ViewHolder>() {

    var items: ArrayList<Item> = arrayListOf()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_item_review, parent, false)
        val layoutParams = itemView.layoutParams
        layoutParams.height = height.toInt()
        itemView.layoutParams = layoutParams
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int = items.size

    fun updateData(items: List<Item>) {
        this.items = arrayListOf()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun clear() {
        items = arrayListOf()
        notifyDataSetChanged()
    }

    inner class ViewHolder(var view: View): RecyclerView.ViewHolder(view) {

        private lateinit var item: Item

        private var root: PercentRelativeLayout = view.findViewById(R.id.root)
        private var name: TextView = view.findViewById(R.id.textViewName)
        private var quantity: TextView = view.findViewById(R.id.textViewQuantity)
        private var price: TextView = view.findViewById(R.id.textViewPrice)

        fun bind(item: Item) {
            setMargins(root)
            this.item = item

            name.text = this.item.name
            quantity.text = "${this.item.quantity}x"
            price.text = "${this.item.quantity * this.item.price}0"
        }
    }
}