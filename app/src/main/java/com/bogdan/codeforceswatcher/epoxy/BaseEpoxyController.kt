package com.bogdan.codeforceswatcher.epoxy

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModel
import com.bogdan.codeforceswatcher.BuildConfig

abstract class BaseEpoxyController<T> : EpoxyController() {

    init {
        if (BuildConfig.DEBUG) {
            addInterceptor { requireUniqueModelIds(it) }
        }
    }

    var data: List<T>? = null
        set(value) {
            field = value
            requestModelBuild()
        }

    /**
     * If true, models wil be unbound and their views will be recycled once the RecyclerView is
     * detached from its window, which, in particular, ensures that views will remain visible during
     * exit animations.
     *
     * See https://github.com/airbnb/epoxy/wiki/Avoiding-Memory-Leaks#child-views
     */
    protected open val recycleChildrenOnDetach: Boolean = true

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        (recyclerView.layoutManager as? LinearLayoutManager)?.recycleChildrenOnDetach = recycleChildrenOnDetach
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)

        // This ensures that any pending model build won't be executed, as this could lead to
        // crashes if the controller depends on a view or fragment which might not be available anymore.
        cancelPendingModelBuild()
    }
}

/**
 * Throws an [IllegalArgumentException] if there are items with duplicate IDs in `models`.
 *
 * Models with duplicate IDs in an Epoxy controller can lead to crashes, as model IDs can end up
 * being used as stable IDs for view holders, which must be unique.
 */
fun EpoxyController.requireUniqueModelIds(models: List<EpoxyModel<*>>) {
    val allIds = models.map { it.id() }
    val uniqueIds = allIds.distinct()
    require(uniqueIds.size == allIds.size) {
        val dupClasses = models.toMutableList()
                .apply {
                    uniqueIds.map { id -> models.first { it.id() == id } }.forEach { remove(it) }
                }
                .map { it::class.java }
        "Found Epoxy models with duplicate ids in ${this::class.java}: $dupClasses!"
    }
}

