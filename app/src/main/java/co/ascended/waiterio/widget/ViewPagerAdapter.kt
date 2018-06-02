package co.ascended.waiterio.widget

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

class ViewPagerAdapter(fragmentManager: FragmentManager): FragmentStatePagerAdapter(fragmentManager) {

    private val fragments = ArrayList<Fragment>()
    private val titles = ArrayList<String>()

    fun addFragment(fragment: Fragment, title: String) {
        if (!fragments.contains(fragment)) {
            fragments.add(fragment)
            titles.add(title)
            notifyDataSetChanged()
        }
    }

    fun clear() {
        fragments.clear()
        notifyDataSetChanged()
    }

    fun addFragment(fragment: Fragment, title: String, index: Int) {
        if (!fragments.contains(fragment)) {
            fragments.add(index, fragment)
            titles.add(index, title)
            notifyDataSetChanged()
        }
    }

    fun removeFragment(index: Int) {
        fragments.removeAt(index)
        titles.removeAt(index)
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return titles[position]
    }
}