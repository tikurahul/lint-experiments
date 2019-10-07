package com.rahulrav

import com.android.tools.lint.checks.infrastructure.TestFiles.manifest
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.rahulrav.IssueRegistry.Companion.NoisyIssue
import org.junit.Test

class NoisyDetectorTest {
    @Test
    fun testNoisyDetector() {
        lint()
            .files(
                manifest(
                    """
                    <manifest package="test">
                    </manifest>
                    """.trimIndent()
                ).indented()
            )
            .allowMissingSdk()
            .issues(NoisyIssue)
            .run()
            .expect(
                """
                AndroidManifest.xml: Information: This is a noisy issue. Feel free to ignore for now. [NoisyIssueId]
                0 errors, 0 warnings
                """.trimIndent()
            )
    }
}
