package edu.oregonstate.mist.api.hr

import edu.oregonstate.mist.api.jsonapi.ResourceObject
import edu.oregonstate.mist.hr.core.Department
import org.junit.Test

import static org.junit.Assert.assertEquals

class DepartmentTest {

    @Test
    void shouldConvertToResoureObject() {
        Department department = new Department(name: "myName", businessCenter: "bc",
                organizationCode: "1234")

        def resourceObject = department.toResourceObject()
        assertEquals(resourceObject.class, ResourceObject.class)
        assertEquals(resourceObject.id, "1234")
        assertEquals(resourceObject.type, "department")
        assertEquals(resourceObject.attributes.name, "myName")
        assertEquals(resourceObject.attributes.businessCenter, "bc")
        assertEquals(resourceObject.attributes.organizationCode, "1234")
    }
}