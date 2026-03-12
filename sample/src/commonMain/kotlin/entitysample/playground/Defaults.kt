package entitysample.playground

import com.jakala.mapkt.toDefaultSample
import com.jakala.mapkt.toMappedDefaultSample
import entitysample.playground.defaults.ComplexSample
import entitysample.playground.defaults.DefaultSample
import entitysample.playground.defaults.MappedDefaultSample
import entitysample.playground.defaults.MappedEnumSample

fun test() {
    DefaultSample(0, complexSample = ComplexSample("")).toMappedDefaultSample()
    MappedDefaultSample(enumValue = MappedEnumSample.SECOND).toDefaultSample()
}