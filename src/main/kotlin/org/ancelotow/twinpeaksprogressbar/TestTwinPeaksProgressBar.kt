package org.ancelotow.twinpeaksprogressbar

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import java.awt.Dimension
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JProgressBar
import javax.swing.SwingUtilities

class TestProgressBarAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val frame = JFrame("Twin Peaks Progress Bar Test").apply {
            defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
            size = Dimension(400, 200)
        }

        val panel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
        }

        val barDeterminate = JProgressBar(0, 100).apply {
            ui = TwinPeaksProgressBarUI.createUI(this)
        }

        val barIndeterminate = JProgressBar(0, 100).apply {
            ui = TwinPeaksProgressBarUI.createUI(this)
            isIndeterminate = true
        }

        panel.add(Box.createVerticalStrut(20))
        panel.add(barDeterminate)
        panel.add(Box.createVerticalStrut(20))
        panel.add(barIndeterminate)

        frame.contentPane = panel
        frame.isVisible = true

        ApplicationManager.getApplication().executeOnPooledThread {
            for (i in 0..100) {
                SwingUtilities.invokeLater {
                    barDeterminate.value = i;
                    barIndeterminate.value = i
                }
                Thread.sleep(80)
            }
        }
    }
}