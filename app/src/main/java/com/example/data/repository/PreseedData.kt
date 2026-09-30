package com.example.data.repository

import com.example.data.local.MenuItemEntity
import com.example.data.model.DiningTable
import com.example.data.model.MenuCategory
import com.example.data.model.TableStatus
import com.example.data.model.TableZone

object PreseedData {

    val initialMenuItems = listOf(
        // Hors d'oeuvres
        MenuItemEntity(
            name = "Black Truffle & Taleggio Arancini",
            description = "Crisp carnaroli rice sphere infused with Italian black truffle, melted taleggio core, and 25-year aged balsamic reduction.",
            category = MenuCategory.STARTERS.name,
            price = 26.0,
            calories = 380,
            prepMinutes = 15,
            tags = "Vegetarian,Chef's Signature",
            winePairing = "Barolo DOCG or Franciacorta Brut",
            isChefSpecial = false,
            rating = 4.9f
        ),
        MenuItemEntity(
            name = "Hamachi Crudo with Yuzu Ponzu",
            description = "Sashimi-grade yellowtail kingfish, Australian finger lime caviar, crisp sea grapes, and cold-pressed white truffle oil.",
            category = MenuCategory.STARTERS.name,
            price = 28.0,
            calories = 240,
            prepMinutes = 12,
            tags = "Gluten-Free,Raw Bar,Light",
            winePairing = "Sancerre Sauvignon Blanc",
            isChefSpecial = false,
            rating = 4.8f
        ),
        MenuItemEntity(
            name = "Prime Wagyu Tartare Crostini",
            description = "Hand-cut A5 Miyazaki tenderloin, organic quail egg yolk, shallot confit, caper berries on grilled house brioche.",
            category = MenuCategory.STARTERS.name,
            price = 32.0,
            calories = 410,
            prepMinutes = 16,
            tags = "Raw Bar,Signature",
            winePairing = "Pinot Noir Bourgogne",
            isChefSpecial = true,
            rating = 5.0f
        ),
        MenuItemEntity(
            name = "Maine Lobster & Cognac Bisque",
            description = "Velvety lobster velouté simmered for 18 hours with VSOP cognac, fresh tarragon chantilly, and butter-poached claw morsels.",
            category = MenuCategory.STARTERS.name,
            price = 24.0,
            calories = 320,
            prepMinutes = 14,
            tags = "Signature,Classic",
            winePairing = "Chardonnay Russian River",
            isChefSpecial = false,
            rating = 4.9f
        ),

        // Main Entrées
        MenuItemEntity(
            name = "45-Day Dry-Aged Prime Ribeye (14oz)",
            description = "USDA Prime beef aged in Himalayan salt vault, roasted bone marrow herb butter, glazed heirloom carrots, and rich bordelaise jus.",
            category = MenuCategory.MAINS.name,
            price = 68.0,
            calories = 890,
            prepMinutes = 25,
            tags = "Gluten-Free,Signature,Steakhouse",
            winePairing = "Napa Valley Cabernet Sauvignon 2018",
            isChefSpecial = true,
            rating = 4.95f
        ),
        MenuItemEntity(
            name = "Pan-Seared Chilean Sea Bass",
            description = "Glacier 51 sustainably caught sea bass, sweet white miso glaze, glazed baby bok choy, and dashi shiitake mushroom emulsion.",
            category = MenuCategory.MAINS.name,
            price = 56.0,
            calories = 520,
            prepMinutes = 20,
            tags = "Gluten-Free,Seafood",
            winePairing = "Meursault Premier Cru Chardonnay",
            isChefSpecial = true,
            rating = 4.9f
        ),
        MenuItemEntity(
            name = "Imperial Duck Breast & Confit",
            description = "Slow-roasted Challandais duck breast with lavender flower honey crust, crispy leg confit pressé, parsnip mousseline, and tart cherry gastrique.",
            category = MenuCategory.MAINS.name,
            price = 48.0,
            calories = 660,
            prepMinutes = 22,
            tags = "Poultry,Classic French",
            winePairing = "Gevrey-Chambertin Pinot Noir",
            isChefSpecial = false,
            rating = 4.85f
        ),
        MenuItemEntity(
            name = "Wild Morel Mushroom & Saffron Risotto",
            description = "Acquerello aged carnaroli rice, Kashmiri saffron, foraged black morels, fermented black garlic crisps, and 36-month Parmigiano Reggiano.",
            category = MenuCategory.MAINS.name,
            price = 42.0,
            calories = 490,
            prepMinutes = 20,
            tags = "Vegetarian,Gluten-Free",
            winePairing = "Barbaresco Nebbiolo",
            isChefSpecial = false,
            rating = 4.8f
        ),

        // Chef's Signature Platters
        MenuItemEntity(
            name = "Grand Savoria Ocean Plateau",
            description = "Chilled King Crab legs, 6 Kumamoto oysters, jumbo tiger prawns, Hokkaido scallop ceviche, and house cocktail mignonette.",
            category = MenuCategory.CHEF_SPECIALS.name,
            price = 135.0,
            calories = 620,
            prepMinutes = 25,
            tags = "Seafood,Raw Bar,Platter for Two",
            winePairing = "Dom Pérignon Vintage 2013",
            isChefSpecial = true,
            rating = 5.0f
        ),
        MenuItemEntity(
            name = "A5 Miyazaki Wagyu Tenderloin (8oz)",
            description = "Bespoke BMS 11 Japanese Wagyu, 24-karat edible gold leaf crust, smoked Murray River pink salt, charred baby leeks, and black Périgord jus.",
            category = MenuCategory.CHEF_SPECIALS.name,
            price = 145.0,
            calories = 720,
            prepMinutes = 25,
            tags = "Gluten-Free,Exclusive,Luxury",
            winePairing = "Château Cheval Blanc 2012",
            isChefSpecial = true,
            rating = 5.0f
        ),
        MenuItemEntity(
            name = "Dover Sole Meunière Flambé",
            description = "Freshly flown wild Dover sole filleted tableside, clarified hazelnut butter, Sicilian salted capers, Meyer lemon, and Italian parsley.",
            category = MenuCategory.CHEF_SPECIALS.name,
            price = 74.0,
            calories = 540,
            prepMinutes = 25,
            tags = "Tableside Service,Seafood",
            winePairing = "Chablis Grand Cru",
            isChefSpecial = true,
            rating = 4.95f
        ),

        // Artisan Desserts
        MenuItemEntity(
            name = "Dark Chocolate & 24K Gold Dome",
            description = "Valrhona Guanaja 70% dark chocolate mousse, hazelnut praline feuilletine, hot Grand Marnier caramel poured tableside.",
            category = MenuCategory.DESSERTS.name,
            price = 24.0,
            calories = 460,
            prepMinutes = 12,
            tags = "Signature Dessert,Vegetarian",
            winePairing = "Taylor Fladgate 20-Year Tawny Port",
            isChefSpecial = true,
            rating = 5.0f
        ),
        MenuItemEntity(
            name = "Tahitian Vanilla Bean Soufflé",
            description = "Warm risen soufflé infused with whole Tahitian vanilla orchid pods, served with chilled pistachio crème anglaise.",
            category = MenuCategory.DESSERTS.name,
            price = 22.0,
            calories = 380,
            prepMinutes = 18,
            tags = "Classic French,Vegetarian",
            winePairing = "Château d'Yquem Sauternes",
            isChefSpecial = false,
            rating = 4.9f
        ),
        MenuItemEntity(
            name = "Sicilian Bronte Pistachio Tart",
            description = "Crisp sablé crust, whipped mascarpone cream, Bronte pistachio paste, and wild macerated strawberries.",
            category = MenuCategory.DESSERTS.name,
            price = 19.0,
            calories = 350,
            prepMinutes = 10,
            tags = "Nut Special,Vegetarian",
            winePairing = "Moscato d'Asti DOCG",
            isChefSpecial = false,
            rating = 4.8f
        ),

        // Cellar & Spirits
        MenuItemEntity(
            name = "Château Margaux Premier Cru (Glass)",
            description = "2015 Vintage. Intense floral violet aroma, blackcurrant, refined velvety tannins, and unparalleled lingering finish.",
            category = MenuCategory.BEVERAGES.name,
            price = 65.0,
            calories = 145,
            prepMinutes = 5,
            tags = "Grand Cru,Vintage Wine",
            winePairing = "Pairs with Dry-Aged Ribeye & Wagyu",
            isChefSpecial = true,
            rating = 5.0f
        ),
        MenuItemEntity(
            name = "Smoked Rosemary Old Fashioned",
            description = "Small-batch Woodford Reserve Bourbon, rich demerara syrup, Angostura & orange bitters, torched organic rosemary cloche smoke.",
            category = MenuCategory.BEVERAGES.name,
            price = 26.0,
            calories = 180,
            prepMinutes = 6,
            tags = "Craft Cocktail,Smoked",
            winePairing = "Aperitif or digestif",
            isChefSpecial = true,
            rating = 4.9f
        ),
        MenuItemEntity(
            name = "Royal Elderflower & Pear Spritz",
            description = "Clarified French pear nectar, St. Germain essence, sparkling spring water, crushed mint, and edible gold flakes.",
            category = MenuCategory.BEVERAGES.name,
            price = 18.0,
            calories = 95,
            prepMinutes = 5,
            tags = "Non-Alcoholic,Mocktail,Refreshing",
            winePairing = "Universal aperitif",
            isChefSpecial = false,
            rating = 4.85f
        )
    )

