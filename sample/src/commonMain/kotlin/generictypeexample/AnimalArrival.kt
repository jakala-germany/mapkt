package generictypeexample

import de.yanneckreiss.kconmapper.generated.toAnimalCareStation
import generictypeexample.cat.BritishShorthair
import generictypeexample.cat.CatCareStation
import generictypeexample.dog.DogCareStation
import generictypeexample.dog.ShibaInu

class AnimalArrival {

    fun main(args: Array<String>) {


        val cat1 = BritishShorthair("Caty", "Grey")
        val cat2 = BritishShorthair("Kitty", "Brown")
        val dog1 = ShibaInu("Akiko", "Orange")
        val dog2 = ShibaInu("Aimi", "Orange")

        val animalCareStationFromCats: AnimalCareStation<Animal> = CatCareStation(cat1, cat2).toAnimalCareStation<Animal>() // Generated extension
        val animalCareStationFromDogs: AnimalCareStation<Animal> = DogCareStation(dog1, dog2).toAnimalCareStation<Animal>() // Generated extension

        println(animalCareStationFromCats.toString())
        println(animalCareStationFromDogs.toString())
    }
}