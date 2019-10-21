@file:Suppress("UnstableApiUsage")

package com.rahulrav

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.*
import java.util.*

class IssueRegistry : IssueRegistry() {
    override val issues: List<Issue>
        get() = listOf(
                NoisyIssue,
                BadConfigurationProviderIssue,
                RemoveWorkManagerIntializerIssue,
                ExperimentalDetector.ISSUE,
                LogWtfDetector.ISSUE
        )

    companion object {
        private const val NoisyIssueId = "NoisyIssueId"
        const val NoisyIssueDescription = "This is a noisy issue. Feel free to ignore for now."

        private const val BadConfigurationProviderId = "BadConfigurationProviderId"
        val BadConfigurationProviderDescription = """
            Only an `android.app.Application` can implement `androidx.work.Configuration.Provider`
        """.trimIndent()

        private const val RemoveWorkManagerIntializerId = "RemoveWorkManagerIntializerId"
        val RemoveWorkManagerIntializerDescription = """
            If an android.app.Application implements androidx.work.Configuration.Provider, 
            the default androidx.work.impl.WorkManagerInitializer needs to be removed from tne
            AndroidManifest.xml file.
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

        val RemoveWorkManagerIntializerIssue = Issue.create(
                id = RemoveWorkManagerIntializerId,
                briefDescription = RemoveWorkManagerIntializerDescription,
                explanation = RemoveWorkManagerIntializerDescription,
                category = Category.CORRECTNESS,
                priority = 2,
                severity = Severity.FATAL,
                implementation = Implementation(
                        RemoveWorkManagerInitializerDetector::class.java,
                        EnumSet.of(Scope.JAVA_FILE, Scope.MANIFEST))
        )
    }
}
