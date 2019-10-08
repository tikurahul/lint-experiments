@file:Suppress("UnstableApiUsage")

package com.rahulrav

import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UClass
import org.w3c.dom.Element

class RemoveWorkManagerInitializerDetector : Detector(), XmlScanner, SourceCodeScanner {

  private var removedInitializer = false
  private var manifestLocation: Location? = null
  private var applicationImplementsProvider = false

  override fun getApplicableElements() = listOf("provider")
  override fun applicableSuperClasses() = listOf("androidx.work.Configuration.Provider")

  override fun visitElement(context: XmlContext, element: Element) {
      manifestLocation = Location.create(context.file)
      val name = element.getAttributeNS(ANDROID_NS, ATTRIBUTE_NAME)
      val remove = element.getAttributeNS(TOOLS_NS, ATTRIBUTE_NODE)

      if (name == "androidx.work.impl.WorkManagerInitializer" && remove == "remove") {
          removedInitializer = true
      }
  }

  override fun visitClass(context: JavaContext, declaration: UClass) {
    if (context.evaluator.extendsClass(
            declaration.javaPsi,
            "android.app.Application",
            false)) {
      applicationImplementsProvider = true
    }
  }

  override fun afterCheckRootProject(context: Context) {
      val location = manifestLocation ?: Location.create(context.file)
      if (applicationImplementsProvider) {
          if (!removedInitializer) {
            context.report(
                issue = IssueRegistry.RemoveWorkManagerIntializerIssue,
                location = location,
                message = IssueRegistry.RemoveWorkManagerIntializerDescription
            )
          }
      }
  }

  companion object {
      private const val ANDROID_NS = "http://schemas.android.com/apk/res/android"
      private const val TOOLS_NS = "http://schemas.android.com/tools"

      private const val ATTRIBUTE_NAME = "name"
      private const val ATTRIBUTE_NODE = "node"
  }
}
