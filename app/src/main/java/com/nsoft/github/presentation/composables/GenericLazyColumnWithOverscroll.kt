package com.nsoft.github.presentation.composables

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll

// Helper extension to check if we're at the end
fun LazyListState.isAtEnd(): Boolean {
    return layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1
}

/**
 * A [Composable] extension over [LazyColumn] that takes a generic list of items of type [Type],
 * a composable method that generates the item's composable given it's index and value, and an overscroll
 * callback to invoke when the list has reached it's end
 *
 * @param onOverScrollCallback callback to be invoked when the list is overscrolled
 * @param itemsList list of items to display, of type [Type]
 * @param itemComposable a composable to use for the item itself
 */
@Composable
fun <Type> GenericLazyColumnWithOverscroll(
    onOverScrollCallback: () -> Unit,
    itemsList: List<Type>,
    itemComposable: @Composable (Int, Type) -> Unit
) {
    val lazyListState = rememberLazyListState()
    var isOverscrolling by remember { mutableStateOf(false) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                // Detect vertical overscroll once we're at the end
                if (lazyListState.isAtEnd() && available.y > 0) {
                    isOverscrolling = true
                    onOverScrollCallback()
                } else {
                    isOverscrolling = false
                }
                return super.onPostScroll(consumed, available, source)
            }
        }
    }

    LazyColumn(
        modifier = Modifier.nestedScroll(nestedScrollConnection),
        state = lazyListState
    ) {
        itemsIndexed(itemsList) { index, item ->
            itemComposable(index, item)
        }
    }
}
