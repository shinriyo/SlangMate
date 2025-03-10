package com.shinriyo.slangmate

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.options.ShowSettingsUtil
import java.io.File
import java.net.HttpURLConnection
import java.net.URI

class DownloadCsvAction : AnAction() {
    init {
        templatePresentation.apply {
            text = SlangMateBundle.message("action.download")
            description = SlangMateBundle.message("action.download.description")
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project: Project = e.project ?: return

        // get spreadSheetId and filePath from settings
        val spreadSheetId = PluginSettings.getInstance().spreadSheetId
        val filePath = PluginSettings.getInstance().filePath

       // if spreadSheetId is blank, show "Yes" "No" dialog
       if (spreadSheetId.isBlank()) {
            val result = Messages.showYesNoDialog(
                project,
                SlangMateBundle.message("error.no.sheet.id"),
                SlangMateBundle.message("error.no.sheet.id.title"),
                SlangMateBundle.message("dialog.yes"),
                SlangMateBundle.message("dialog.no"),
                Messages.getErrorIcon()
            )
        
            if (result == Messages.YES) {
                ShowSettingsUtil.getInstance().showSettingsDialog(project, SlangMateBundle.message("settings.title"))
            }
            return
        }

        val url = "https://docs.google.com/spreadsheets/d/$spreadSheetId/export?format=csv"

        try {
            val connection = URI(url).toURL().openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val csvData = connection.inputStream.bufferedReader().readText()

                // get project base path
                val projectBasePath = project.basePath ?: return
                val fullFilePath = "$projectBasePath/$filePath"

                val file = File(fullFilePath)
                file.parentFile.mkdirs()
                file.writeText(csvData)

                Messages.showInfoMessage(
                    SlangMateBundle.message("success.download", fullFilePath),
                    SlangMateBundle.message("success.title")
                )
            } else {
                Messages.showErrorDialog(
                    SlangMateBundle.message("error.download.failed", connection.responseCode),
                    SlangMateBundle.message("error.download.failed.title")
                )
            }
        } catch (ex: Exception) {
            Messages.showErrorDialog(
                SlangMateBundle.message("error.exception", ex.message ?: SlangMateBundle.message("error.unknown")),
                SlangMateBundle.message("error.exception.title")
            )
        }
    }
}
