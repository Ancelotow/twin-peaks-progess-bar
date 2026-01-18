package org.ancelotow.twinpeaksprogressbar

import com.intellij.ide.ui.LafManager
import com.intellij.ide.ui.LafManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import javax.swing.UIManager

class TwinPeaksAppComponent : LafManagerListener, ProjectActivity {
    
    override suspend fun execute(project: Project) {
        updateUI()
    }

    override fun lookAndFeelChanged(p0: LafManager) {
        updateUI()
    }

    private fun updateUI() {
        UIManager.put("ProgressBarUI", TwinPeaksProgressBarUI::class.java.name)
        UIManager.getDefaults()[TwinPeaksProgressBarUI::class.java.name] = TwinPeaksProgressBarUI::class.java
    }
}