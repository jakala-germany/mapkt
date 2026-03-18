package entitysample.properties

import com.jakala.mapkt.annotations.MapKt
import com.jakala.mapkt.annotations.PropertyMapping

@MapKt(
    mapTo = RemoteSomeProperty::class,
    aliases = [
        PropertyMapping("name", "fullName"),
    ],
)
data class SomeProperty(
    val name: String,
    val value: String,
)

data class RemoteSomeProperty(
    val fullName: String,
    val value: String,
)