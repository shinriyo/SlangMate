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
            description = "Download CSV file from Google Sheets"
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
                "Google Sheets ID が設定されていません。\n設定画面を開きますか？",
                "エラー: Google Sheets ID 未設定",
                "はい",
                "いいえ",
                Messages.getErrorIcon()
            )
        
            if (result == Messages.YES) {
                ShowSettingsUtil.getInstance().showSettingsDialog(project, "CSV Download Settings")
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

                Messages.showInfoMessage("CSVファイルが保存されました: $fullFilePath", "成功")
            } else {
                Messages.showErrorDialog("CSVダウンロードに失敗しました。\nHTTPステータス: ${connection.responseCode}",
                    "エラー: ダウンロード失敗")
            }
        } catch (ex: Exception) {
            Messages.showErrorDialog("エラーが発生しました: ${ex.message}", "エラー: 例外発生")
        }
    }
}
