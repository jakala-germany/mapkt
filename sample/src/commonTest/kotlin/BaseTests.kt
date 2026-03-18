package commonTest

import com.jakala.mapkt.toAddress
import com.jakala.mapkt.toComplexSubType
import com.jakala.mapkt.toCreateUserDTO
import com.jakala.mapkt.toEnumClass
import com.jakala.mapkt.toEnumSample
import com.jakala.mapkt.toInnerEnum
import com.jakala.mapkt.toLocalNestedModel
import com.jakala.mapkt.toLocalSuperModel
import com.jakala.mapkt.toMappedEnumClass
import com.jakala.mapkt.toRemoteSomeProperty
import com.jakala.mapkt.toRemoteSuperModel
import com.jakala.mapkt.toSomeProperty
import com.jakala.mapkt.toSuperModel
import com.jakala.mapkt.toSuperNestedModel
import com.jakala.mapkt.toTestClassOne
import com.jakala.mapkt.toTestClassTwo
import com.jakala.mapkt.toUpdateUserDTO
import com.jakala.mapkt.toUserEntity
import entitysample.LocalNestedModel
import entitysample.LocalSuperModel
import entitysample.LocalTestClassOne
import entitysample.LocalTestClassTwo
import entitysample.RemoteSuperModel
import entitysample.SuperModel
import entitysample.SuperNestedModel
import entitysample.db.UserEntity
import entitysample.dto.AddressLocal
import entitysample.dto.AddressRemote
import entitysample.dto.ComplexSubTypeLocal
import entitysample.dto.ComplexSubTypeRemote
import entitysample.dto.CreateUserDTO
import entitysample.dto.UpdateUserDTO
import entitysample.model.Address
import entitysample.playground.defaults.MappedEnumSample
import entitysample.properties.RemoteSomeProperty
import entitysample.properties.SomeProperty
import enumSample.EnumClass
import enumSample.MappedEnumClass
import kotlin.test.Test
import kotlin.test.fail

class BaseTests {
    @Test
    fun testLocalSuperModelToSuperModel() {
        val local = LocalSuperModel(name = "John")
        val superModel = local.toSuperModel()

        assertEquals("John", superModel.name)
    }

    @Test
    fun testRemoteSuperModelToSuperModel() {
        val remote = RemoteSuperModel(name = "Jane")
        val superModel = remote.toSuperModel()

        assertEquals("Jane", superModel.name)
    }

    @Test
    fun testSuperModelToLocalSuperModel() {
        val superModel = SuperModel(name = "Bob")
        val local = superModel.toLocalSuperModel()

        assertEquals("Bob", local.name)
    }

    @Test
    fun testSuperModelToRemoteSuperModel() {
        val superModel = SuperModel(name = "Alice")
        val remote = superModel.toRemoteSuperModel()

        assertEquals("Alice", remote.name)
    }

    @Test
    fun testLocalNestedModelToSuperNestedModel() {
        val localInner1 = LocalNestedModel.Inner1(name = "Inner1Name")
        val superNested = localInner1.toSuperNestedModel()

        assertTrue(superNested is SuperNestedModel.Inner1)
        assertEquals("Inner1Name", (superNested as SuperNestedModel.Inner1).name)
    }

    @Test
    fun testLocalNestedModelInner2ToSuperNestedModel() {
        val localInner2 = LocalNestedModel.Inner2(name = "Inner2Name")
        val superNested = localInner2.toSuperNestedModel()

        assertTrue(superNested is SuperNestedModel.Inner2)
        assertEquals("Inner2Name", (superNested as SuperNestedModel.Inner2).name)
    }

    @Test
    fun testLocalNestedModelInnerEnumToSuperNestedModel() {
        val localEnum = LocalNestedModel.InnerEnum.FIRST
        val superEnum = localEnum.toInnerEnum()

        assertEquals(SuperNestedModel.InnerEnum.FIRST, superEnum)
    }

    @Test
    fun testSuperNestedModelToLocalNestedModel() {
        val superInner1 = SuperNestedModel.Inner1(name = "ConvertedInner1")
        val localInner1 = superInner1.toLocalNestedModel()

        assertTrue(localInner1 is LocalNestedModel.Inner1)
        assertEquals("ConvertedInner1", (localInner1 as LocalNestedModel.Inner1).name)
    }

    @Test
    fun testLocalNopiToNopi2() {
        val localTestClassOne = LocalTestClassOne(name = "TestName")
        val nopi2 = localTestClassOne.toTestClassOne()

        assertEquals("TestName", nopi2.name)
    }

    @Test
    fun testLocalNopi2ToLocalNopi() {
        val localNopi2 = LocalTestClassTwo(name = "TestName2")
        val nopi = localNopi2.toTestClassTwo()

        assertEquals("TestName2", nopi.name)
    }

    @Test
    fun testUserEntityToCreateUserDTO() {
        val address =
            Address(street = "123 Main St", zip = "12345", houseNumber = 42, city = "New York")
        val user = UserEntity(name = "John Doe", address = address)

        val dto = user.toCreateUserDTO()

        assertEquals("John Doe", dto.name)
        assertEquals("123 Main St", dto.address.street)
        assertEquals("12345", dto.address.zip)
        assertEquals(42, dto.address.houseNumber)
        assertEquals("New York", dto.address.city)
    }