    val initialTables = listOf(
        // Grand Dining Hall
        DiningTable(1, 1, 2, TableZone.GRAND_HALL, TableStatus.AVAILABLE, isWindowSeat = true, "Intimate candlelit table beside crystal chandelier"),
        DiningTable(2, 2, 2, TableZone.GRAND_HALL, TableStatus.OCCUPIED, isWindowSeat = false, "Center hall romantic banquette"),
        DiningTable(3, 3, 4, TableZone.GRAND_HALL, TableStatus.AVAILABLE, isWindowSeat = false, "Round mahogany table with orchestra acoustics"),
        DiningTable(4, 4, 4, TableZone.GRAND_HALL, TableStatus.RESERVED, isWindowSeat = true, "Arched window overlooking hotel atrium fountain"),
        DiningTable(5, 5, 6, TableZone.GRAND_HALL, TableStatus.AVAILABLE, isWindowSeat = false, "Spacious semi-circular velvet booth"),
        DiningTable(6, 6, 6, TableZone.GRAND_HALL, TableStatus.AVAILABLE, isWindowSeat = true, "Grand corner table for celebratory dining"),

        // Garden Terrace
        DiningTable(7, 7, 2, TableZone.GARDEN_TERRACE, TableStatus.AVAILABLE, isWindowSeat = true, "Private heated terrace table surrounded by jasmine"),
        DiningTable(8, 8, 2, TableZone.GARDEN_TERRACE, TableStatus.AVAILABLE, isWindowSeat = true, "Fountain-view bistro table with warm brass lantern"),
        DiningTable(9, 9, 4, TableZone.GARDEN_TERRACE, TableStatus.OCCUPIED, isWindowSeat = true, "Open-air patio dining under illuminated pergola"),
        DiningTable(10, 10, 4, TableZone.GARDEN_TERRACE, TableStatus.AVAILABLE, isWindowSeat = true, "Garden alcove table with gentle evening breeze"),
        DiningTable(11, 11, 6, TableZone.GARDEN_TERRACE, TableStatus.RESERVED, isWindowSeat = true, "Expansive veranda table with marble fire pit"),

        // Skyline VIP Lounge
        DiningTable(12, 12, 2, TableZone.SKYLINE_VIP, TableStatus.AVAILABLE, isWindowSeat = true, "Top floor skyline window with 180° harbor panorama"),
        DiningTable(13, 13, 4, TableZone.SKYLINE_VIP, TableStatus.AVAILABLE, isWindowSeat = true, "VIP leather lounge table overlooking city lights"),
        DiningTable(14, 14, 4, TableZone.SKYLINE_VIP, TableStatus.OCCUPIED, isWindowSeat = true, "Private booth with dedicated sommelier service"),
        DiningTable(15, 15, 6, TableZone.SKYLINE_VIP, TableStatus.AVAILABLE, isWindowSeat = true, "Executive banquette with crystal champagne cooler"),
        DiningTable(16, 16, 8, TableZone.SKYLINE_VIP, TableStatus.RESERVED, isWindowSeat = true, "Presidential skyline table with private butler bar"),

        // Private Cellar Room
        DiningTable(17, 17, 8, TableZone.PRIVATE_CELLAR, TableStatus.AVAILABLE, isWindowSeat = false, "Handcrafted oak banquet table in vintage wine vault"),
        DiningTable(18, 18, 12, TableZone.PRIVATE_CELLAR, TableStatus.AVAILABLE, isWindowSeat = false, "Grand sommelier tasting table with temperature-controlled cellar")
    )
}
