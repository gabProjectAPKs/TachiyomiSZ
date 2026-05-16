package eu.kanade.presentation.library.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import tachiyomi.presentation.core.util.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import eu.kanade.core.preference.PreferenceMutableState
import eu.kanade.tachiyomi.ui.library.LibraryItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tachiyomi.core.common.preference.TriState
import tachiyomi.domain.category.model.Category
import tachiyomi.domain.library.model.LibraryDisplayMode
import tachiyomi.domain.library.model.LibraryManga
import tachiyomi.domain.library.service.LibraryPreferences
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.material.PullRefresh
import tachiyomi.presentation.core.i18n.stringResource
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import kotlin.time.Duration.Companion.seconds

@Composable
fun LibraryContent(
    categories: List<Category>,
    searchQuery: String?,
    selection: List<LibraryManga>,
    contentPadding: PaddingValues,
    currentPage: () -> Int,
    hasActiveFilters: Boolean,
    showPageTabs: Boolean,
    onChangeCurrentPage: (Int) -> Unit,
    onMangaClicked: (Long) -> Unit,
    onContinueReadingClicked: ((LibraryManga) -> Unit)?,
    onToggleSelection: (LibraryManga) -> Unit,
    onToggleRangeSelection: (LibraryManga) -> Unit,
    onRefresh: (Category?) -> Boolean,
    onGlobalSearchClicked: () -> Unit,
    getNumberOfMangaForCategory: (Category) -> Int?,
    getDisplayMode: (Int) -> PreferenceMutableState<LibraryDisplayMode>,
    getColumnsForOrientation: (Boolean) -> PreferenceMutableState<Int>,
    getLibraryForPage: (Int) -> List<LibraryItem>,
) {
    Column(
        modifier = Modifier.padding(
            top = contentPadding.calculateTopPadding(),
            start = contentPadding.calculateStartPadding(LocalLayoutDirection.current),
            end = contentPadding.calculateEndPadding(LocalLayoutDirection.current),
        ),
    ) {
        // SY -->
        val coercedCurrentPage = remember(categories) { currentPage().coerceIn(0, categories.lastIndex) }
        val pagerState = rememberPagerState(coercedCurrentPage) { categories.size }
        // SY <--

        val scope = rememberCoroutineScope()
        var isRefreshing by remember(pagerState.currentPage) { mutableStateOf(false) }

        if (showPageTabs && categories.size > 1) {
            LaunchedEffect(categories) {
                if (categories.size <= pagerState.currentPage) {
                    pagerState.scrollToPage(categories.size - 1)
                }
            }
            LibraryTabs(
                categories = categories,
                pagerState = pagerState,
                getNumberOfMangaForCategory = getNumberOfMangaForCategory,
            ) { scope.launch { pagerState.animateScrollToPage(it) } }
        }

        // SY -->
        val libraryPrefs = remember { Injekt.get<LibraryPreferences>() }
        LibraryFilterChips(
            libraryPrefs = libraryPrefs,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
        // SY <--

        val notSelectionMode = selection.isEmpty()
        val onClickManga = { manga: LibraryManga ->
            if (notSelectionMode) {
                onMangaClicked(manga.manga.id)
            } else {
                onToggleSelection(manga)
            }
        }

        PullRefresh(
            refreshing = isRefreshing,
            onRefresh = {
                val started = onRefresh(categories.getOrNull(currentPage()) ?: return@PullRefresh)
                if (!started) return@PullRefresh
                scope.launch {
                    // Fake refresh status but hide it after a second as it's a long running task
                    isRefreshing = true
                    delay(1.seconds)
                    isRefreshing = false
                }
            },
            enabled = notSelectionMode,
        ) {
            LibraryPager(
                state = pagerState,
                contentPadding = PaddingValues(bottom = contentPadding.calculateBottomPadding()),
                hasActiveFilters = hasActiveFilters,
                selectedManga = selection,
                searchQuery = searchQuery,
                onGlobalSearchClicked = onGlobalSearchClicked,
                getDisplayMode = getDisplayMode,
                getColumnsForOrientation = getColumnsForOrientation,
                getLibraryForPage = getLibraryForPage,
                onClickManga = onClickManga,
                onLongClickManga = onToggleRangeSelection,
                onClickContinueReading = onContinueReadingClicked,
            )
        }

        LaunchedEffect(pagerState.currentPage) {
            onChangeCurrentPage(pagerState.currentPage)
        }
    }
}

// SY -->
@Composable
private fun LibraryFilterChips(
    libraryPrefs: LibraryPreferences,
    modifier: Modifier = Modifier,
) {
    val filterUnread by libraryPrefs.filterUnread().collectAsState()
    val filterDownloaded by libraryPrefs.filterDownloaded().collectAsState()
    val filterCompleted by libraryPrefs.filterCompleted().collectAsState()

    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FilterChip(
            selected = filterUnread == TriState.ENABLED_IS ||
                filterDownloaded == TriState.ENABLED_IS ||
                filterCompleted == TriState.ENABLED_IS,
            onClick = {
                libraryPrefs.filterUnread().set(TriState.DISABLED)
                libraryPrefs.filterDownloaded().set(TriState.DISABLED)
                libraryPrefs.filterCompleted().set(TriState.DISABLED)
            },
            label = { Text(stringResource(MR.strings.all_genres)) },
        )
        FilterChip(
            selected = filterUnread == TriState.ENABLED_IS,
            onClick = {
                libraryPrefs.filterUnread().set(
                    if (filterUnread == TriState.ENABLED_IS) TriState.DISABLED else TriState.ENABLED_IS,
                )
            },
            label = { Text(stringResource(MR.strings.action_filter_unread)) },
        )
        FilterChip(
            selected = filterDownloaded == TriState.ENABLED_IS,
            onClick = {
                libraryPrefs.filterDownloaded().set(
                    if (filterDownloaded == TriState.ENABLED_IS) TriState.DISABLED else TriState.ENABLED_IS,
                )
            },
            label = { Text(stringResource(MR.strings.label_downloaded)) },
        )
        FilterChip(
            selected = filterCompleted == TriState.ENABLED_IS,
            onClick = {
                libraryPrefs.filterCompleted().set(
                    if (filterCompleted == TriState.ENABLED_IS) TriState.DISABLED else TriState.ENABLED_IS,
                )
            },
            label = { Text(stringResource(MR.strings.completed)) },
        )
    }
    HorizontalDivider()
}
// SY <--
