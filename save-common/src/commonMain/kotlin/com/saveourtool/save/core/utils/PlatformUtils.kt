/**
 * This file contains platform-dependent utils
 */

@file:Suppress("FILE_NAME_MATCH_CLASS", "MatchingDeclarationName")

package com.saveourtool.save.core.utils

import com.saveourtool.save.core.config.OutputStreamType

/**
 * Supported platforms
 */
enum class CurrentOs {
    LINUX, MACOS, UNDEFINED, WINDOWS
}

/**
 * Atomic values
 */
expect class AtomicInt(value: Int) {
    /**
     * @return value
     */
    fun get(): Int

    /**
     * @param delta increments the value_ by delta
     * @return the new value
     */
    fun addAndGet(delta: Int): Int
}

/**
 * Atomic boolean
 */
@Suppress("FUNCTION_BOOLEAN_PREFIX")
expect class AtomicBoolean(value: Boolean) {
    /**
     * @return value
     */
    fun get(): Boolean

    /**
     * @param expect expected value
     * @param update updated value
     * @return the result of the comparison
     */
    fun compareAndSet(expect: Boolean, update: Boolean): Boolean
}

/**
 *  Class that holds value and shares atomic reference to the value (native only)
 *
 *  @param valueToStore value to store
 */
@Suppress("USE_DATA_CLASS")
expect class GenericAtomicReference<T>(valueToStore: T) {
    /**
     * @return stored value
     */
    fun get(): T

    /**
     * @param newValue new value to store
     */
    fun set(newValue: T)
}

/**
 * Get type of current OS
 *
 * @return type of current OS
 */
expect fun getCurrentOs(): CurrentOs

/**
 * Checks if the current OS is windows.
 *
 * @return true if current OS is Windows
 */
fun isCurrentOsWindows(): Boolean = (getCurrentOs() == CurrentOs.WINDOWS)

/**
 * Platform specific writer
 *
 * @param msg a message string
 * @param outputType output stream (file, stdout, stderr)
 */
@Suppress("WHEN_WITHOUT_ELSE")
fun writeToStream(msg: String, outputType: OutputStreamType) {
    when (outputType) {
        OutputStreamType.STDOUT, OutputStreamType.STDERR -> writeToConsole(msg, outputType)
        OutputStreamType.FILE -> TODO("Not yet implemented")
    }
}

/**
 * Platform specific writer to console
 *
 * @param msg a message string
 * @param outputType output stream: stdout or stderr
 */
expect fun writeToConsole(msg: String, outputType: OutputStreamType)
