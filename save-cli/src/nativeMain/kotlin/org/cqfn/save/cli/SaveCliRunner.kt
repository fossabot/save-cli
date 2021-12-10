/**
 * Main entry point for SAVE CLI execution
 */

package org.cqfn.save.cli

import org.cqfn.save.core.Save
import org.cqfn.save.core.logging.isTimeStampsEnabled

import okio.FileSystem

fun main(args: Array<String>) {
    val config = createConfigFromArgs(args)
    // FixMe: This is temporary for experiments
    isTimeStampsEnabled = true
    Save(config, FileSystem.SYSTEM)
        .performAnalysis()
}
