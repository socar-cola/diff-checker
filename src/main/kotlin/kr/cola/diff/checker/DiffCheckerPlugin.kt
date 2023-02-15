package kr.cola.diff.checker

import org.gradle.api.Project
import org.gradle.api.Plugin
import kr.cola.diff.checker.core.DiffChecker
import org.gradle.api.provider.Property

// https://peachytree.tistory.com/m/4
interface Input {
    val baseDir: Property<String>
}

class DiffCheckerPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val input = project.extensions.create("diffChecker", Input::class.java)

        project.tasks.register("diffChecker") {
            val checker = DiffChecker(input.baseDir.get())
            it.doLast {
                checker.run()
            }
        }
    }
}

