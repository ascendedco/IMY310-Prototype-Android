package co.ascended.waiterio.managers

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.Snackbar.LENGTH_LONG
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.ascended.waiterio.R
import android.widget.ArrayAdapter
import co.ascended.waiterio.database.Database
import co.ascended.waiterio.entity.Category
import co.ascended.waiterio.entity.Item
import co.ascended.waiterio.widget.tag
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_item.*

class ItemFragment: Fragment() {

    private var item: Item? = null
    private var categories = mutableListOf<Category>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // textView.text = title

        val adapterData = ArrayList<String>()
        /*categories.add("Breakfasts")
        categories.add("Starters")
        categories.add("Mains")
        categories.add("Deserts")
        categories.add("Drinks")*/

        val dataAdapter = ArrayAdapter<String>(context!!, android.R.layout.simple_spinner_item, adapterData)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = dataAdapter

        val disposables = CompositeDisposable()
        Database.categories.subscribe { categories ->
            this.categories.clear()
            this.categories.addAll(categories)
            adapterData.clear()
            categories.forEach { category ->
                adapterData.add(category.name)
            }
            dataAdapter.notifyDataSetChanged()
        }.addTo(disposables)

        if (this.item != null) {
            textViewName.setText(item!!.name)
            textViewDescription.setText(item!!.description)
            textViewPrice.setText(item!!.price.toInt().toString())
            checkBoxAvailable.isChecked = item!!.available
        }
    }

    fun setItem(item: Item) {
        this.item = item
        if (textViewName != null) {
            textViewName.setText(item.name)
            textViewDescription.setText(item.description)
            textViewPrice.setText(item.price.toInt().toString())
            checkBoxAvailable.isChecked = item.available
        }
    }

    fun addItem(listener: (Item) -> Unit) {
        val name = textViewName.text.toString()
        val description = textViewDescription.text.toString()
        val available = checkBoxAvailable.isChecked
        val price = textViewPrice.text.toString()

        if (price.trim().isEmpty() || name.trim().isEmpty() || description.trim().isEmpty()) {
            Snackbar.make(root, "Some fields are empty.", LENGTH_LONG).show()
        }
        else {
            try {
                val category = categories.first { category ->
                    category.name == spinner.selectedItem.toString()
                }
                val formatted = price.toDouble()
                val id = if (this.item == null) -1 else this.item!!.id
                val item = Item(id, name, description, category, available, 0, formatted)
                listener(item)
            }
            catch (exception: NumberFormatException) {
                Snackbar.make(root, "Price has to be a number.", LENGTH_LONG).show()
            }
        }
    }

    fun clear() {
        this.item = null
        textViewName.setText("")
        textViewDescription.setText("")
        textViewPrice.setText("")
        checkBoxAvailable.isChecked = false
    }

    companion object {
        fun start(/*title: String*/): ItemFragment {
            val fragment = ItemFragment()
            // fragment.title = title
            return fragment
        }

        fun start(item: Item): ItemFragment {
            val fragment = ItemFragment()
            fragment.item = item
            return fragment
        }
    }
}