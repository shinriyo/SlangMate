package com.shinriyo.slangmate

import com.intellij.openapi.options.Configurable
import javax.swing.*
import com.intellij.openapi.ui.Messages
import java.net.HttpURLConnection
import java.net.URI
import java.awt.*
import com.intellij.ide.BrowserUtil

class SettingsConfigurable : Configurable {
    private var settingsPanel: JPanel? = null
    private var spreadSheetIdField: JTextField? = null
    private var filePathField: JTextField? = null
    private var useFvmCheckbox: JCheckBox? = null  // add FVM checkbox
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
            settingsPanel!!.add(JLabel(SlangMateBundle.message("settings.spreadsheet.id")), gbc)

            val idPanel = JPanel(FlowLayout(FlowLayout.LEFT, 0, 0))
            spreadSheetIdField = JTextField(settings.spreadSheetId).apply {
                preferredSize = Dimension(250, preferredSize.height)
            }
            idPanel.add(spreadSheetIdField)

            val openButton = JButton(SlangMateBundle.message("settings.open.sheet")).apply {
                addActionListener {
                    val id = spreadSheetIdField?.text?.trim() ?: ""
                    if (id.isNotEmpty()) {
                        BrowserUtil.browse("https://docs.google.com/spreadsheets/d/$id")
                    }
                }
            }
            idPanel.add(openButton)

            gbc.gridx = 1
            gbc.weightx = 1.0
            settingsPanel!!.add(idPanel, gbc)

            gbc.gridx = 0
            gbc.gridy = 1
            gbc.weightx = 0.0
            settingsPanel!!.add(JLabel(SlangMateBundle.message("settings.file.path")), gbc)

            gbc.gridx = 1
            gbc.weightx = 1.0
            filePathField = JTextField(settings.filePath).apply {
                preferredSize = Dimension(300, preferredSize.height)
            }
            settingsPanel!!.add(filePathField, gbc)

            // fvm checkbox
            gbc.gridx = 0
            gbc.gridy = 2
            gbc.weightx = 0.0
            settingsPanel!!.add(JLabel(SlangMateBundle.message("settings.use.fvm")), gbc)

            gbc.gridx = 1
            gbc.weightx = 1.0
            useFvmCheckbox = JCheckBox("", settings.useFvm)  // get the state from settings
            settingsPanel!!.add(useFvmCheckbox, gbc)

            // fvm description
            gbc.gridx = 1
            gbc.gridy = 3
            gbc.weightx = 1.0
            val fvmDescription = JLabel(SlangMateBundle.message("settings.fvm.description"))
            fvmDescription.font = fvmDescription.font.deriveFont(Font.ITALIC)
            settingsPanel!!.add(fvmDescription, gbc)

            // push the rest of the space to the bottom
            gbc.gridx = 0
            gbc.gridy = 4
            gbc.gridwidth = 2
            gbc.weighty = 1.0
            settingsPanel!!.add(Box.createVerticalGlue(), gbc)
        }

        return settingsPanel!!
    }

    override fun isModified(): Boolean {
        return spreadSheetIdField?.text?.trim() != settings.spreadSheetId ||
                filePathField?.text?.trim() != settings.filePath ||
                useFvmCheckbox?.isSelected != settings.useFvm  // compare FVM checkbox
    }

    override fun apply() {
        val newSpreadSheetId = spreadSheetIdField?.text?.trim() ?: ""
        val newFilePath = filePathField?.text?.trim() ?: ""
        val newUseFvm = useFvmCheckbox?.isSelected ?: false  // the value of the checkbox

        // check spread sheet id
        if (!isValidSpreadsheetId(newSpreadSheetId)) {
            Messages.showErrorDialog(
                SlangMateBundle.message("error.invalid.id"),
                SlangMateBundle.message("error.title")
            )
            return // stop saving
        }

        // save settings                                                                                      
        settings.spreadSheetId = newSpreadSheetId
        settings.filePath = newFilePath
        settings.useFvm = newUseFvm  // save FVM settings
    }

    override fun getDisplayName(): String = SlangMateBundle.message("settings.title")

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
