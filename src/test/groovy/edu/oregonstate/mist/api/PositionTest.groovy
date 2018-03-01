package edu.oregonstate.mist.api

import edu.oregonstate.mist.api.jsonapi.ResourceObject
import edu.oregonstate.mist.hr.core.Position
import org.junit.Test

import static org.junit.Assert.assertEquals

class PositionTest {

    @Test
    void shouldConvertToResoureObject() {
        Position department = new Position(title: "myTitle", businessCenter: "bc",
                organizationCode: "1234", positionNumber: "C51234", lowSalaryPoint: 10,
                highSalaryPoint: 15.5)

        // Per JUnit: delta - the maximum delta between expected and actual for
        // which both numbers are still considered equal.
        BigDecimal delta = 1e-15

        def resourceObject = department.toResourceObject()
        assertEquals(resourceObject.class, ResourceObject.class)
        assertEquals(resourceObject.id, "C51234")
        assertEquals(resourceObject.type, "position")
        assertEquals(resourceObject.attributes.title, "myTitle")
        assertEquals(resourceObject.attributes.businessCenter, "bc")
        assertEquals(resourceObject.attributes.organizationCode, "1234")
        assertEquals(resourceObject.attributes.positionNumber, "C51234")
        assertEquals(resourceObject.attributes.lowSalaryPoint, 10, delta)
        assertEquals(resourceObject.attributes.highSalaryPoint, 15.5, delta)
    }
}
