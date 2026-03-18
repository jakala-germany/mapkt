package entitysample

import com.jakala.mapkt.annotations.MapKt

data class SuperModel(
    val name: String,
)

@MapKt(mapTo = SuperModel::class)
data class LocalSuperModel(
    val name: String,
)

@MapKt(mapTo = SuperModel::class)
data class RemoteSuperModel(
    val name: String,
)

sealed class SuperNestedModel {
    data class Inner1(
        val name: String,
    ) : SuperNestedModel()

    data class Inner2(
        val name: String,
    ) : SuperNestedModel()

    enum class InnerEnum {
        FIRST,
        SECOND,
        THIRD,
        FOURTH,
    }
}

@MapKt(mapTo = SuperNestedModel::class)
sealed class LocalNestedModel {
    @MapKt(mapTo = SuperNestedModel.Inner1::class)
    data class Inner1(
        val name: String,
    ) : LocalNestedModel()

    @MapKt(mapTo = SuperNestedModel.Inner2::class)
    data class Inner2(
        val name: String,
    ) : LocalNestedModel()

    @MapKt(mapTo = SuperNestedModel.InnerEnum::class)
    enum class InnerEnum {
        FIRST,
        SECOND,
        THIRD,
        FOURTH,
    }
}

sealed class SealedClassRoot

@MapKt(mapTo = SealedClassRoot::class)
sealed class LocalSeleadClassRoot

data class TestClassOne(
    val name: String,
) : SealedClassRoot()

data class TestClassTwo(
    val name: String,
) : SealedClassRoot()

@MapKt(mapTo = TestClassOne::class)
data class LocalTestClassOne(
    val name: String,
) : LocalSeleadClassRoot()

@MapKt(mapTo = TestClassTwo::class)
data class LocalTestClassTwo(
    val name: String,
) : LocalSeleadClassRoot()