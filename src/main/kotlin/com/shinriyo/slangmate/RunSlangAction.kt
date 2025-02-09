package com.shinriyo.slangmate

import com.intellij.execution.ExecutionManager
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.execution.impl.ConsoleViewImpl
import com.intellij.openapi.project.Project
import java.io.OutputStream
import com.intellij.execution.process.OSProcessHandler

@Suppress("DialogTitleCapitalization")
class RunSlangAction : AnAction() {
    init {
        templatePresentation.text = "Run slang"
        templatePresentation.description = "Run slang command"
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project: Project = e.project ?: return

        val command = listOf("fvm", "flutter", "pub", "run", "slang")

        try {
            val commandLine = GeneralCommandLine(command)
                .withWorkDirectory(project?.basePath) // プロジェクトのルートディレクトリで実行

            val processHandler = OSProcessHandler(commandLine)
            val consoleView: ConsoleView = ConsoleViewImpl(project, false)
            consoleView.print("> ${command.joinToString(" ")}\n", ConsoleViewContentType.SYSTEM_OUTPUT)

            consoleView.attachToProcess(processHandler)
            processHandler.startNotify()

        } catch (ex: Exception) {
            println("エラー: ${ex.message}")
        }
    }
}
