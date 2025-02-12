package com.shinriyo.slangmate

import com.intellij.openapi.options.Configurable
import javax.swing.*
import com.intellij.openapi.components.service
import com.intellij.openapi.ui.Messages
import java.net.HttpURLConnection
import java.net.URI
import java.awt.*

class SettingsConfigurable : Configurable {
    private var settingsPanel: JPanel? = null
    private var spreadSheetIdField: JTextField? = null
    private var filePathField: JTextField? = null
    private val settings = PluginSettings.getInstance()

    override fun createComponent(): JComponent {
        if (settingsPanel == null) {
            settingsPanel = JPanel(GridBagLayout())
            val gbc = GridBagConstraints().apply {
                fill = GridBagConstraints.HORIZONTAL
                insets = Insets(0, 5, 5, 5)
                anchor = GridBagConstraints.NORTHWEST
            }

            gbc.gridx = 0
            gbc.gridy = 0
            gbc.weightx = 0.0
            gbc.weighty = 0.0
            settingsPanel!!.add(JLabel("Google Sheets ID:"), gbc)

            gbc.gridx = 1
            gbc.weightx = 1.0
            spreadSheetIdField = JTextField(settings.spreadSheetId).apply {
                preferredSize = Dimension(300, preferredSize.height)
            }
            settingsPanel!!.add(spreadSheetIdField, gbc)

            gbc.gridx = 0
            gbc.gridy = 1
            gbc.weightx = 0.0
            settingsPanel!!.add(JLabel("保存先ファイルパス:"), gbc)

            gbc.gridx = 1
            gbc.weightx = 1.0
            filePathField = JTextField(settings.filePath).apply {
                preferredSize = Dimension(300, preferredSize.height)
            }
            settingsPanel!!.add(filePathField, gbc)

            gbc.gridx = 0
            gbc.gridy = 2
            gbc.gridwidth = 2
            gbc.weighty = 1.0
            settingsPanel!!.add(Box.createVerticalGlue(), gbc)
        }

        return settingsPanel!!
    }

    override fun isModified(): Boolean {
        return spreadSheetIdField?.text?.trim() != settings.spreadSheetId ||
               filePathField?.text?.trim() != settings.filePath
    }

    override fun apply() {
        val newSpreadSheetId = spreadSheetIdField?.text?.trim() ?: ""
        val newFilePath = filePathField?.text?.trim() ?: ""

        // check spread sheet id
        if (!isValidSpreadsheetId(newSpreadSheetId)) {                                    
            Messages.showErrorDialog(                                                     
                "Google Sheets ID が無効です。\n正しい ID を入力してください。",          
                "エラー: 無効な Google Sheets ID"                                         
            )                                                                             
            return // stop saving
        }
                                                                                          
        // save if spread sheet id is valid                                                        
        settings.spreadSheetId = newSpreadSheetId                                         
        settings.filePath = newFilePath
    }

    override fun getDisplayName(): String {
        return "CSV Download Settings"
    }

    /**
     * Chedck of valid of Google Sheets ID
     * @return true if valid, false if invalid
     */
    private fun isValidSpreadsheetId(spreadsheetId: String): Boolean {
        if (spreadsheetId.isBlank()) return false // if blank, invalid

        val url = "https://docs.google.com/spreadsheets/d/$spreadsheetId/export?format=csv"
        return try {
            val connection = URI(url).toURL().openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000 // 5 seconds timeout
            connection.responseCode == HttpURLConnection.HTTP_OK
        } catch (e: Exception) {
            false
        }
    }
}
