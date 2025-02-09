package com.shinriyo.slangmate

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.shinriyo.slangmate.PluginSettings
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

class DownloadCsvAction : AnAction("Download CSV") {
    override fun actionPerformed(e: AnActionEvent) {
        val project: Project = e.project ?: return

        // 設定からspreadSheetIDとファイルパスを取得
        val spreadSheetId = PluginSettings.instance.spreadSheetId
        val filePath = PluginSettings.instance.filePath

        // spreadSheetId が空の場合はエラーダイアログを表示
        if (spreadSheetId.isBlank()) {
            Messages.showErrorDialog("Google Sheets IDが設定されていません。\n設定画面から入力してください。",
                "エラー: Google Sheets ID 未設定")
            return
        }

        val url = "https://docs.google.com/spreadsheets/d/$spreadSheetId/export?format=csv"

        try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val csvData = connection.inputStream.bufferedReader().readText()

                // プロジェクトのベースパスを取得
                val projectBasePath = project?.basePath ?: return
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
