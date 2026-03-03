

data class Product(
    val id: String,
    val name: String,
    val price: String,
    val originalPrice: String,
    val category: String,
    val imageUrl: String = "", // для URL из сети
    val imageResId: Int? = null // для локальных ресурсов
)