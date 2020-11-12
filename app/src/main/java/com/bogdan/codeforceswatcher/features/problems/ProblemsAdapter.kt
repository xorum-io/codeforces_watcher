package com.bogdan.codeforceswatcher.features.problems

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bogdan.codeforceswatcher.R
import io.xorum.codeforceswatcher.features.problems.models.Problem
import io.xorum.codeforceswatcher.features.problems.redux.requests.ProblemsRequests
import kotlinx.android.synthetic.main.view_problem_item.view.*
import io.xorum.codeforceswatcher.redux.store
import java.util.*

class ProblemsAdapter(
        private val context: Context,
        private val itemClickListener: (Problem) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    private var showingItems: MutableList<Problem> = mutableListOf()
    private var items: List<Problem> = listOf()
    private var isFavouriteStatus = false

    override fun getItemCount() = showingItems.size + if (showingItems.isEmpty()) 1 else 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            when (viewType) {
                STUB_ALL_PROBLEMS_VIEW_TYPE -> {
                    val layout = LayoutInflater.from(context).inflate(R.layout.view_all_problems_stub, parent, false)
                    StubViewHolder(layout)
                }
                STUB_FAVOURITE_PROBLEMS_VIEW_TYPE -> {
                    val layout = LayoutInflater.from(context).inflate(R.layout.view_favourite_problems_stub, parent, false)
                    StubViewHolder(layout)
                }
                else -> {
                    val layout = LayoutInflater.from(context).inflate(R.layout.view_problem_item, parent, false)
                    ProblemViewHolder(layout)
                }
            }

    override fun getItemViewType(position: Int): Int {
        return when {
            showingItems.isEmpty() && store.state.problems.isFavourite -> STUB_FAVOURITE_PROBLEMS_VIEW_TYPE
            showingItems.isEmpty() -> STUB_ALL_PROBLEMS_VIEW_TYPE
            else -> PROBLEM_VIEW_TYPE
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (showingItems.isEmpty()) return

        val problemViewHolder = viewHolder as ProblemViewHolder
        with(problemViewHolder) {
            with(showingItems[adapterPosition]) {
                tvProblemName.text = fullName
                tvContestName.text = contestName
                ivFavourite.setColorFilter(ContextCompat.getColor(
                        context, if (isFavourite) R.color.colorAccent else R.color.dark_gray)
                )

                onClickListener = { itemClickListener(this) }
                onFavouriteClickListener = {
                    store.dispatch(ProblemsRequests.ChangeStatusFavourite(this.copy()))
                    when (isFavouriteStatus) {
                        false -> notifyItemChanged(adapterPosition).also { changeProblem(this) }
                        true -> notifyItemRemoved(adapterPosition).also { removeProblem(this) }
                    }
                }
            }
        }
    }

    private fun changeProblem(problem: Problem) {
        problem.isFavourite = problem.isFavourite.not()
    }

    private fun removeProblem(problem: Problem) {
        items.minus(problem)
        showingItems.remove(problem)
    }

    fun setItems(problemsList: List<Problem>, constraint: String, isFavourite: Boolean) {
        this.isFavouriteStatus = isFavourite
        items = problemsList
        showingItems = buildFilteredList(constraint)
        notifyDataSetChanged()
    }

    private val problemFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = mutableListOf<Problem>()
            filteredList.addAll(
                    if (constraint.isNullOrEmpty()) items else buildFilteredList(constraint.toString())
            )
            return FilterResults().apply { values = filteredList }
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            showingItems.clear()
            showingItems.addAll(results.values as List<Problem>)
            notifyDataSetChanged()
        }
    }

    private fun buildFilteredList(constraint: String): MutableList<Problem> {
        val lowerCaseConstraint = constraint.lowercase()
        val filteredList = mutableListOf<Problem>()

        for (problem in items) {
            val fullProblemNameEn = "${problem.contestId}${problem.index}: ${problem.enName.lowercase()}"
            val fullProblemNameRu = "${problem.contestId}${problem.index}: ${problem.ruName.lowercase()}"
            if (fullProblemNameEn.kmpContains(lowerCaseConstraint)
                    || fullProblemNameRu.kmpContains(lowerCaseConstraint)
                    || problem.contestName.lowercase().kmpContains(lowerCaseConstraint)) {
                filteredList.add(problem)
            }
        }
        return filteredList
    }

    private fun String.lowercase() = this.toLowerCase(Locale.getDefault())

    private fun String.kmpContains(searchString: String): Boolean {
        val findingStringLength = searchString.length
        val cmpStr = "$searchString%$this"
        val prefixArray = IntArray(cmpStr.length)
        var currentIndexOfBlock = 0
        prefixArray[0] = 0
        for (i in 1 until cmpStr.length) {
            while (currentIndexOfBlock > 0 && cmpStr[currentIndexOfBlock] != cmpStr[i]) {
                currentIndexOfBlock = prefixArray[currentIndexOfBlock - 1]
            }
            if (cmpStr[currentIndexOfBlock] == cmpStr[i]) {
                currentIndexOfBlock++
            }

            prefixArray[i] = currentIndexOfBlock
            if (prefixArray[i] == findingStringLength) {
                return true
            }
        }
        return false
    }

    override fun getFilter() = problemFilter

    class ProblemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val tvProblemName: TextView = view.tvProblemName
        val tvContestName: TextView = view.tvContestName
        val ivFavourite: ImageView = view.ivFavourite

        var onClickListener: (() -> Unit)? = null
        var onFavouriteClickListener: (() -> Unit)? = null

        init {
            view.setOnClickListener { onClickListener?.invoke() }
            view.ivFavourite.setOnClickListener { onFavouriteClickListener?.invoke() }
        }
    }

    data class StubViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    companion object {
        const val STUB_ALL_PROBLEMS_VIEW_TYPE = 0
        const val STUB_FAVOURITE_PROBLEMS_VIEW_TYPE = 2
        const val PROBLEM_VIEW_TYPE = 1
    }
}
