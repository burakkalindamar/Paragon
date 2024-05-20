import java.text.SimpleDateFormat
import java.util.*

data class IslemModel(
    val symbol: String,
    val tarih: Date,
    val adet: Int,
    val fiyat: Double,
    val islem: String
) {
    fun getFormattedTarih(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(tarih)
    }
}
