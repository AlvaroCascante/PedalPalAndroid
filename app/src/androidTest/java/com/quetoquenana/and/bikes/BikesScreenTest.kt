package com.quetoquenana.and.bikes

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import com.quetoquenana.and.features.bikes.domain.model.Bike
import com.quetoquenana.and.features.bikes.domain.model.Component
import com.quetoquenana.and.features.bikes.domain.model.BikeMedia
import com.quetoquenana.and.features.bikes.domain.model.BikeType
import com.quetoquenana.and.features.bikes.ui.AddBikeComponentUiState
import com.quetoquenana.and.features.bikes.ui.BikeComponentScreen
import com.quetoquenana.and.features.bikes.ui.BikeDetailScreen
import com.quetoquenana.and.features.bikes.ui.BikeDetailUiState
import com.quetoquenana.and.features.bikes.ui.BikeMediaScreen
import com.quetoquenana.and.features.bikes.ui.BikeMediaUiState
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
    fun bikesScreen_showsTrackedUsageInsteadOfComponentCount() {
        composeTestRule.setContent {
            BikesScreen(uiState = BikesUiState(bikes = sampleBikes))
        }

        composeTestRule.onNodeWithText("120 km · 10 h tracked").assertIsDisplayed()
        composeTestRule.onAllNodesWithText("1 components · 120 km").fetchSemanticsNodes().let { nodes ->
            assertTrue(nodes.isEmpty())
        }
    }

    @Test
    fun bikeDetailScreen_componentAndHistoryClicksTriggerCallbacks() {
        val bike = sampleBikes.first()
        var historyBikeId: String? = null
        var imagesBikeId: String? = null
        var selectedComponent: Pair<String, String>? = null

        composeTestRule.setContent {
            BikeDetailScreen(
                uiState = BikeDetailUiState(bike = bike),
                onHistoryClick = { clickedBike -> historyBikeId = clickedBike.id },
                onViewImagesClick = { clickedBike -> imagesBikeId = clickedBike.id },
                onComponentClick = { clickedBike, component ->
                    selectedComponent = clickedBike.id to component.id
                }
            )
        }

        composeTestRule.onNodeWithText("Options").performClick()
        composeTestRule.onNodeWithText("View bike history").assertIsDisplayed()
        composeTestRule.onNodeWithText("View images").performClick()
        composeTestRule.onNodeWithText("View bike history").performClick()
        composeTestRule.onNodeWithText("Shimano 105").performClick()

        assertEquals("road-bike", historyBikeId)
        assertEquals("road-bike", imagesBikeId)
        assertEquals("road-bike" to "component-1", selectedComponent)
    }

    @Test
    fun bikeDetailScreen_optionButtonFlipsCardAndBackRestoresInfo() {
        composeTestRule.setContent {
            BikeDetailScreen(uiState = BikeDetailUiState(bike = sampleBikes.first()))
        }

        composeTestRule.onNodeWithText("Options").assertIsDisplayed()
        composeTestRule.onNodeWithText("Options").performClick()

        composeTestRule.onNodeWithText("Bike options").assertIsDisplayed()
        composeTestRule.onNodeWithText("View images").assertIsDisplayed()
        composeTestRule.onNodeWithText("Back to info").assertIsDisplayed()

        composeTestRule.onNodeWithText("Back to info").performClick()
        composeTestRule.onNodeWithText("Options").assertIsDisplayed()
    }

    @Test
    fun bikeDetailScreen_componentsRowCanScrollHorizontally() {
        composeTestRule.setContent {
            BikeDetailScreen(uiState = BikeDetailUiState(bike = sampleBikes.first()))
        }

        composeTestRule
            .onNodeWithTag("bike-components-row")
            .performScrollToNode(hasText("Fox 34"))

        composeTestRule.onNodeWithText("Fox 34").assertIsDisplayed()
    }

    @Test
    fun bikeMediaScreen_showsImageCards() {
        composeTestRule.setContent {
            BikeMediaScreen(
                uiState = BikeMediaUiState(
                    media = listOf(
                        BikeMedia(
                            id = "media-1",
                            contentType = "IMAGE_PNG",
                            provider = "Cloudflare",
                            isPrimary = false,
                            status = "Active",
                            name = "SecondBikeImage",
                            altText = "Alt text",
                            url = "https://example.com/image-1.png",
                            expiresAt = "2026-05-15T03:28:49Z"
                        )
                    )
                )
            )
        }

        composeTestRule.onNodeWithText("Bike images").assertIsDisplayed()
        composeTestRule.onNodeWithText("SecondBikeImage").assertIsDisplayed()
        composeTestRule.onNodeWithText("Alt text").assertIsDisplayed()
    }

    @Test
    fun componentScreen_newComponentShowsAddForm() {
        composeTestRule.setContent {
            BikeComponentScreen(
                bikeId = "road-bike",
                componentId = "new",
                uiState = AddBikeComponentUiState()
            )
        }

        composeTestRule.onNodeWithText("Add component").assertIsDisplayed()
        composeTestRule.onNodeWithText("Save component").assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Component options").fetchSemanticsNodes().let { nodes ->
            assertTrue(nodes.isEmpty())
        }
    }

    @Test
    fun componentScreen_existingComponentShowsOptions() {
        composeTestRule.setContent {
            BikeComponentScreen(
                bikeId = "road-bike",
                componentId = "component-1",
                uiState = AddBikeComponentUiState()
            )
        }

        composeTestRule.onNodeWithText("Component options").assertIsDisplayed()
        composeTestRule.onNodeWithText("Update component").assertIsDisplayed()
        composeTestRule.onNodeWithText("Replace component").assertIsDisplayed()
        composeTestRule.onNodeWithText("Bike: road-bike - Component: component-1").assertIsDisplayed()
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
            Component(
                id = "component-1",
                type = "DRIVETRAIN",
                name = "Shimano 105",
                status = "ACTIVE",
                brand = "Shimano",
                model = "105",
                notes = null,
                odometerKm = 120,
                usageTimeMinutes = 600
            ),
            Component(
                id = "component-2",
                type = "SUSPENSION",
                name = "Fox 34",
                status = "ACTIVE",
                brand = "Fox",
                model = "Factory",
                notes = null,
                odometerKm = 80,
                usageTimeMinutes = 360
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
