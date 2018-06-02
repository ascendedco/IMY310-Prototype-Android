package co.ascended.waiterio.managers

import android.support.percent.PercentRelativeLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import co.ascended.waiterio.R
import co.ascended.waiterio.entity.Table
import co.ascended.waiterio.managers.TablesFragment.TableTotal
import co.ascended.waiterio.widget.setMargins

class TablesAdapter constructor(val height: Float, val showTable: (Table) -> Unit):
        RecyclerView.Adapter<TablesAdapter.ViewHolder>() {

    var tables: ArrayList<TableTotal> = arrayListOf()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = tables[position]
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_table, parent, false)
        val layoutParams = itemView.layoutParams
        layoutParams.height = height.toInt()
        itemView.layoutParams = layoutParams
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int = tables.size

    fun updateData(tables: List<TableTotal>) {
        this.tables = arrayListOf()
        this.tables.addAll(tables)
        notifyDataSetChanged()

    }

    fun clear() {
        tables = arrayListOf()
        notifyDataSetChanged()
    }

    inner class ViewHolder(var view: View): RecyclerView.ViewHolder(view) {

        private lateinit var item: TableTotal

        private var root: PercentRelativeLayout = view.findViewById(R.id.background)
        private var name: TextView = view.findViewById(R.id.textViewTable)
        private var total: TextView = view.findViewById(R.id.textViewTotal)

        fun bind(item: TableTotal) {
            setMargins(root)
            this.item = item

            name.text = "Table ${this.item.table.number}"
            total.text = "R ${this.item.total}0"

            root.setOnClickListener { showTable(this.item.table) }
        }
    }
}