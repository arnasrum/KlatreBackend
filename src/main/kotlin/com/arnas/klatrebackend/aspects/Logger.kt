package com.arnas.klatrebackend.aspects

import org.apache.logging.log4j.LogManager
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.stereotype.Component

class Logger {

    //private val logger = LoggerFactory.getLogger(Logger::class.java)
    private val logger = LogManager.getLogger()

    @Pointcut("execution(* com.arnas.klatrebackend.controllers.*.*(..))")
    fun controllerMethodPointcut() {}

    @Around("controllerMethodPointcut()")
    fun logControllerMethod(joinPoint: ProceedingJoinPoint): Any? {
        val className = joinPoint.signature.declaringTypeName
        val methodName = joinPoint.signature.name
        val args = joinPoint.args.joinToString(", ") { it.toString() }

        //logger.info("Entering method: {}.{} with args: [{}]", className, methodName, args)

        val startTime = System.currentTimeMillis()
        try {
            val result = joinPoint.proceed() // Execute the intercepted method
            val endTime = System.currentTimeMillis()
            val duration = endTime - startTime

            //logger.info("Exiting method: {}.{} with result: {} (took {}ms)", className, methodName, result, duration)
            return result
        } catch (e: Throwable) {
            val endTime = System.currentTimeMillis()
            val duration = endTime - startTime
            logger.error("Exception in method: {}.{} (took {}ms)", className, methodName, duration, e)
            throw e // Re-throw the exception to maintain normal error handling
        }
    }

}