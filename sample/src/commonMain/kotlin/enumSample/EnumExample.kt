package enumSample

import com.github.yanneckreiss.kconmapper.annotations.KConMapper

@KConMapper(
    fromClasses = [MappedEnumClass::class],
    toClasses = [MappedEnumClass::class]
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