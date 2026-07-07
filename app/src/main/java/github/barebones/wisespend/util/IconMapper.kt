package github.barebones.wisespend.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.icons.lucide.*

object IconMapper {
    val allIcons: Map<String, ImageVector> = mapOf(
        "lucide_shopping_cart"  to Lucide.ShoppingCart,
        "lucide_utensils"       to Lucide.UtensilsCrossed,
        "lucide_clapperboard"   to Lucide.Clapperboard,
        "material_add"          to Icons.Default.Add,
        "material_home"         to Icons.Default.Home,
        "material_flight"       to Icons.Default.Flight,
        "material_sports"       to Icons.Default.SportsEsports,
        "material_mall"         to Icons.Default.LocalMall,
        "material_school"       to Icons.Default.School,
        "material_fitness"      to Icons.Default.FitnessCenter,
        "material_lightbulb"    to Icons.Default.Lightbulb,
        "material_receipt"      to Icons.Default.Receipt,
        "material_hospital"     to Icons.Default.LocalHospital,
        "material_dining"       to Icons.Default.LocalDining,
        "material_shipping"     to Icons.Default.LocalShipping,
        "material_activity"     to Icons.Default.LocalActivity,
        "material_straighten"   to Icons.Default.Straighten,
        "material_label"        to Icons.AutoMirrored.Filled.Label,
    )

    fun toKey(icon: ImageVector): String =
        allIcons.entries.find { it.value == icon }?.key ?: "material_label"

    fun fromKey(key: String): ImageVector =
        allIcons[key] ?: Icons.AutoMirrored.Filled.Label
}