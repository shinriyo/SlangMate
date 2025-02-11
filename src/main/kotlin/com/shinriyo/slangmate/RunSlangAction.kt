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
            text = "Run Slang"
            description = "Execute slang command"
        }
        
    }
    
    override fun actionPerformed(e: AnActionEvent) {
        val project: Project = e.project ?: return

        val command = listOf("fvm", "flutter", "pub", "run", "slang")

        try {
            val processBuilder = ProcessBuilder(command)
                .directory(project.basePath?.let { java.io.File(it) }) // プロジェクトのルートで実行
                .redirectErrorStream(true) // 標準エラーも含める

            val process = processBuilder.start()

            // 結果を取得
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }
            val exitCode = process.waitFor()

            // 成功か失敗かを判定
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
