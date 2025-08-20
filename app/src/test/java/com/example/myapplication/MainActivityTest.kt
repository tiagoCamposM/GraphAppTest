package com.example.myapplication

import android.widget.TextView
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class MainActivityTest {

    @Test
    fun testTextViewContent() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).setup().get()
        val textView = activity.findViewById<TextView>(R.id.textView)
        assertEquals("Hello World!", textView.text)
    }
}
