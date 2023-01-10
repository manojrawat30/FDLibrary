import com.nivesh.production.bajajfd.model.Errors

data class Response(
    val Errors: List<Errors>,
    val Message: String,
    var ROIDatalist: MutableList<ROIDataList>,
    val Status: String,
    val StatusCode: Int
)