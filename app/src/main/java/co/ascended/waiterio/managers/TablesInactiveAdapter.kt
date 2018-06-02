package co.ascended.waiterio.managers

import android.support.percent.PercentRelativeLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import co.ascended.waiterio.R
import co.ascended.waiterio.entity.Table
import co.ascended.waiterio.widget.setMargins

class TablesInactiveAdapter constructor(val height: Float, val addTable: (Table) -> Unit):
        RecyclerView.Adapter<TablesInactiveAdapter.ViewHolder>() {

    var tables: ArrayList<Table> = arrayListOf()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = tables[position]
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_table_inactive, parent, false)
        val layoutParams = itemView.layoutParams
        layoutParams.height = height.toInt()
        itemView.layoutParams = layoutParams
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int = tables.size

    fun updateData(tables: List<Table>) {
        this.tables = arrayListOf()
        this.tables.addAll(tables)
        notifyDataSetChanged()
    }

    fun clear() {
        tables = arrayListOf()
        notifyDataSetChanged()
    }

    inner class ViewHolder(var view: View): RecyclerView.ViewHolder(view) {

        private lateinit var table: Table

        private var root: PercentRelativeLayout = view.findViewById(R.id.background)
        private var name: TextView = view.findViewById(R.id.textViewTable)
        private var add: Button = view.findViewById(R.id.buttonAdd)

        fun bind(table: Table) {
            setMargins(root)
            this.table = table

            name.text = "Table ${this.table.number}"
            add.setOnClickListener { addTable(this.table) }
            // total.text = "R ${this.item.total}0"
        }
    }
}