package com.dfa.ui.generalpublic.pagination

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class PaginationScrollListener
/**
 * Supporting only LinearLayoutManager for now.
 *
 * @param layoutManager
 */
    (var layoutManager: LinearLayoutManager) : RecyclerView.OnScrollListener() {

    companion object {
        var PAGE_START = 1
    }

    val PAGE_SIZE = 10

    abstract fun isLastPage(): Boolean

    abstract fun isLoading(): Boolean


  /*  override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        loadMoreItems()
    }*/

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val visibleItemCount = layoutManager.childCount
        val totalItemCount = layoutManager.itemCount
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

        if (!isLoading() && !isLastPage()) {
            if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0  && totalItemCount >= PAGE_SIZE) {
                loadMoreItems()
            }//                  && totalItemCount >= ClothesFragment.itemsCount
        }
    }

    abstract fun loadMoreItems()
}