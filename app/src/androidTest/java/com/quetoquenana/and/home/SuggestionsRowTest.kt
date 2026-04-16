package com.quetoquenana.and.home

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.quetoquenana.and.features.home.ui.SuggestionsRow
import com.quetoquenana.and.util.sampleSuggestions
import org.junit.Rule
import org.junit.Test

class SuggestionsRowTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun suggestionsRow_showsSuggestions_and_clickTriggers() {
        var clickedId: String? = null

        composeTestRule.setContent {
            SuggestionsRow(suggestions = sampleSuggestions, onSuggestionClick = { id: String -> clickedId = id })
        }

        composeTestRule.onNodeWithText(text = "Helmet Discount").assertExists()
        composeTestRule.onNodeWithText(text = "10% off helmets this week").assertExists()

        composeTestRule.onNodeWithText(text = "Helmet Discount").performClick()
        assert(clickedId == "s1")
    }

    @Test
    fun suggestionsRow_empty_doesNotShowSuggestionCards() {
        composeTestRule.setContent {
            SuggestionsRow(suggestions = emptyList())
        }

        val suggestionNodes = composeTestRule.onAllNodesWithText(text = "Helmet Discount").fetchSemanticsNodes()
        assert(suggestionNodes.isEmpty())
    }
}
