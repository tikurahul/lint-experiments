@file:Suppress("UnstableApiUsage")

package com.rahulrav

import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Location
import com.android.tools.lint.detector.api.SourceCodeScanner
import com.rahulrav.IssueRegistry.Companion.BadConfigurationProviderDescription
import org.jetbrains.uast.UClass

class BadConfigurationProviderDetector : Detector(), SourceCodeScanner {
  override fun applicableSuperClasses() = listOf("androidx.work.Configuration.Provider")

  override fun visitClass(context: JavaContext, declaration: UClass) {
    if (!declaration.isInterface) {
      if (!context.evaluator.extendsClass(
              declaration.javaPsi,
              "android.app.Application",
              false)) {
        context.report(
            issue = IssueRegistry.BadConfigurationProviderIssue,
            location = Location.create(context.file),
            message = BadConfigurationProviderDescription
        )
      }
    }
  }
}
