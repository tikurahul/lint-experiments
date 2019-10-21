package com.rahulrav

import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression

@Suppress("UnstableApiUsage")
class LogWtfDetector : Detector(), SourceCodeScanner {

    override fun getApplicableMethodNames(): List<String>? =
            listOf(
                    "wtf"
            )

    override fun visitMethodCall(
            context: JavaContext,
            node: UCallExpression,
            method: PsiMethod
    ) {
        val evaluator = context.evaluator
        if (evaluator.isMemberInClass(method, "android.util.Log")) {
            reportUsage(context, node, method)
        }
    }

    private fun reportUsage(
            context: JavaContext,
            node: UCallExpression,
            method: PsiMethod
    ) {
        val quickfixData = LintFix.create()
                .name("Use Log.e()")
                .replace()
                .text(method.name)
                .with("e")
                .robot(true) // Can be applied automatically.
                .independent(true) // Does not conflict with other auto-fixes.
                .build()

        context.report(
                issue = ISSUE,
                scope = node,
                location = context.getCallLocation(
                        call = node,
                        includeReceiver = false,
                        includeArguments = false
                ),
                message = "Usage of `Log.wtf()` is prohibited",
                quickfixData = quickfixData
        )
    }

    companion object {
        private val IMPLEMENTATION = Implementation(
                LogWtfDetector::class.java,
                Scope.JAVA_FILE_SCOPE
        )

        val ISSUE: Issue = Issue.create(
                id = "LogWtfUsageError",
                briefDescription = "Prohibited logging level",
                explanation = """
                    This lint check prevents usage of `Log.wtf()`.
                """.trimIndent(),
                category = Category.CORRECTNESS,
                priority = 3,
                severity = Severity.ERROR,
                implementation = IMPLEMENTATION
        ).setAndroidSpecific(true)
    }
}

