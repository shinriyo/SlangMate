package com.shinriyo.slangmate

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import java.io.BufferedReader
import java.io.InputStreamReader

class RunSlangAction : AnAction() {
    init {
        templatePresentation.apply {
            description = "Execute slang command"
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project: Project = e.project ?: return

        // 🔥 FVMの有無をチェック
        val useFvm = PluginSettings.getInstance().useFvm
        val command = if (useFvm) {
            listOf("fvm", "flutter", "pub", "run", "slang")
        } else {
            listOf("flutter", "pub", "run", "slang")
        }

        try {
            val processBuilder = ProcessBuilder(command)
                .directory(project.basePath?.let { java.io.File(it) }) // execute in project root
                .redirectErrorStream(true) // include standard error

            val process = processBuilder.start()

            // get result
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }
            val exitCode = process.waitFor()

            // check success or failure
            if (exitCode == 0) {
                Messages.showInfoMessage(
                    project,
                    "Slang execution completed successfully.\n${output}",
                    "Success"
                )
            } else {
                Messages.showErrorDialog(
                    "Slang execution failed.\nClick 'Details' to see full error output.",
                    "Error"
                )
            }
        } catch (ex: Exception) {
            Messages.showErrorDialog("An error occurred: ${ex.message}", "Error")
        }
    }
}
