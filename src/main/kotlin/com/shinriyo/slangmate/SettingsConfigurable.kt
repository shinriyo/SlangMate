package com.shinriyo.slangmate

import com.intellij.openapi.options.Configurable
import javax.swing.*
import com.intellij.openapi.components.service
import com.intellij.openapi.ui.Messages
import java.net.HttpURLConnection
import java.net.URL

class SettingsConfigurable : Configurable {
    private var settingsPanel: JPanel? = null
    private var spreadSheetIdField: JTextField? = null
    private var filePathField: JTextField? = null

    private val settings = service<PluginSettings>() // ServiceManagerの代替

    override fun createComponent(): JComponent {
        if (settingsPanel == null) { // 複数回 createComponent() が呼ばれたときの防止
            settingsPanel = JPanel()
            settingsPanel!!.layout = BoxLayout(settingsPanel, BoxLayout.Y_AXIS)

            spreadSheetIdField = JTextField(settings.spreadSheetId, 50)
            filePathField = JTextField(settings.filePath, 50)

            settingsPanel!!.add(JLabel("Google Sheets ID:"))
            settingsPanel!!.add(spreadSheetIdField)
            settingsPanel!!.add(JLabel("保存先ファイルパス:"))
            settingsPanel!!.add(filePathField)
        }

        return settingsPanel!!
    }

    override fun isModified(): Boolean {
        return spreadSheetIdField?.text != settings.spreadSheetId ||
                filePathField?.text != settings.filePath
    }

    override fun apply() {
        val newSpreadSheetId = spreadSheetIdField?.text ?: ""
        val newFilePath = filePathField?.text ?: "lib/i18n/strings.i18n.csv"

        // スプレッドシートIDのチェック
        if (!isValidSpreadsheetId(newSpreadSheetId)) {
            Messages.showErrorDialog(
                "Google Sheets ID が無効です。\n正しい ID を入力してください。",
                "エラー: 無効な Google Sheets ID"
            )
            return // 保存を中断
        }

        // ID が正しい場合のみ保存
        settings.spreadSheetId = newSpreadSheetId
        settings.filePath = newFilePath
    }

    override fun getDisplayName(): String {
        return "CSV Download Settings"
    }

    /**
     * Google Sheets ID の有効性をチェック
     * @return 正常なら true, 無効なら false
     */
    private fun isValidSpreadsheetId(spreadsheetId: String): Boolean {
        if (spreadsheetId.isBlank()) return false // 空なら無効

        val url = "https://docs.google.com/spreadsheets/d/$spreadsheetId/export?format=csv"
        return try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000 // 5秒タイムアウト
            connection.responseCode == HttpURLConnection.HTTP_OK
        } catch (e: Exception) {
            false
        }
    }
}
