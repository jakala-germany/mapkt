package enumSample

import com.github.yanneckreiss.kconmapper.annotations.KConMap

@KConMap(
    mapTo = [MappedEnumClass::class]
)
enum class EnumClass {
    FIRST, SECOND, THIRD, FOURTH
}

enum class MappedEnumClass {
    FIRST, SECOND, THIRD, FOURTH
}

fun test() {
    EnumClass.FOURTH.toMappedEnumClass()
}