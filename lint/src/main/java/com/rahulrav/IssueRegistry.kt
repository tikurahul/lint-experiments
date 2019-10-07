@file:Suppress("UnstableApiUsage")

package com.rahulrav

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.*

class IssueRegistry : IssueRegistry() {
    override val issues: List<Issue>
        get() = listOf(NoisyIssue)

    companion object {
        private const val NoisyIssueId = "NoisyIssueId"
        const val NoisyIssueDescription = "This is a noisy issue. Feel free to ignore for now."

        val NoisyIssue = Issue.create(
                id = NoisyIssueId,
                briefDescription = NoisyIssueDescription,
                explanation = NoisyIssueDescription,
                category = Category.CORRECTNESS,
                priority = 4,
                severity = Severity.INFORMATIONAL,
                implementation = Implementation(NoisyDetector::class.java, Scope.MANIFEST_SCOPE)
        )
    }
}
