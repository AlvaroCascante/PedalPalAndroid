package com.quetoquenana.and.bikes

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.quetoquenana.and.features.bikes.domain.model.Bike
import com.quetoquenana.and.features.bikes.domain.model.BikeComponent
import com.quetoquenana.and.features.bikes.domain.model.BikeType
import com.quetoquenana.and.features.bikes.ui.BikeComponentOptionsScreen
import com.quetoquenana.and.features.bikes.ui.BikeDetailScreen
import com.quetoquenana.and.features.bikes.ui.BikeDetailUiState
import com.quetoquenana.and.features.bikes.ui.BikesScreen
import com.quetoquenana.and.features.bikes.ui.BikesUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class BikesScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun bikesScreen_filtersByTypeChip() {
        composeTestRule.setContent {
            var selectedType by remember { mutableStateOf<BikeType?>(null) }

            BikesScreen(
                uiState = BikesUiState(
                    bikes = sampleBikes,
                    selectedType = selectedType
                ),
                onTypeSelected = { type -> selectedType = type }
            )
        }

        composeTestRule.onNodeWithText("Trek Domane").assertIsDisplayed()
        composeTestRule.onNodeWithText("Canyon Grizl").assertIsDisplayed()

        composeTestRule.onNodeWithText("Gravel").performClick()

        composeTestRule.onNodeWithText("Canyon Grizl").assertIsDisplayed()
        val roadBikeNodes = composeTestRule.onAllNodesWithText("Trek Domane").fetchSemanticsNodes()
        assertTrue(roadBikeNodes.isEmpty())
    }

    @Test
    fun bikesScreen_bikeClickTriggersSelectedBikeId() {
        var clickedBikeId: String? = null

        composeTestRule.setContent {
            BikesScreen(
                uiState = BikesUiState(bikes = sampleBikes),
                onBikeClick = { id -> clickedBikeId = id }
            )
        }

        composeTestRule.onNodeWithText("Trek Domane").performClick()

        assertEquals("road-bike", clickedBikeId)
    }

    @Test
    fun bikeDetailScreen_componentAndHistoryClicksTriggerCallbacks() {
        val bike = sampleBikes.first()
        var historyBikeId: String? = null
        var selectedComponent: Pair<String, String>? = null

        composeTestRule.setContent {
            BikeDetailScreen(
                uiState = BikeDetailUiState(bike = bike),
                onHistoryClick = { clickedBike -> historyBikeId = clickedBike.id },
                onComponentClick = { clickedBike, component ->
                    selectedComponent = clickedBike.id to component.id
                }
            )
        }

        composeTestRule.onNodeWithText("View bike history").performClick()
        composeTestRule.onNodeWithText("Shimano 105").performClick()

        assertEquals("road-bike", historyBikeId)
        assertEquals("road-bike" to "component-1", selectedComponent)
    }

    @Test
    fun componentOptions_newComponentAllowsAddOnly() {
        composeTestRule.setContent {
            BikeComponentOptionsScreen(
                bikeId = "road-bike",
                componentId = "new"
            )
        }

        composeTestRule.onNodeWithText("Add component").performClick()

        composeTestRule.onNodeWithText("Add component selected. The next step is wiring the form for this action.")
            .assertExists()
        composeTestRule.onNodeWithText("Select an existing component first.").assertExists()
    }

    @Test
    fun componentOptions_existingComponentAllowsUpdateAndReplace() {
        composeTestRule.setContent {
            BikeComponentOptionsScreen(
                bikeId = "road-bike",
                componentId = "component-1"
            )
        }

        composeTestRule.onNodeWithText("Update component").performClick()
        composeTestRule.onNodeWithText("Update component selected. The next step is wiring the form for this action.")
            .assertExists()

        composeTestRule.onNodeWithText("Replace component").performClick()
        composeTestRule.onNodeWithText("Replace component selected. The next step is wiring the form for this action.")
            .assertExists()
        composeTestRule.onAllNodesWithText("Select an existing component first.").fetchSemanticsNodes().let { nodes ->
            assertTrue(nodes.isEmpty())
        }
    }
}

private val sampleBikes = listOf(
    Bike(
        id = "road-bike",
        name = "Trek Domane",
        type = "ROAD",
        status = "ACTIVE",
        isPublic = false,
        isExternalSync = false,
        brand = "Trek",
        model = "Domane AL 2",
        year = 2024,
        serialNumber = "ABC123",
        notes = "Daily road bike",
        odometerKm = 120.0,
        usageTimeMinutes = 600,
        externalGearId = null,
        externalSyncProvider = "",
        components = listOf(
            BikeComponent(
                id = "component-1",
                type = "DRIVETRAIN",
                name = "Shimano 105",
                status = "ACTIVE",
                brand = "Shimano",
                model = "105",
                notes = null,
                odometerKm = 120,
                usageTimeMinutes = 600
            )
        )
    ),
    Bike(
        id = "gravel-bike",
        name = "Canyon Grizl",
        type = "GRAVEL",
        status = "ACTIVE",
        isPublic = false,
        isExternalSync = false,
        brand = "Canyon",
        model = "Grizl",
        year = 2023,
        serialNumber = null,
        notes = null,
        odometerKm = 80.0,
        usageTimeMinutes = 360,
        externalGearId = null,
        externalSyncProvider = "",
        components = emptyList()
    )
)
