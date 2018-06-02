package co.ascended.waiterio.waiters

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import co.ascended.waiterio.R
import co.ascended.waiterio.entity.Table

class TablesAdapter constructor(val height: Float, val width: Float, var selected: Int):
        RecyclerView.Adapter<TablesAdapter.ViewHolder>() {

    var tables: ArrayList<Table> = arrayListOf()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val table = tables[position]
        holder.bind(table)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_table_select, parent, false)
        val layoutParams = itemView.layoutParams
        layoutParams.height = height.toInt()
        layoutParams.width = width.toInt()
        itemView.layoutParams = layoutParams
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int = tables.size

    fun updateData(tables: List<Table>) {
        if (this.tables.isEmpty()) {
            this.tables.addAll(tables)
            notifyDataSetChanged()
        }
        else {
            tables.forEach { o ->
                // Update existing instances of the table.
                val query = this.tables.filter { table -> table.number == o.number }
                if (query.size == 1) {
                    val index = this.tables.indexOf(query[0])
                    this.tables.removeAt(index)
                    this.tables.add(index, o)
                    notifyItemChanged(index)
                }
                // Or just insert a new table.
                else {
                    this.tables.add(0, o)
                    notifyItemInserted(0)
                }
            }
        }
    }

    fun clear() {
        tables = arrayListOf()
        notifyDataSetChanged()
    }

    inner class ViewHolder(var view: View): RecyclerView.ViewHolder(view) {

        private lateinit var table: Table

        private var number: TextView = view.findViewById(R.id.textViewTable)
        private var background: View = view.findViewById(R.id.background)

        fun bind(table: Table) {
            val context = view.context
            this.table = table

            number.text = "Table ${this.table.number}"
            if (this.table.number == selected) {
                background.background = ContextCompat.getDrawable(context, R.drawable.selector_table_active)
                number.setTextColor(ContextCompat.getColor(context, R.color.blueLight))
            }
            else {
                background.background = ContextCompat.getDrawable(context, R.drawable.selector_table_inactive)
                number.setTextColor(ContextCompat.getColor(context, R.color.gray90))
            }

            background.setOnClickListener {
                selected = this.table.number
                notifyDataSetChanged()
            }
        }
    }
}