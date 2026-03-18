package enumSample

import com.jakala.mapkt.annotations.MapKt

@MapKt(mapTo = MappedEnumClass::class)
enum class EnumClass {
    FIRST,
    SECOND,
    THIRD,
    FOURTH,
}

enum class MappedEnumClass {
    FIRST,
    SECOND,
    THIRD,
    FOURTH,
}