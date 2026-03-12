package entitysample.playground.defaults

import com.jakala.mapkt.annotations.MapKt

@MapKt(mapTo = [EnumSample::class])
enum class MappedEnumSample {
    FIRST,
    SECOND,
}
