package com.gruffins.birch

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EmailScrubberTest {

    lateinit var scrubber: EmailScrubber

    @Before
    fun setup() {
        scrubber = EmailScrubber()
    }

    @Test
    fun `scrub() removes emails`() {
        val input = "abcd@domain.com"
        assert(scrubber.scrub(input) == "[FILTERED]")
    }

    @Test
    fun `scrub() removes emails from urls`() {
        val input = "https://birch.ryanfung.com/user?email=valid+email@domain.com"
        assert(scrubber.scrub(input) == "https://birch.ryanfung.com/user?email=[FILTERED]")
    }

    @Test
    fun `scrub() removes emails from json`() {
        val input = "{\"email\": \"abcd@domain.com\"}"
        assert(scrubber.scrub(input) == "{\"email\": \"[FILTERED]\"}")
    }

    @Test
    fun `scrub() removes multiple emails`() {
        val input = "abcd@domain.com asdf@domain.com"
        assert(scrubber.scrub(input) == "[FILTERED] [FILTERED]")
    }
}