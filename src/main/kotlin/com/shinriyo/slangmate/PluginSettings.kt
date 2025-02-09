package com.shinriyo.slangmate

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service

@State(name = "CsvDownloadSettings", storages = [Storage("csv_download_settings.xml")])
class PluginSettings : PersistentStateComponent<PluginSettings.State> {

    var spreadSheetId: String = ""  // デフォルトを空にする
    var filePath: String = "lib/i18n/strings.i18n.csv"  // デフォルトの保存先

    class State {
        var spreadSheetId: String = ""  // デフォルトを空にする
        var filePath: String = "lib/i18n/strings.i18n.csv"
    }

    override fun getState(): State {
        return State().apply {
            spreadSheetId = this@PluginSettings.spreadSheetId
            filePath = this@PluginSettings.filePath
        }
    }

    override fun loadState(state: State) {
        spreadSheetId = state.spreadSheetId
        filePath = state.filePath
    }

    companion object {
        fun getInstance(): PluginSettings = service()
    }
}
