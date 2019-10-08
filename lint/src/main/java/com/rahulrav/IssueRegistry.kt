@file:Suppress("UnstableApiUsage")

package com.rahulrav

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.*

class IssueRegistry : IssueRegistry() {
    override val issues: List<Issue>
        get() = listOf(NoisyIssue, BadConfigurationProviderIssue)

    companion object {
        private const val NoisyIssueId = "NoisyIssueId"
        const val NoisyIssueDescription = "This is a noisy issue. Feel free to ignore for now."

        private const val BadConfigurationProviderId = "BadConfigurationProviderId"
        val BadConfigurationProviderDescription = """
            Only an `android.app.Application` can implement `androidx.work.Configuration.Provider`
        """.trimIndent()

        val NoisyIssue = Issue.create(
            id = NoisyIssueId,
            briefDescription = NoisyIssueDescription,
            explanation = NoisyIssueDescription,
            category = Category.CORRECTNESS,
            priority = 4,
            severity = Severity.INFORMATIONAL,
            implementation = Implementation(NoisyDetector::class.java, Scope.MANIFEST_SCOPE)
        )

        val BadConfigurationProviderIssue = Issue.create(
            id = BadConfigurationProviderId,
            briefDescription = BadConfigurationProviderDescription,
            explanation = BadConfigurationProviderDescription,
            category = Category.CORRECTNESS,
            priority = 2,
            severity = Severity.FATAL,
            implementation = Implementation(
                BadConfigurationProviderDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
