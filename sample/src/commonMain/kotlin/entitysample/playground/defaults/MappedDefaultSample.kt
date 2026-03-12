package entitysample.playground.defaults

import com.jakala.mapkt.annotations.MapKt

@MapKt(mapTo = [DefaultSample::class])
data class MappedDefaultSample(
    val intValue: Int = 0,
    val enumValue: MappedEnumSample,
    val nullableEnumValue: MappedEnumSample? = null,
    val complexSample: ComplexSample = ComplexSample(""),
    val nullableComplexSample: ComplexSample? = null,
)
