package entitysample.playground.defaults

import com.jakala.mapkt.annotations.MapKt
import com.jakala.mapkt.annotations.PropertyMapping

@MapKt(
    mapTo = DefaultSample::class,
    aliases = [
        PropertyMapping(src = "intValue", tar = "intValue"),
        PropertyMapping(src = "enumValue", tar = "enumValue"),
        PropertyMapping(src = "nullableEnumValue", tar = "nullableEnumValue"),
        PropertyMapping(src = "complexSample", tar = "complexSample"),
        PropertyMapping(src = "nullableComplexSample", tar = "nullableComplexSample"),
    ],
)
data class MappedDefaultSample(
    val intValue: Int = 0,
    val enumValue: MappedEnumSample,
    val nullableEnumValue: MappedEnumSample? = null,
    val complexSample: ComplexSample = ComplexSample(""),
    val nullableComplexSample: ComplexSample? = null,
)