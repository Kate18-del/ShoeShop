import kotlinx.serialization.Serializable


@Serializable
data class Product(
    val id: String,
    val title: String,
    val category_id: String?,
    val cost: Double,
    val description: String,
    val is_best_seller: Boolean? = false,
    val imageUrl: String? = null,
    val imageResId: Int? = null
) {
    // Для отображения в UI
    fun getFormattedPrice(): String = "P${String.format("%.2f", cost)}"
}