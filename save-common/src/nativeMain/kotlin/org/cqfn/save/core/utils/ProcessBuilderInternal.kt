@file:Suppress("FILE_WILDCARD_IMPORTS")

package org.cqfn.save.core.utils

import org.cqfn.save.core.logging.logTrace

import okio.Path
import platform.posix.system

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking

@Suppress("MISSING_KDOC_TOP_LEVEL",
    "MISSING_KDOC_CLASS_ELEMENTS",
    "MISSING_KDOC_ON_FUNCTION"
)
actual class ProcessBuilderInternal actual constructor(
    private val stdoutFile: Path,
    private val stderrFile: Path,
    private val useInternalRedirections: Boolean
) {
    actual fun prepareCmd(command: String) = if (useInternalRedirections) {
        "($command) >$stdoutFile 2>$stderrFile"
    } else {
        command
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    actual fun exec(
        cmd: String,
        timeOutMillis: Long
    ): Int {
        var status = -1

        runBlocking {
            val timeOut = async(newSingleThreadContext("timeOut")) {
                logTrace("Starting to measure time for process `$cmd` execution, timeout is set to $timeOutMillis ms")
                delay(timeOutMillis)
                logTrace("Timeout is reached, will try to destroy the process `$cmd`")
                destroy(cmd)
                throw ProcessTimeoutException(timeOutMillis, "Timeout is reached: $timeOutMillis")
            }

            val command = async {
                logTrace("Starting process `$cmd` using `system(...)`")
                status = system(cmd)
                logTrace("Process `$cmd` has completed with code $status, will now cancel monitoring coroutine")
                timeOut.cancel()
            }
            joinAll(timeOut, command)
        }

        if (status == -1) {
            error("Couldn't execute $cmd, exit status: $status")
        }
        return status
    }

    private fun destroy(cmd: String) {
        val killCmd = if (isCurrentOsWindows()) {
            "taskkill /im \"$cmd\" /f"
        } else {
            "pkill \"$cmd\""
        }
        logTrace("Destroying process using command `$killCmd`")
        system(killCmd)
        logTrace("Finished destroying process using command `$killCmd`")
    }
}
