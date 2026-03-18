package entitysample.properties

import com.jakala.mapkt.annotations.MapKt
import com.jakala.mapkt.annotations.PropertyMapping

@MapKt(
    mapTo = RemoteSomeProperty::class,
    aliases = [
        PropertyMapping("name", "fullName"),
    ],
    ignores = [ "newValue" ],
)
data class SomeProperty(
    val name: String,
    val value: String,
    val newValue: String,
)

data class RemoteSomeProperty(
    val fullName: String,
    val value: String,
)

@MapKt(
    mapTo = SomeProperty::class,
    ignores = [ "dbId", "newValue" ],
)
data class LocalSomeProperty(
    val dbId: Long,
    val name: String,
    val value: String,
)