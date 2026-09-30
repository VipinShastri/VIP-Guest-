package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DinnerDining
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Tapas
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WineBar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.MenuItemEntity
import com.example.data.model.MenuCategory
import com.example.ui.theme.EmeraldReserve
import com.example.ui.theme.GoldChampagne
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldLight
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.ObsidianSurfaceElevated
import com.example.ui.viewmodel.SavoriaViewModel

/**
 * Form Composable that allows adding new MenuItem entities to the Room database.
 * Includes validated fields for name, price, description, and category,
 * plus optional luxury culinary attributes.
 */
@Composable
fun AddMenuItemForm(
    onDismiss: () -> Unit = {},
    onItemAdded: ((MenuItemEntity) -> Unit)? = null,
    viewModel: SavoriaViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    var isSubmitting by remember { mutableStateOf(false) }

    AddMenuItemFormContent(
        onSubmit = { name, price, desc, category, calories, prep, tags, pairing, isSpecial ->
            isSubmitting = true
            viewModel.addMenuItem(
                name = name,
                price = price,
                description = desc,
                category = category,
                calories = calories,
                prepMinutes = prep,
                tags = tags,
                winePairing = pairing,
                isChefSpecial = isSpecial,
                onSuccess = { entity ->
                    isSubmitting = false
                    onItemAdded?.invoke(entity)
                    onDismiss()
                }
            )
        },
        onDismiss = onDismiss,
        isSubmitting = isSubmitting,
        modifier = modifier
    )
}

/**
 * Dialog wrapper for [AddMenuItemForm] allowing easy display anywhere in the application.
 */
