package com.mksoft.maknaeya_sikdang_chajara

import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the App under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("com.mksoft.maknaeya_sikdang_chajara", appContext.packageName)
    }
}
