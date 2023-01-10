data class ROIDataList(
    val Frequency: String,
    val Provider: String,
    val ROI: Double,
    val Tenure: String,
    val Type: String
) {
    override fun toString(): String {
        return Tenure.plus(" Months ").plus(" |  ").plus(ROI).plus("%")
    }
}