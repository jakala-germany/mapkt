package entitysample.model

import com.jakala.mapkt.annotations.MapKt
import com.jakala.mapkt.annotations.PropertyMapping

@MapKt(
    mapTo = DefaultSample::class,
    aliases = [
        PropertyMapping(source = "intValue", target = "intValue"),
        PropertyMapping(source = "enumValue", target = "enumValue"),
        PropertyMapping(source = "nullableEnumValue", target = "nullableEnumValue"),
        PropertyMapping(source = "complexSample", target = "complexSample"),
        PropertyMapping(source = "nullableComplexSample", target = "nullableComplexSample"),
    ],
)
data class MappedDefaultSample(
    val intValue: Int = 0,
    val enumValue: MappedEnumSample,
    val nullableEnumValue: MappedEnumSample? = null,
    val complexSample: ComplexSample = ComplexSample(""),
    val nullableComplexSample: ComplexSample? = null,
)