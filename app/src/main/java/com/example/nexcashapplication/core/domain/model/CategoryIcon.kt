package com.example.nexcashapplication.core.domain.model

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
import androidx.compose.ui.graphics.vector.ImageVector

data class CategoryIcon(
    val id: String,           // identifier unik, buat disimpan di DB (misal "bicycle", "train")
    val label: String,        // nama buat search & accessibility (misal "Sepeda")
    val icon: ImageVector,    // icon Compose-nya
)

data class CategoryIconGroup(
    val categoryName: String,
    val icons: List<CategoryIcon>,
)

enum class IconCategory(val displayName: String) {
    TRANSPORTASI("Transportasi"),
    MAKANAN("Makanan & Minuman"),
    HIBURAN("Hiburan"),
    BELANJA("Belanja"),
    LAINNYA("Lainnya"),
}


object DummyCategoryIcons {

    val transportasi = listOf(
        CategoryIcon("bike", "Sepeda", Icons.Default.DirectionsBike),
        CategoryIcon("car", "Mobil", Icons.Default.DirectionsCar),
        CategoryIcon("bus", "Bus", Icons.Default.DirectionsBus),
        CategoryIcon("train", "Kereta", Icons.Default.Train),
        CategoryIcon("flight", "Pesawat", Icons.Default.Flight),
        CategoryIcon("taxi", "Taksi", Icons.Default.LocalTaxi),
        CategoryIcon("motorcycle", "Motor", Icons.Default.TwoWheeler),
        CategoryIcon("parking", "Parkir", Icons.Default.LocalParking),
        CategoryIcon("gas", "Bensin", Icons.Default.LocalGasStation),
    )

    val makanan = listOf(
        CategoryIcon("restaurant", "Restoran", Icons.Default.Restaurant),
        CategoryIcon("fastfood", "Fast Food", Icons.Default.Fastfood),
        CategoryIcon("coffee", "Kopi", Icons.Default.Coffee),
        CategoryIcon("groceries", "Belanja", Icons.Default.ShoppingCart),
        CategoryIcon("delivery", "Delivery", Icons.Default.DeliveryDining),
        CategoryIcon("wine", "Minuman", Icons.Default.WineBar),
        CategoryIcon("icecream", "Es Krim", Icons.Default.Icecream),
    )

    val hiburan = listOf(
        CategoryIcon("movie", "Film", Icons.Default.Movie),
        CategoryIcon("music", "Musik", Icons.Default.MusicNote),
        CategoryIcon("game", "Game", Icons.Default.SportsEsports),
        CategoryIcon("sport", "Olahraga", Icons.Default.SportsBasketball),
        CategoryIcon("camera", "Foto", Icons.Default.CameraAlt),
        CategoryIcon("party", "Party", Icons.Default.Celebration),
    )

    val tagihan = listOf(
        CategoryIcon("electricity", "Listrik", Icons.Default.ElectricBolt),
        CategoryIcon("water", "Air", Icons.Default.WaterDrop),
        CategoryIcon("phone", "Pulsa", Icons.Default.Phone),
        CategoryIcon("insurance", "Asuransi", Icons.Default.HealthAndSafety),
        CategoryIcon("rent", "Sewa", Icons.Default.Home),
        CategoryIcon("subscription", "Langganan", Icons.Default.Subscriptions),
    )

    val kesehatan = listOf(
        CategoryIcon("hospital", "Rumah Sakit", Icons.Default.LocalHospital),
        CategoryIcon("pharmacy", "Apotek", Icons.Default.LocalPharmacy),
        CategoryIcon("fitness", "Fitness", Icons.Default.FitnessCenter),
        CategoryIcon("spa", "Spa", Icons.Default.Spa),
    )

    val lainnya = listOf(
        CategoryIcon("location", "Lokasi", Icons.Default.LocationOn),
        CategoryIcon("gift", "Hadiah", Icons.Default.CardGiftcard),
        CategoryIcon("pet", "Hewan", Icons.Default.Pets),
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
}