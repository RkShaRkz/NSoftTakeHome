package com.nsoft.github.presentation.composables

import androidx.annotation.NonNull
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Dp

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
 * @param modifier the [Modifier] to apply, if any
 * @param useDivider whether we should use a [HorizontalDivider] between list items
 * @param dividerThickness the divider thickness to use. Defaults to [Dp.Hairline]
 */
@Composable
fun <Type> GenericLazyColumnWithOverscroll(
    @NonNull onOverScrollCallback: () -> Unit,
    @NonNull itemsList: List<Type>,
    @NonNull itemComposable: @Composable (Int, Type) -> Unit,
    @NonNull modifier: Modifier = Modifier,
    useDivider: Boolean = false,
    dividerThickness: Dp = Dp.Hairline,
    dividerColor: Color = Color.Transparent
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
                if (lazyListState.isAtEnd()) {
                    if (available.y < 0) {
                        // ensure we fire it just once
                        if (!isOverscrolling) {
                            isOverscrolling = true
                            onOverScrollCallback()
                        }
                    }
                } else {
                    isOverscrolling = false
                }
                return super.onPostScroll(consumed, available, source)
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .nestedScroll(nestedScrollConnection)
            .then(modifier),
        state = lazyListState
    ) {
        itemsIndexed(itemsList) { index, item ->
            itemComposable(index, item)
            if (useDivider) {
                HorizontalDivider(
                    color = dividerColor,
                    thickness = dividerThickness,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
