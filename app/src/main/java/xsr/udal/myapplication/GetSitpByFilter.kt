package xsr.udal.myapplication

data class GetSitpByFilter(
    var features: List<Feature>,
    var fields: List<Field>,
    var geometryType: String,
    var globalIdFieldName: String,
    var objectIdFieldName: String,
    var spatialReference: SpatialReference
)