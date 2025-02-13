package com.shinriyo.slangmate

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project

class OpenSettingsAction : AnAction() {
    init {
        templatePresentation.apply {
            description = "Open plugin settings"
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project: Project = e.project ?: return
        ShowSettingsUtil.getInstance().showSettingsDialog(project, "CSV Download Settings")
    }
}
