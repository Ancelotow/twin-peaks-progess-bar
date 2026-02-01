package org.ancelotow.twinpeaksprogressbar.theme

import androidx.compose.runtime.Immutable
import com.intellij.openapi.util.IconLoader
import com.intellij.ui.JBColor
import org.ancelotow.twinpeaksprogressbar.TwinPeaksProgressBarUI
import java.awt.Color
import javax.swing.Icon

@Immutable
data class TwinPeaksTheme(
    val primaryColor: Color,

    val backgroundIcon: Icon,
    val determinateIcon: Icon,
    val indeterminateIcon: Icon
)

val defaultTheme = TwinPeaksTheme(
    primaryColor = JBColor(Color(0x8A2BE2), Color(0x5C1D9E)),
    backgroundIcon = IconLoader.getIcon("/images/black-lodge-floor.png", TwinPeaksProgressBarUI::class.java),
    determinateIcon = IconLoader.getIcon("/images/black-lodge-curtain.png", TwinPeaksProgressBarUI::class.java),
    indeterminateIcon = IconLoader.getIcon("/images/man-from-another-place.png", TwinPeaksProgressBarUI::class.java)
)