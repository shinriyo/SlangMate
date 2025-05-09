package com.shinriyo.slangmate

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.File

class RunSlangAction : AnAction() {
    companion object {
        private const val FVM = "fvm"
        private const val FLUTTER = "flutter"
    }

    init {
        templatePresentation.apply {
            description = "Execute slang command"
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project: Project = e.project ?: return

        // check if FVM is used
        val useFvm = PluginSettings.getInstance().useFvm
        val command = if (useFvm) {
            listOf(FVM, FLUTTER, "pub", "run", "slang")
        } else {
            listOf(FLUTTER, "pub", "run", "slang")
        }

        try {
            val projectBasePath = project.basePath?.let { File(it) } ?: return

            // Check if flutter or fvm is available in the system PATH
            val requiredCommand = if (useFvm) FVM else FLUTTER
            if (!isCommandInPath(requiredCommand)) {
                Messages.showErrorDialog(
                    project,
                    SlangMateBundle.message("error.command.not.found", requiredCommand),
                    SlangMateBundle.message("error.command.not.found.title", requiredCommand)
                )
                return
            }

            val processBuilder = ProcessBuilder(command)
                .directory(projectBasePath) // execute in project root
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
                    SlangMateBundle.message("success.run", output),
                    SlangMateBundle.message("success.title")
                )
            } else {
                Messages.showErrorDialog(
                    SlangMateBundle.message("error.run"),
                    SlangMateBundle.message("error.title")
                )
            }
        } catch (ex: Exception) {
            Messages.showErrorDialog("An error occurred: ${ex.message}", "Error")
        }
    }

    private fun isCommandInPath(command: String): Boolean {
        return try {
            val process = ProcessBuilder("which", command).start()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val result = reader.readLine()
            process.waitFor()
            !result.isNullOrBlank()
        } catch (e: Exception) {
            false
        }
    }
}
