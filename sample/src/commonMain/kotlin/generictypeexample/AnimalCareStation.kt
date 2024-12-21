package generictypeexample

import com.github.yanneckreiss.kconmapper.annotations.KConMapper
import generictypeexample.cat.CatCareStation
import generictypeexample.dog.DogCareStation

@KConMapper(
    toClasses = [CatCareStation::class, DogCareStation::class],
    fromClasses = [CatCareStation::class, DogCareStation::class],
)
class AnimalCareStation<out C : Animal>(
    val animalOne: C,
    val animalTwo: C,
)
