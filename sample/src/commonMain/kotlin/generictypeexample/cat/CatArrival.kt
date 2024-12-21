package generictypeexample.cat


import generictypeexample.Animal
import generictypeexample.AnimalCareStation
import generictypeexample.dog.DogCareStation
import generictypeexample.dog.ShibaInu

class CatArrival {

    fun main(args: Array<String>) {
        val cat1 = BritishShorthair("Caty", "Grey")
        val cat2 = BritishShorthair("Kitty", "Brown")
        val dog1 = ShibaInu("Akiko", "Orange")
        val dog2 = ShibaInu("Aimi", "Orange")

        /*
        val animalCareStationFromCats: AnimalCareStation<Animal> =
            CatCareStation(cat1, cat2).toAnimalCareStation<Animal>() // Generated extension
        val animalCareStationFromDogs: AnimalCareStation<Animal> =
            DogCareStation(dog1, dog2).toAnimalCareStation<Animal>() // Generated extension

        // TODO: Both of these should not need input parameters here
        val catCareStation: CatCareStation = animalCareStationFromCats.toCatCareStation(cat1, cat2)
        val dogStation: DogCareStation = animalCareStationFromCats.toDogCareStation(dog1, dog2)

        println(animalCareStationFromCats.toString())
        println(animalCareStationFromDogs.toString())*/
    }
}