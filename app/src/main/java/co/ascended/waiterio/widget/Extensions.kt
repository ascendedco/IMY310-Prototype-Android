package co.ascended.waiterio.widget

import android.support.percent.PercentRelativeLayout
import android.support.v4.view.MarginLayoutParamsCompat
import android.support.v7.widget.RecyclerView

fun Any.tag(): String {
    return javaClass.simpleName
}

/**
 * Method that recursively loops through all child views of the parameter view and sets margins for them.
 *
 * @param root Root view.
 */
fun RecyclerView.ViewHolder.setMargins(root: PercentRelativeLayout) {
    val displayMetrics = root.context.resources.displayMetrics
    for (x in 0 until root.childCount) {
        val view = root.getChildAt(x)
        if (view.javaClass == PercentRelativeLayout::class.java) {
            setMargins(view as PercentRelativeLayout)
        }
        val p = view.layoutParams as PercentRelativeLayout.LayoutParams
        val i = p.percentLayoutInfo
        if (i.leftMarginPercent == -1f && i.rightMarginPercent != -1f) {
            MarginLayoutParamsCompat.setMarginEnd(p, Math.round(i.rightMarginPercent * displayMetrics.widthPixels))
        }
        else if (i.rightMarginPercent == -1f && i.leftMarginPercent != -1f) {
            MarginLayoutParamsCompat.setMarginStart(p, Math.round(i.leftMarginPercent * displayMetrics.widthPixels))
        }
    }
}