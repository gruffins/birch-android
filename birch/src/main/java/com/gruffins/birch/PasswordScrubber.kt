package com.gruffins.birch

class PasswordScrubber: Scrubber {
    companion object {
        val KVP_REGEX = "(password)=[^&#]*".toRegex(RegexOption.IGNORE_CASE)
        val JSON_REGEX = "(\"password\":\\s?)\".*\"".toRegex(RegexOption.IGNORE_CASE)
        const val KVP_REPLACEMENT = "$1=[FILTERED]"
        const val JSON_REPLACEMENT = "$1\"[FILTERED]\""
    }

    override fun scrub(input: String): String {
        return input
            .replace(KVP_REGEX, KVP_REPLACEMENT)
            .replace(JSON_REGEX, JSON_REPLACEMENT)
    }
}