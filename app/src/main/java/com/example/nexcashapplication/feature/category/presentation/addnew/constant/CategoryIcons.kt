package com.example.nexcashapplication.feature.category.presentation.addnew.constant

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Icecream
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WineBar
import com.example.nexcashapplication.core.domain.model.CategoryIcon
import com.example.nexcashapplication.core.domain.model.CategoryIconGroup

object CategoryIcons {
    val transportasi = listOf(
        CategoryIcon("bike", Icons.Default.DirectionsBike),
        CategoryIcon("car", Icons.Default.DirectionsCar),
        CategoryIcon("bus", Icons.Default.DirectionsBus),
        CategoryIcon("train", Icons.Default.Train),
        CategoryIcon("flight", Icons.Default.Flight),
        CategoryIcon("taxi", Icons.Default.LocalTaxi),
        CategoryIcon("motorcycle", Icons.Default.TwoWheeler),
        CategoryIcon("parking", Icons.Default.LocalParking),
        CategoryIcon("gas", Icons.Default.LocalGasStation),
    )

    val makanan = listOf(
        CategoryIcon("restaurant", Icons.Default.Restaurant),
        CategoryIcon("fastfood", Icons.Default.Fastfood),
        CategoryIcon("coffee", Icons.Default.Coffee),
        CategoryIcon("groceries", Icons.Default.ShoppingCart),
        CategoryIcon("delivery", Icons.Default.DeliveryDining),
        CategoryIcon("wine", Icons.Default.WineBar),
        CategoryIcon("icecream", Icons.Default.Icecream),
    )

    val hiburan = listOf(
        CategoryIcon("movie", Icons.Default.Movie),
        CategoryIcon("music", Icons.Default.MusicNote),
        CategoryIcon("game", Icons.Default.SportsEsports),
        CategoryIcon("sport", Icons.Default.SportsBasketball),
        CategoryIcon("camera", Icons.Default.CameraAlt),
        CategoryIcon("party", Icons.Default.Celebration),
    )

    val tagihan = listOf(
        CategoryIcon("electricity", Icons.Default.ElectricBolt),
        CategoryIcon("water", Icons.Default.WaterDrop),
        CategoryIcon("phone", Icons.Default.Phone),
        CategoryIcon("insurance", Icons.Default.HealthAndSafety),
        CategoryIcon("rent", Icons.Default.Home),
        CategoryIcon("subscription", Icons.Default.Subscriptions),
    )

    val kesehatan = listOf(
        CategoryIcon("hospital", Icons.Default.LocalHospital),
        CategoryIcon("pharmacy", Icons.Default.LocalPharmacy),
        CategoryIcon("fitness", Icons.Default.FitnessCenter),
        CategoryIcon("spa", Icons.Default.Spa),
    )

    val lainnya = listOf(
        CategoryIcon("location", Icons.Default.LocationOn),
        CategoryIcon("gift", Icons.Default.CardGiftcard),
        CategoryIcon("pet", Icons.Default.Pets),
    )

    val allGroups = listOf(
        CategoryIconGroup("Transportasi", transportasi),
        CategoryIconGroup("Makanan & Minuman", makanan),
        CategoryIconGroup("Hiburan", hiburan),
        CategoryIconGroup("Tagihan", tagihan),
        CategoryIconGroup("Kesehatan", kesehatan),
        CategoryIconGroup("Lainnya", lainnya),
    )


    // Buat preview row - icon yang sering dipakai
    val recentIcons = listOf(
        transportasi[0], // Sepeda
        transportasi[3], // Kereta
        transportasi[4], // Pesawat
        transportasi[2], // Bus
        lainnya[0],      // Lokasi
    )

    private val flatMap: Map<String, CategoryIcon> by lazy {
        allGroups.flatMap { it.icons }.associateBy { it.id }
    }

    fun fromId(id: String): CategoryIcon {
        return flatMap[id] ?: lainnya[0] // fallback default: icon "location"
    }
}