    @Test
    fun testUserEntityToUpdateUserDTO() {
        val address =
            Address(street = "456 Oak Ave", zip = "67890", houseNumber = 13, city = "Los Angeles")
        val user = UserEntity(name = "Jane Doe", address = address)

        val dto = user.toUpdateUserDTO()

        assertEquals("Jane Doe", dto.name)
        assertEquals("456 Oak Ave", dto.address.street)
        assertEquals("67890", dto.address.zip)
        assertEquals(13, dto.address.houseNumber)
        assertEquals("Los Angeles", dto.address.city)
    }

    @Test
    fun testCreateUserDTOToUserEntity() {
        val address =
            Address(street = "789 Pine Rd", zip = "11223", houseNumber = 5, city = "Chicago")
        val dto = CreateUserDTO(name = "Bob Smith", address = address)

        val entity = dto.toUserEntity()

        assertEquals("Bob Smith", entity.name)
        assertTrue(entity.address == dto.address)
    }

    @Test
    fun testUpdateUserDTOToUserEntity() {
        val address =
            Address(street = "321 Elm St", zip = "44556", houseNumber = 8, city = "Houston")
        val dto = UpdateUserDTO(name = "Alice Brown", address = address)

        val entity = dto.toUserEntity()

        assertEquals("Alice Brown", entity.name)
        assertTrue(entity.address == dto.address)
    }

    @Test
    fun testComplexSubTypeRemoteToLocal() {
        val remote =
            ComplexSubTypeRemote(
                name = "ComplexName",
                address = AddressRemote("addr1", "zip1", 100, "city1"),
                addresses = listOf(AddressRemote("a1", "z1", 1, "c1")),
            )

        val local = remote.toComplexSubType()

        assertEquals("ComplexName", local.name)
    }

    @Test
    fun testComplexSubTypeLocalToRemote() {
        val local =
            ComplexSubTypeLocal(
                name = "ComplexLocalName",
                address = AddressLocal("addr2", "zip2", 200, "city2"),
                addresses = listOf(AddressLocal("a2", "z2", 2, "c2")),
            )

        val remote = local.toComplexSubType()

        assertEquals("ComplexLocalName", remote.name)
    }

    @Test
    fun testAddressRemoteToLocal() {
        val remote =
            AddressRemote(
                street = "RemoteStreet",
                zip = "12345",
                houseNumber = 1,
                city = "City",
            )

        val local = remote.toAddress()

        assertEquals("RemoteStreet", local.street)
    }

    @Test
    fun testAddressLocalToRemote() {
        val local =
            AddressLocal(
                street = "LocalStreet",
                zip = "67890",
                houseNumber = 2,
                city = "City",
            )

        val remote = local.toAddress()

        assertEquals("LocalStreet", remote.street)
    }

    @Test
    fun testComplexSubTypeWithNulls() {
        val remote =
            ComplexSubTypeRemote(
                name = "TestName",
                address = null,
                addresses = null,
            )

        val local = remote.toComplexSubType()

        assertTrue(local.name == "TestName", "Expected name to be 'TestName'")
    }

    @Test
    fun testEnumToLocalEnumClass() {
        val enumItem = EnumClass.FIRST
        val mapped = enumItem.toMappedEnumClass()

        assertEquals(EnumClass.FIRST.toString(), mapped.toString())
    }

    @Test
    fun testMappedEnumClassToEnum() {
        val mapped = MappedEnumClass.SECOND
        val local = mapped.toEnumClass()

        assertEquals(MappedEnumClass.SECOND.toString(), local.toString())
    }

    @Test
    fun testMappedEnumSampleToLocalNestedModel() {
        val mapped = MappedEnumSample.FIRST
        val nested = mapped.toEnumSample()

        assertEquals(MappedEnumSample.FIRST.toString(), nested.toString())
    }

    @Test
    fun testPropertyAliases() {
        val someProperty =
            SomeProperty(
                name = "Some name",
                value = "Some value",
                newValue = "Some new value",
            )

        val remoteSomeProperty = someProperty.toRemoteSomeProperty()

        assertEquals(remoteSomeProperty.fullName, someProperty.name)
        assertEquals(remoteSomeProperty.value, someProperty.value)
    }

    @Test
    fun testIgnoredPropertyRequiresManualMapping() {
        val remoteSomeProperty =
            RemoteSomeProperty(
                fullName = "Some name",
                value = "Some value",
            )

        val someProperty = remoteSomeProperty.toSomeProperty("Some new value")

        assertEquals(someProperty.name, remoteSomeProperty.fullName)
        assertEquals(someProperty.value, remoteSomeProperty.value)
        assertEquals(someProperty.newValue, "Some new value")
    }

    @Test
    fun `ensure that repeatable annotation works`() {
        val a = UserEntity(name = "a", address = Address("a", "a", 1, "a"))
        a.toUpdateUserDTO()
        a.toCreateUserDTO()
    }

    private fun assertTrue(
        condition: Boolean,
        message: String? = null,
    ) {
        if (!condition) {
            fail("Test failed: ${message ?: "Expected condition to be true"}")
        }
    }

    private fun assertEquals(
        expected: Any?,
        actual: Any?,
        message: String? = null,
    ) {
        if (expected != actual) {
            fail("Test failed: ${message ?: "Expected $expected but got $actual"}")
        }
    }
}