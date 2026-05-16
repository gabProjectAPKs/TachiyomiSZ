package eu.kanade.presentation.reader.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import eu.kanade.tachiyomi.ui.reader.setting.ReaderOrientation
import eu.kanade.tachiyomi.ui.reader.setting.ReadingMode
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.i18n.stringResource

@Composable
fun ReaderQuickSettingsDialog(
    currentBrightness: Int,
    onBrightnessChanged: (Int) -> Unit,
    readingMode: Int,
    onChangeReadingMode: (ReadingMode) -> Unit,
    orientation: Int,
    onChangeOrientation: (ReaderOrientation) -> Unit,
    cropEnabled: Boolean,
    onToggleCrop: () -> Unit,
    grayscaleEnabled: Boolean,
    onToggleGrayscale: () -> Unit,
    invertedEnabled: Boolean,
    onToggleInverted: () -> Unit,
    keepScreenOn: Boolean,
    onToggleKeepScreenOn: () -> Unit,
    fullscreenEnabled: Boolean,
    onToggleFullscreen: () -> Unit,
    onOpenFullSettings: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(MR.strings.pref_category_general),
                style = MaterialTheme.typography.headlineSmall,
            )

            HorizontalDivider()

            // Brightness
            Text(text = stringResource(MR.strings.pref_custom_brightness), style = MaterialTheme.typography.titleSmall)
            var brightness by remember(currentBrightness) { mutableFloatStateOf(currentBrightness.toFloat()) }
            Slider(
                value = brightness,
                onValueChange = { brightness = it },
                onValueChangeFinished = { onBrightnessChanged(brightness.toInt()) },
                valueRange = -100f..100f,
                modifier = Modifier.fillMaxWidth(),
            )

            // Keep screen on
            SettingsToggle(
                label = stringResource(MR.strings.pref_keep_screen_on),
                checked = keepScreenOn,
                onCheckedChange = onToggleKeepScreenOn,
            )

            // Fullscreen
            SettingsToggle(
                label = stringResource(MR.strings.pref_fullscreen),
                checked = fullscreenEnabled,
                onCheckedChange = onToggleFullscreen,
            )

            HorizontalDivider()

            // Reading mode
            Text(text = stringResource(MR.strings.pref_category_reading_mode), style = MaterialTheme.typography.titleSmall)
            val currentReadingMode = remember(readingMode) {
                ReadingMode.fromPreference(readingMode)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                ReadingMode.entries.forEach { mode ->
                    FilterChip(
                        selected = currentReadingMode == mode,
                        onClick = { onChangeReadingMode(mode) },
                        label = { Text(stringResource(mode.stringRes), style = MaterialTheme.typography.labelSmall) },
                    )
                }
            }

            // Orientation
            val currentOrientation = remember(orientation) {
                ReaderOrientation.fromPreference(orientation)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                ReaderOrientation.entries.forEach { orient ->
                    FilterChip(
                        selected = currentOrientation == orient,
                        onClick = { onChangeOrientation(orient) },
                        label = { Text(stringResource(orient.titleRes), style = MaterialTheme.typography.labelSmall) },
                    )
                }
            }

            HorizontalDivider()

            // Crop borders
            SettingsToggle(
                label = stringResource(MR.strings.pref_crop_borders),
                checked = cropEnabled,
                onCheckedChange = onToggleCrop,
            )

            // Grayscale
            SettingsToggle(
                label = stringResource(MR.strings.pref_grayscale),
                checked = grayscaleEnabled,
                onCheckedChange = onToggleGrayscale,
            )

            // Inverted colors
            SettingsToggle(
                label = stringResource(MR.strings.pref_inverted_colors),
                checked = invertedEnabled,
                onCheckedChange = onToggleInverted,
            )

            Spacer(Modifier.height(8.dp))

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TextButton(onClick = onDismissRequest, modifier = Modifier.weight(1f)) {
                    Text(stringResource(MR.strings.action_close))
                }
                Button(onClick = onOpenFullSettings, modifier = Modifier.weight(1f)) {
                    Text(stringResource(MR.strings.action_settings))
                }
            }
        }
    }
}

@Composable
private fun SettingsToggle(
    label: String,
    checked: Boolean,
    onCheckedChange: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Switch(
            checked = checked,
            onCheckedChange = { onCheckedChange() },
        )
    }
}
