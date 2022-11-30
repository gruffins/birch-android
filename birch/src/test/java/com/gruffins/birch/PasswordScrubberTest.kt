package com.gruffins.birch

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PasswordScrubberTest {

    lateinit var scrubber: PasswordScrubber

    @Before
    fun setup() {
        scrubber = PasswordScrubber()
    }

    @Test
    fun `scrub() removes passwords`() {
        val input = "password=@BcopBT4nSDRuDL!"
        assert(scrubber.scrub(input) == "password=[FILTERED]")
    }

    @Test
    fun `scrub() is case insensitive`() {
        val input = "PASSWORD=Bs%%yY@7MnVQRooR"
        assert(scrubber.scrub(input) == "PASSWORD=[FILTERED]")
    }

    @Test
    fun `scrub() works for url params`() {
        val input = "https://birch.ryanfung.com/auth?username=test123&password=password123"
        assert(scrubber.scrub(input) == "https://birch.ryanfung.com/auth?username=test123&password=[FILTERED]")
    }
}