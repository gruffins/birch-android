package com.gruffins.birch

import android.util.Patterns.EMAIL_ADDRESS

class EmailScrubber: Scrubber {
    companion object {
        val REGEX = EMAIL_ADDRESS.toRegex()
        const val REPLACEMENT = "[FILTERED]"
    }

    override fun scrub(input: String): String {
        return input.replace(REGEX, REPLACEMENT)
    }
}