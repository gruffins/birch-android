package com.gruffins.birch

class PasswordScrubber: Scrubber {
    companion object {
        val REGEX = "(password)=[^&#]*".toRegex(RegexOption.IGNORE_CASE)
        const val REPLACEMENT = "$1=[FILTERED]"
    }

    override fun scrub(input: String): String {
        return input.replace(REGEX, REPLACEMENT)
    }
}