package enumSample

import com.jakala.mapkt.annotations.MapKt
import com.jakala.mapkt.toMappedEnumClass

@MapKt(
    mapTo = [MappedEnumClass::class],
)
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

fun test() {
    EnumClass.FOURTH.toMappedEnumClass()
}
