package co.ascended.waiterio.managers

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.ascended.waiterio.R
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.fragment_item.*

class ItemFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // textView.text = title

        val categories = ArrayList<String>()
        categories.add("Breakfasts")
        categories.add("Starters")
        categories.add("Mains")
        categories.add("Deserts")
        categories.add("Drinks")

        val dataAdapter = ArrayAdapter<String>(context!!, android.R.layout.simple_spinner_item, categories)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = dataAdapter
    }

    companion object {
        fun start(/*title: String*/): ItemFragment {
            val fragment = ItemFragment()
            // fragment.title = title
            return fragment
        }
    }
}