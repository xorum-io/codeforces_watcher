package com.bogdan.codeforceswatcher.epoxy

import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import com.airbnb.epoxy.EpoxyModel

abstract class BaseEpoxyModel(
        @LayoutRes private val layoutRes: Int
) : EpoxyModel<View>() {

    protected var boundView: View? = null
        private set

    final override fun buildView(parent: ViewGroup): View {
        val view = super.buildView(parent)
        initView(view)
        return view
    }

    /**
     * Called after a view has been created by [buildView].
     *
     * **Important:** Make sure this method does not reference anything from the model itself (via
     * click listeners for instance), as the view might be reused by other instances of the same
     * epoxy model class.
     */
    protected open fun initView(view: View) {}

    @CallSuper
    override fun bind(view: View) {
        super.bind(view)
        this.boundView = view
    }

    @CallSuper
    override fun unbind(view: View) {
        this.boundView = null
        super.unbind(view)
    }

    override fun getDefaultLayout(): Int {
        return layoutRes
    }
}
