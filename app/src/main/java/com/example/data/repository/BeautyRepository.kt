package com.example.data.repository

import com.example.R
import com.example.data.model.BeautyLook
import com.example.data.model.BudgetOption
import com.example.data.model.CosmeticInsight
import com.example.data.model.Product
import com.example.data.model.ProductCategory
import com.example.data.model.ShadeOption

object BeautyRepository {

    val availableShades = listOf(
        ShadeOption(
            id = "shade_velvet_plum",
            name = "Velvet Plum",
            hexColor = 0xFF562A3B,
            undertone = "Rich Berry Plum",
            finishRecommended = "Satin Velvet",
            description = "Deep crushed berry with cool undertones and tactile velvet richness."
        ),
        ShadeOption(
            id = "shade_terracotta_dew",
            name = "Terracotta Dew",
            hexColor = 0xFFC0684E,
            undertone = "Warm Earthen",
            finishRecommended = "Dewy Radiant",
            description = "Sun-baked earth tone balancing warmth and natural radiance."
        ),
        ShadeOption(
            id = "shade_rose_nude",
            name = "Rose Nude",
            hexColor = 0xFFC98B82,
            undertone = "Neutral Rose",
            finishRecommended = "Satin",
            description = "Understated petal wash providing subtle natural enhancement."
        ),
        ShadeOption(
            id = "shade_spiced_berry",
            name = "Spiced Berry",
            hexColor = 0xFF7E384D,
            undertone = "Deep Spiced Wine",
            finishRecommended = "Soft Matte",
            description = "Bold editorial contrast designed for evening definition."
        ),
        ShadeOption(
            id = "shade_golden_radiance",
            name = "Golden Glow",
            hexColor = 0xFFB37D45,
            undertone = "Warm Bronze",
            finishRecommended = "Luminous Sheer",
            description = "Gilded amber reflection evoking late afternoon studio sunlight."
        ),
        ShadeOption(
            id = "shade_amber_glaze",
            name = "Amber Glaze",
            hexColor = 0xFF9B5D34,
            undertone = "Caramel Undertone",
            finishRecommended = "Glossed Balm",
            description = "Nourishing warmth complementing deeper undertones effortlessly."
        )
    )

    val cosmeticInsights = listOf(
        CosmeticInsight(
            id = "insight_undertone",
            title = "Undertone Warmth",
            value = "Neutral Rose",
            cosmeticNote = "Balances cool plum pigments and warm earthy terracotta without ashiness.",
            categoryTag = "Tone Calibration"
        ),
        CosmeticInsight(
            id = "insight_hydration",
            title = "Hydration Finish Signal",
            value = "Balanced Radiance",
            cosmeticNote = "Thrives with breathable emulsion serums and tactile satin cream textures.",
            categoryTag = "Finish Compatibility"
        ),
        CosmeticInsight(
            id = "insight_texture",
            title = "Texture Affinity",
            value = "Velvet Smooth",
            cosmeticNote = "Minimal pores and soft grain ideal for featherweight diffusion.",
            categoryTag = "Application Profile"
        ),
        CosmeticInsight(
            id = "insight_coverage",
            title = "Coverage Preference",
            value = "Sheer-to-Medium",
            cosmeticNote = "Preserves skin breathability while allowing dimensional cheek draping.",
            categoryTag = "Formulation Guide"
        ),
        CosmeticInsight(
            id = "insight_sunlight",
            title = "Sun Resonance",
            value = "Golden Ambient",
            cosmeticNote = "Reflects natural studio day light with soft-focus pearl undertones.",
            categoryTag = "Lighting Responsive"
        ),
        CosmeticInsight(
            id = "insight_lip",
            title = "Lip Contour Tone",
            value = "Soft Mauve Berry",
            cosmeticNote = "Natural berry border anchors both muted nudes and bold pressed plum.",
            categoryTag = "Color Draping"
        )
    )

    val budgetOptions = listOf(
        BudgetOption(id = "b1", label = "₹750", maxLimit = 750),
        BudgetOption(id = "b2", label = "₹1,500", maxLimit = 1500),
        BudgetOption(id = "b3", label = "₹2,500", maxLimit = 2500),
        BudgetOption(id = "b4", label = "₹4,000", maxLimit = 4000),
        BudgetOption(id = "b_custom", label = "Custom", maxLimit = 10000)
    )

