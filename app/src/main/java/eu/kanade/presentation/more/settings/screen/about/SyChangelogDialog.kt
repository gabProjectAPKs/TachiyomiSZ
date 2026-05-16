package eu.kanade.presentation.more.settings.screen.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.i18n.stringResource

private data class ChangelogEntry(
    val type: String,
    val message: String,
    val color: androidx.compose.ui.graphics.Color,
)

private val changes = listOf(
    ChangelogEntry("[+]", "Smooth list animations on Library, Browse, and Manga screens", androidx.compose.ui.graphics.Color(0xFF4CAF50)),
    ChangelogEntry("[+]", "Changelog popup on app launch with rounded corners", androidx.compose.ui.graphics.Color(0xFF4CAF50)),
    ChangelogEntry("[~]", "Fixed 15+ NPE crash bugs in Reader, Backup, Notifications", androidx.compose.ui.graphics.Color(0xFF2196F3)),
    ChangelogEntry("[~]", "Fixed ANR in NotificationReceiver (removed runBlocking)", androidx.compose.ui.graphics.Color(0xFF2196F3)),
    ChangelogEntry("[~]", "Fixed 412 infinite loop in SyncYomi sync service", androidx.compose.ui.graphics.Color(0xFF2196F3)),
    ChangelogEntry("[~]", "Fixed Downloader stream leak (inputStream not closed)", androidx.compose.ui.graphics.Color(0xFF2196F3)),
    ChangelogEntry("[~]", "Fixed ArchivePageLoader deadlock (unmanaged CoroutineScope)", androidx.compose.ui.graphics.Color(0xFF2196F3)),
    ChangelogEntry("[~]", "Fixed TODO runtime crashes in Suwayomi/Komga/Kavita trackers", androidx.compose.ui.graphics.Color(0xFF2196F3)),
    ChangelogEntry("[~]", "Fixed AniList/Comick recommendation API crash on null fields", androidx.compose.ui.graphics.Color(0xFF2196F3)),
    ChangelogEntry("[~]", "Deprecated GlobalScope usage to prevent memory leaks", androidx.compose.ui.graphics.Color(0xFF2196F3)),
)

@Composable
fun SyChangelogDialog(onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(28.dp),
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(MR.strings.action_ok))
            }
        },
        title = {
            Text(
                text = "Tachiyomi SY - Changelog",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column {
                Text(
                    text = "Bug fixes, enhancements & new features by Gab",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
                HorizontalDivider()
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                ) {
                    items(changes) { entry ->
                        Text(
                            text = buildAnnotatedString {
                                withStyle(SpanStyle(color = entry.color, fontWeight = FontWeight.Bold)) {
                                    append(entry.type)
                                }
                                append("  ")
                                append(entry.message)
                            },
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        },
    )
}
