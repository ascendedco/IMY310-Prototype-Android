package co.ascended.waiterio.waiters

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.ascended.waiterio.R

class ReviewFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_review, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // textView.text = title
    }

    companion object {
        fun start(/*title: String*/): ReviewFragment {
            val fragment = ReviewFragment()
            // fragment.title = title
            return fragment
        }
    }
}