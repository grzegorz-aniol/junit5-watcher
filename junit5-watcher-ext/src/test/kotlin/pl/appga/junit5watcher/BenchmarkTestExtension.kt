package pl.appga.junit5watcher

import java.lang.reflect.InvocationTargetException
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import org.slf4j.LoggerFactory
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.findAnnotations
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.valueParameters

/**
 * Extension to test results generated by BenchmarkExtension.
 *
 * <code>BenchmarkTestExtension</code> should be registered __before_ extension under test
 * - then it doesn't affect results measured by <code>BenchmarkExtension</code>
 *
 */
internal class BenchmarkTestExtension : ParameterResolver, AfterAllCallback {
    private val log = LoggerFactory.getLogger(BenchmarkTestExtension::class.java)
    private val contextNamespace = ExtensionContext.Namespace.create(BenchmarkExtension::class.java)

    override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean {
        val type = parameterContext.parameter.type
        return type == ExtensionContext::class.java ||
                type == Metrics::class.java ||
                type == TestClassCounters::class.java
    }

    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any {
        return when (parameterContext.parameter.type) {
            ExtensionContext::class.java -> extensionContext
            Metrics::class.java -> extensionContext.root.getStore(contextNamespace).get(Metrics::class.java)
            TestClassCounters::class.java -> extensionContext.getStore(contextNamespace).get(TestClassCounters::class.java)
            else -> throw RuntimeException("Unsupported parameter")
        }
    }

    override fun afterAll(context: ExtensionContext) {
        val companion = context.testClass.get().kotlin.companionObject
        val instance = context.testClass.get().kotlin.companionObjectInstance
        companion?.members?.forEach { member ->
            if (member.findAnnotations(TestFinalization::class).isNotEmpty()) {
                if (member.valueParameters.size == 2) {
                    try {
                        member.call(
                            instance, context.root.getStore(contextNamespace).get(Metrics::class.java),
                            context.getStore(contextNamespace).get(TestClassCounters::class.java)
                        )
                    } catch (ex: InvocationTargetException) {
                        log.error("Cannot invoke @TestFinalization annotated method", ex.cause)
                        ex.cause?.let { throw it }
                    }
                }
            }
        }
    }
}