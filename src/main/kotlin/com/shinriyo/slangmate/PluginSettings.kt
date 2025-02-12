package com.shinriyo.slangmate

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service

@State(name = "CsvDownloadSettings", storages = [Storage("csv_download_settings.xml")])
class PluginSettings : PersistentStateComponent<PluginSettings.State> {

    var spreadSheetId: String = ""  // default is empty
    var filePath: String = "lib/i18n/strings.i18n.csv"  // default save path
    var useFvm: Boolean = false  // 🔥 追加: FVMを使うかどうか

    class State {
        var spreadSheetId: String = ""  // default is empty
        var filePath: String = "lib/i18n/strings.i18n.csv"
        var useFvm: Boolean = false  // 🔥 追加: チェックボックスの状態
    }

    override fun getState(): State {
        return State().apply {
            spreadSheetId = this@PluginSettings.spreadSheetId
            filePath = this@PluginSettings.filePath
            useFvm = this@PluginSettings.useFvm
        }
    }

    override fun loadState(state: State) {
        spreadSheetId = state.spreadSheetId
        filePath = state.filePath
        useFvm = state.useFvm
    }

    companion object {
        fun getInstance(): PluginSettings = service()
    }
}
