package com.shinriyo.slangmate

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service

@State(name = "CsvDownloadSettings", storages = [Storage("csv_download_settings.xml")])
class PluginSettings : PersistentStateComponent<PluginSettings.State> {

    companion object {
        fun getInstance(): PluginSettings = service()
        const val DEFAULT_FILE_PATH = "lib/i18n/strings.i18n.csv"
    }

    var spreadSheetId: String = ""  // default is empty
    var filePath: String = DEFAULT_FILE_PATH  // default save path
    var useFvm: Boolean = false  // use FVM or not

    class State {
        var spreadSheetId: String = ""  // default is empty
        var filePath: String = DEFAULT_FILE_PATH
        var useFvm: Boolean = false  // the state of the checkbox
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
}
