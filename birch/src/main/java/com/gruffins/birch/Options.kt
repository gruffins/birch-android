package com.gruffins.birch

class Options {
    var scrubbers: List<Scrubber> = listOf(PasswordScrubber(), EmailScrubber())
    var host: String = "birch.ryanfung.com"
    var defaultLevel: Level = Level.TRACE
}