    val initialProducts = listOf(
        Product(
            id = "prod_serum",
            brand = "Aurora Botanical",
            name = "Lumina Hydration Botanical Serum",
            shade = "Dewy Essence",
            finish = "Glass Radiance",
            category = ProductCategory.PREP,
            reasonTag = "Matches satin preference",
            priceInRupees = 1450,
            verifiedTimestamp = "Price verified today",
            localDrawableRes = R.drawable.product_serum,
            imageUrl = "https://images.unsplash.com/photo-1620916566398-39f1143ab7be?auto=format&fit=crop&w=600&q=80",
            description = "A lightweight cold-pressed botanical nectar that plumps skin before makeup application, infusing moisture without greasy residue.",
            ingredientsHighlight = "Squalane, Rosehip Seed Oil, White Truffle Extract, Hyaluronic Acid"
        ),
        Product(
            id = "prod_lip_plum",
            brand = "Atelier Cosmetics",
            name = "Velvet Pressed Lip Cushion",
            shade = "04 Velvet Plum",
            finish = "Satin Velvet",
            category = ProductCategory.LIPS,
            reasonTag = "Editorial studio signature",
            priceInRupees = 1250,
            verifiedTimestamp = "Price verified today",
            localDrawableRes = R.drawable.tryon_after,
            imageUrl = "https://images.unsplash.com/photo-1586495777744-4413f21062fa?auto=format&fit=crop&w=600&q=80",
            description = "Richly saturated crushed berry pigment enriched with organic cocoa butter that blurs effortlessly onto lips for an editorial finish.",
            ingredientsHighlight = "Shea Butter, Crushed Mineral Pigments, Camellia Seed Oil, Vitamin E"
        ),
        Product(
            id = "prod_skin_tint",
            brand = "Maison Lumina",
            name = "Featherweight Satin Tint",
            shade = "02 Neutral Warm",
            finish = "Soft Satin",
            category = ProductCategory.BASE,
            reasonTag = "Formulated for neutral undertone",
            priceInRupees = 1850,
            verifiedTimestamp = "Price verified today",
            localDrawableRes = null,
            imageUrl = "https://images.unsplash.com/photo-1522337360788-8b13dee7a37e?auto=format&fit=crop&w=600&q=80",
            description = "Breathable mineral fluid that mimics the texture of real skin while evening out tone with sheer-to-medium customizable veil.",
            ingredientsHighlight = "Niacinamide, Glycerin, Mineral Micro-pigments, Green Tea Leaf Infusion"
        ),
        Product(
            id = "prod_blush",
            brand = "Kavya Studio",
            name = "Petal Veil Draping Blush",
            shade = "Terracotta Rose",
            finish = "Dewy Radiance",
            category = ProductCategory.CHEEKS,
            reasonTag = "Complements warm terracotta",
            priceInRupees = 980,
            verifiedTimestamp = "Price verified today",
            localDrawableRes = null,
            imageUrl = "https://images.unsplash.com/photo-1596462502278-27bfdc403348?auto=format&fit=crop&w=600&q=80",
            description = "Whipped soufflé cream-to-powder cheek tint that melts into skin for a seamless natural flush.",
            ingredientsHighlight = "Jojoba Esters, Raspberry Seed Wax, Mica Pearl, Sweet Almond Oil"
        ),
        Product(
            id = "prod_lip_nude",
            brand = "Atelier Cosmetics",
            name = "Nude Whisper Lip Glaze",
            shade = "01 Rose Petal Nude",
            finish = "Hydrating Glaze",
            category = ProductCategory.LIPS,
            reasonTag = "Budget aligned pick",
            priceInRupees = 680,
            verifiedTimestamp = "Price verified today",
            localDrawableRes = null,
            imageUrl = "https://images.unsplash.com/photo-1631729371254-42c2892f0e6e?auto=format&fit=crop&w=600&q=80",
            description = "Sheer cushion glaze that hydrates deeply while delivering a subtle hint of everyday rosy warmth.",
            ingredientsHighlight = "Peptide Complex, Castor Oil, Vitamin C Ester, Plant Squalane"
        ),
        Product(
            id = "prod_powder",
            brand = "Aura Sculpt",
            name = "Silk Setting Ambient Veil",
            shade = "Translucent Luminous",
            finish = "Soft Focus",
            category = ProductCategory.BASE,
            reasonTag = "Matches satin preference",
            priceInRupees = 1350,
            verifiedTimestamp = "Price verified today",
            localDrawableRes = null,
            imageUrl = "https://images.unsplash.com/photo-1512496015851-a90fb38ba796?auto=format&fit=crop&w=600&q=80",
            description = "Micro-milled featherweight setting powder that locks in base makeup while retaining natural skin dimension.",
            ingredientsHighlight = "Rice Starch, Bamboo Silk, Rose Water Infusion, Silica Spheres"
        )
    )

    val initialLooks = listOf(
        BeautyLook(
            id = "look_velvet_plum",
            title = "Velvet Plum Editorial",
            occasion = "Studio Editorial",
            finish = "Satin Velvet",
            shade = availableShades[0],
            beforeImageRes = R.drawable.tryon_before,
            afterImageRes = R.drawable.tryon_after,
            description = "High-contrast editorial plum lip paired with luminous satin base and sculpted neutral rose cheek contour.",
            recommendedProductIds = listOf("prod_serum", "prod_skin_tint", "prod_lip_plum", "prod_blush"),
            isSaved = true
        ),
        BeautyLook(
            id = "look_terracotta_glow",
            title = "Terracotta Sunlit",
            occasion = "Golden Hour",
            finish = "Dewy Radiant",
            shade = availableShades[1],
            beforeImageRes = R.drawable.tryon_before,
            afterImageRes = R.drawable.tryon_after,
            description = "Warm earthen peach and terracotta wash evoking golden hour sunset on healthy glowing complexion.",
            recommendedProductIds = listOf("prod_serum", "prod_blush", "prod_lip_nude"),
            isSaved = false
        ),
        BeautyLook(
            id = "look_rose_nude",
            title = "Rose Minimalist",
            occasion = "Everyday Soft",
            finish = "Soft Satin",
            shade = availableShades[2],
            beforeImageRes = R.drawable.tryon_before,
            afterImageRes = R.drawable.tryon_after,
            description = "Effortless petal-toned minimalist beauty for casual daywear and studio meetings.",
            recommendedProductIds = listOf("prod_skin_tint", "prod_lip_nude", "prod_powder"),
            isSaved = true
        )
    )
}