@Composable
fun AddMenuItemDialog(
    onDismiss: () -> Unit,
    viewModel: SavoriaViewModel = viewModel(),
    onItemAdded: ((MenuItemEntity) -> Unit)? = null
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .imePadding(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(24.dp)
            ) {
                AddMenuItemForm(
                    onDismiss = onDismiss,
                    onItemAdded = onItemAdded,
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * Pure presentation / state-holding Composable for the Menu Item creation form.
 * Contains fields for:
 * 1. Name (required)
 * 2. Price (required, validated double)
 * 3. Description (required)
 * 4. Category (required, selectable chips + dropdown)
 * 5. Supplementary culinary attributes (calories, prep time, wine pairing, tags, chef special)
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddMenuItemFormContent(
    onSubmit: (
        name: String,
        price: Double,
        description: String,
        category: String,
        calories: Int,
        prepMinutes: Int,
        tags: String,
        winePairing: String,
        isChefSpecial: Boolean
    ) -> Unit,
    onDismiss: () -> Unit = {},
    isSubmitting: Boolean = false,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    // Form fields state
    var name by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(MenuCategory.MAINS) }

    // Supplementary culinary attributes
    var caloriesText by remember { mutableStateOf("450") }
    var prepMinutesText by remember { mutableStateOf("18") }
    var winePairing by remember { mutableStateOf("") }
    var isChefSpecial by remember { mutableStateOf(false) }

    // Dietary tags
    val availableTags = listOf(
        "Gluten-Free",
        "Vegetarian",
        "Organic",
        "Chef's Signature",
        "Raw Bar",
        "Steakhouse",
        "Classic French",
        "Sustainable",
        "Tableside Service"
    )
    val selectedTags = remember { mutableStateListOf("Chef's Signature") }
    var customTagText by remember { mutableStateOf("") }

    // Validation error states
    var nameError by remember { mutableStateOf<String?>(null) }
    var priceError by remember { mutableStateOf<String?>(null) }
    var descriptionError by remember { mutableStateOf<String?>(null) }
    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }

    // Available categories for selection (excluding ALL filter)
    val selectableCategories = listOf(
        MenuCategory.STARTERS,
        MenuCategory.MAINS,
        MenuCategory.CHEF_SPECIALS,
        MenuCategory.DESSERTS,
        MenuCategory.BEVERAGES
    )

    fun validateAndSubmit() {
        var isValid = true

        if (name.trim().isBlank()) {
            nameError = "Dish name is required"
            isValid = false
        } else {
            nameError = null
        }

        val parsedPrice = priceText.toDoubleOrNull()
        if (parsedPrice == null || parsedPrice <= 0.0) {
            priceError = "Enter a valid price (e.g. 34.50)"
            isValid = false
        } else {
            priceError = null
        }

        if (description.trim().isBlank()) {
            descriptionError = "Culinary description is required"
            isValid = false
        } else {
            descriptionError = null
        }

        if (isValid && parsedPrice != null) {
            val calories = caloriesText.toIntOrNull() ?: 350
            val prep = prepMinutesText.toIntOrNull() ?: 15
            val combinedTags = selectedTags.joinToString(",")

            onSubmit(
                name.trim(),
                parsedPrice,
                description.trim(),
                selectedCategory.name,
                calories,
                prep,
                combinedTags,
                winePairing.trim(),
                isChefSpecial
            )
        }
    }

    // Quick fill helper for testing/rapid demonstration
    fun quickFillPreset() {
        name = "Pan-Seared Hokkaido Scallops"
        priceText = "38.00"
        description = "Caramelized wild scallops, cauliflower mousseline, crispy prosciutto crumb, and saffron citronette."
        selectedCategory = MenuCategory.STARTERS
        caloriesText = "290"
        prepMinutesText = "14"
        winePairing = "Chablis Premier Cru or Blanc de Blancs"
        isChefSpecial = true
        if (!selectedTags.contains("Chef's Signature")) selectedTags.add("Chef's Signature")
        if (!selectedTags.contains("Gluten-Free")) selectedTags.add("Gluten-Free")
        nameError = null
        priceError = null
        descriptionError = null
    }

    Column(
        modifier = modifier
            .background(ObsidianSurface)
            .padding(20.dp)
            .verticalScroll(scrollState)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(GoldContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = "New Dish",
                        tint = GoldChampagne,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Add Menu Item",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        ),
                        color = GoldChampagne
                    )
                    Text(
                        text = "Save new creation to Room database",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("add_menu_item_cancel_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Preset Action Pill
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(ObsidianSurfaceElevated)
                .clickable { quickFillPreset() }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Suggest",
                    tint = GoldChampagne,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Fill Sample Curated Dish",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                    color = GoldLight
                )
            }
            Text(
                text = "Auto-fill",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 1. Dish Name Field
        Text(
            text = "Dish Name *",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                if (nameError != null && it.isNotBlank()) nameError = null
            },
            placeholder = {
                Text(
                    "e.g., Herb-Crusted Rack of Lamb",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            singleLine = true,
            isError = nameError != null,
            supportingText = nameError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("menu_item_name_input"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GoldChampagne,
                unfocusedBorderColor = ObsidianBorder,
                focusedContainerColor = ObsidianSurfaceElevated,
                unfocusedContainerColor = ObsidianSurfaceElevated
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Price Field & Category Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Price Input
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Price (USD) *",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = priceText,
                    onValueChange = {
                        priceText = it
                        if (priceError != null) priceError = null
                    },
                    placeholder = {
                        Text("32.00", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    },
                    prefix = {
                        Text(
                            "$",
                            color = GoldChampagne,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                    },
                    singleLine = true,
                    isError = priceError != null,
                    supportingText = priceError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("menu_item_price_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldChampagne,
                        unfocusedBorderColor = ObsidianBorder,
                        focusedContainerColor = ObsidianSurfaceElevated,
                        unfocusedContainerColor = ObsidianSurfaceElevated
                    )
                )
            }

            // Category Dropdown
            Column(modifier = Modifier.weight(1.2f)) {
                Text(
                    text = "Category *",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                ExposedDropdownMenuBox(
                    expanded = isCategoryDropdownExpanded,
                    onExpandedChange = { isCategoryDropdownExpanded = !isCategoryDropdownExpanded },
                    modifier = Modifier.testTag("menu_item_category_selector")
                ) {
                    OutlinedTextField(
                        value = selectedCategory.displayName,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryDropdownExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldChampagne,
                            unfocusedBorderColor = ObsidianBorder,
                            focusedContainerColor = ObsidianSurfaceElevated,
                            unfocusedContainerColor = ObsidianSurfaceElevated
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = isCategoryDropdownExpanded,
                        onDismissRequest = { isCategoryDropdownExpanded = false },
                        modifier = Modifier.background(ObsidianSurfaceElevated)
                    ) {
                        selectableCategories.forEach { category ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = getCategoryIcon(category),
                                            contentDescription = category.displayName,
                                            tint = if (selectedCategory == category) GoldChampagne else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = category.displayName,
                                            color = if (selectedCategory == category) GoldChampagne else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                },
                                onClick = {
                                    selectedCategory = category
                                    isCategoryDropdownExpanded = false
                                },
                                modifier = Modifier.testTag("category_option_${category.name}")
                            )
                        }
                    }
                }
            }
        }

        // Quick Category Chips
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(selectableCategories) { cat ->
                val isSelected = selectedCategory == cat
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) GoldChampagne else ObsidianSurfaceElevated)
                        .border(
                            1.dp,
                            if (isSelected) GoldLight else ObsidianBorder,
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { selectedCategory = cat }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .testTag("category_chip_selector_${cat.name}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cat.displayName,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = if (isSelected) ObsidianBlack else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Description Field
        Text(
            text = "Culinary Description *",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = description,
            onValueChange = {
                description = it
                if (descriptionError != null && it.isNotBlank()) descriptionError = null
            },
            placeholder = {
                Text(
                    "Describe textures, preparation technique, key artisan ingredients...",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            minLines = 3,
            maxLines = 4,
            isError = descriptionError != null,
            supportingText = descriptionError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("menu_item_description_input"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GoldChampagne,
                unfocusedBorderColor = ObsidianBorder,
                focusedContainerColor = ObsidianSurfaceElevated,
                unfocusedContainerColor = ObsidianSurfaceElevated
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 4. Sommelier Wine Pairing
        Text(
            text = "Sommelier Wine Pairing (Optional)",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = winePairing,
            onValueChange = { winePairing = it },
            placeholder = {
                Text(
                    "e.g., Napa Valley Cabernet Sauvignon 2018",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.WineBar,
                    contentDescription = "Wine",
                    tint = GoldChampagne,
                    modifier = Modifier.size(18.dp)
                )
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("menu_item_pairing_input"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GoldChampagne,
                unfocusedBorderColor = ObsidianBorder,
                focusedContainerColor = ObsidianSurfaceElevated,
                unfocusedContainerColor = ObsidianSurfaceElevated
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 5. Calories & Prep Time
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Calories (kcal)",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = caloriesText,
                    onValueChange = { caloriesText = it },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Calories",
                            tint = GoldChampagne,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("menu_item_calories_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldChampagne,
                        unfocusedBorderColor = ObsidianBorder,
                        focusedContainerColor = ObsidianSurfaceElevated,
                        unfocusedContainerColor = ObsidianSurfaceElevated
                    )
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Prep Time (min)",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = prepMinutesText,
                    onValueChange = { prepMinutesText = it },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Prep Time",
                            tint = GoldChampagne,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("menu_item_prep_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldChampagne,
                        unfocusedBorderColor = ObsidianBorder,
                        focusedContainerColor = ObsidianSurfaceElevated,
                        unfocusedContainerColor = ObsidianSurfaceElevated
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 6. Chef's Special Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(ObsidianSurfaceElevated)
                .border(1.dp, ObsidianBorder, RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Stars,
                    contentDescription = "Special",
                    tint = GoldChampagne,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Chef's Signature Selection",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Highlight on main curations banner",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Switch(
                checked = isChefSpecial,
                onCheckedChange = { isChefSpecial = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = ObsidianBlack,
                    checkedTrackColor = GoldChampagne,
                    uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    uncheckedTrackColor = ObsidianSurface
                ),
                modifier = Modifier.testTag("menu_item_chef_special_switch")
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 7. Dietary & Style Tags
        Text(
            text = "Culinary & Dietary Tags",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            availableTags.forEach { tag ->
                val isSelected = selectedTags.contains(tag)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) GoldContainer else ObsidianSurfaceElevated)
                        .border(
                            1.dp,
                            if (isSelected) GoldChampagne else ObsidianBorder,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable {
                            if (isSelected) selectedTags.remove(tag) else selectedTags.add(tag)
                        }
                        .padding(horizontal = 8.dp, vertical = 5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = GoldChampagne,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) GoldLight else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 8. Submit and Cancel Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("add_menu_item_cancel_button")
            ) {
                Text("Cancel")
            }

            Button(
                onClick = { validateAndSubmit() },
                enabled = !isSubmitting,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldChampagne,
                    contentColor = ObsidianBlack
                ),
                modifier = Modifier
                    .weight(1.5f)
                    .height(48.dp)
                    .testTag("add_menu_item_submit_button")
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = ObsidianBlack,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Add to Menu",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

private fun getCategoryIcon(category: MenuCategory): ImageVector {
    return when (category) {
        MenuCategory.STARTERS -> Icons.Default.Tapas
        MenuCategory.MAINS -> Icons.Default.DinnerDining
        MenuCategory.CHEF_SPECIALS -> Icons.Default.Stars
        MenuCategory.DESSERTS -> Icons.Default.Cake
        MenuCategory.BEVERAGES -> Icons.Default.WineBar
        MenuCategory.ALL -> Icons.Default.Restaurant
    }
}
