package com.quetoquenana.and.pedalpal.features.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.quetoquenana.and.pedalpal.features.home.ui.AppointmentsRow
import com.quetoquenana.and.pedalpal.util.sampleAppointments
import org.junit.Rule
import org.junit.Test

class AppointmentsRowTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun appointmentsRow_showsAppointments_and_clickTriggers() {
        var clickedId: String? = null

        composeTestRule.setContent {
            AppointmentsRow(appointments = sampleAppointments, onAppointmentClick = { id -> clickedId = id })
        }

        // Check that texts are displayed
        composeTestRule.onNodeWithText(text = "Mon, Feb 12 · 09:00").assertIsDisplayed()
        composeTestRule.onNodeWithText(text = "Trek Domane").assertIsDisplayed()

        // Simulate click on the first appointment card by text
        composeTestRule.onNodeWithText(text = "Mon, Feb 12 · 09:00").performClick()
        assert(clickedId == "1")
    }

    @Test
    fun appointmentsRow_empty_showsEmptyState_and_clickNavigates() {
        var clicked = false
        composeTestRule.setContent {
            AppointmentsRow(appointments = emptyList(), onEmptyClick = { clicked = true })
        }

        composeTestRule.onNodeWithText(text = "No appointments yet").assertIsDisplayed()
        composeTestRule.onNodeWithText(text = "No appointments yet").performClick()
        assert(clicked)
    }
}
