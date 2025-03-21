package com.shinriyo.slangmate

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class SlangMateToolsMenu : ActionGroup() {
    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        return arrayOf(
            DownloadCsvAction(),
            RunSlangAction(),
            OpenSettingsAction()
        )
    }
}
