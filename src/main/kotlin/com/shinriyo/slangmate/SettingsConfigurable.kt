package com.shinriyo.slangmate

import com.intellij.openapi.options.Configurable
import javax.swing.*

class SettingsConfigurable : Configurable {
    private var settingsPanel: JPanel? = null
    private var spreadSheetIdField: JTextField? = null
    private var filePathField: JTextField? = null

    override fun createComponent(): JComponent {
        settingsPanel = JPanel()
        settingsPanel!!.layout = BoxLayout(settingsPanel, BoxLayout.Y_AXIS)

        spreadSheetIdField = JTextField(PluginSettings.instance.spreadSheetId, 50)
        filePathField = JTextField(PluginSettings.instance.filePath, 50)

        settingsPanel!!.add(JLabel("Google Sheets ID:"))
        settingsPanel!!.add(spreadSheetIdField)
        settingsPanel!!.add(JLabel("保存先ファイルパス:"))
        settingsPanel!!.add(filePathField)

        return settingsPanel!!
    }

    override fun isModified(): Boolean {
        return spreadSheetIdField!!.text != PluginSettings.instance.spreadSheetId ||
                filePathField!!.text != PluginSettings.instance.filePath
    }

    override fun apply() {
        PluginSettings.instance.spreadSheetId = spreadSheetIdField!!.text
        PluginSettings.instance.filePath = filePathField!!.text
    }

    override fun getDisplayName(): String {
        return "CSV Download Settings"
    }
}
