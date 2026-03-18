package entitysample.model

import com.jakala.mapkt.annotations.MapKt

@MapKt(mapTo = EnumSample::class)
enum class MappedEnumSample {
    FIRST,
    SECOND,
}