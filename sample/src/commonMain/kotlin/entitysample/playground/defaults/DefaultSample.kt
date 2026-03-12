package entitysample.playground.defaults

data class DefaultSample(
    val intValue: Int,
    val enumValue: EnumSample = EnumSample.FIRST,
    val nullableEnumValue: EnumSample? = null,
    val complexSample: ComplexSample,
    val nullableComplexSample: ComplexSample? = null,
)