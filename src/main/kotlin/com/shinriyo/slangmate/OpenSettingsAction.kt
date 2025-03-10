package com.shinriyo.slangmate

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project

class OpenSettingsAction : AnAction() {
    init {
        templatePresentation.apply {
            text = SlangMateBundle.message("action.settings")
            description = SlangMateBundle.message("action.settings.description")
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project: Project = e.project ?: return
        ShowSettingsUtil.getInstance().showSettingsDialog(project, "CSV Download Settings")
    }
}
