package edu.oregonstate.mist.api

import edu.oregonstate.mist.api.jsonapi.ResourceObject
import edu.oregonstate.mist.api.jsonapi.ResultObject
import edu.oregonstate.mist.hr.db.BaseHRDAO
import edu.oregonstate.mist.hr.db.HRMockDAO
import edu.oregonstate.mist.hr.resources.HRResource
import org.junit.Before
import org.junit.Test

import javax.ws.rs.core.Response

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

class HRResourceTest {
    public static final int DATA_SIZE = 10
    private final URI endpointUri = new URI("https://www.foo.com/")
    HRResource hrResource

    @Test
    void shouldRequireTypeAndOnlySupportStudents() {
        Response response = hrResource.getPositions("", null)
        assertNotNull(response)
        assertEquals(response.status, 400)
        assertEquals(response.getEntity().class, Error.class)
    }
    @Test
    void shouldListAllPositions() {
        Response response = hrResource.getPositions("bcName", "student")
        assertNotNull(response)
        assertEquals(response.getEntity().class, ResultObject.class)
        assertEquals(response.status, 200)

        ResultObject resultObject = response.getEntity()
        assertNotNull(resultObject.data)
        assertEquals(resultObject.data.class, ArrayList.class)
        assertEquals(resultObject.data.size(), DATA_SIZE * 2)

        resultObject.data.each {
            assertEquals(it.class, ResourceObject.class)
        }
    }

    @Test
    void shouldReturn400ForEmptyListPositions() {
        Response response = hrResource.getPositions("empty", "student")
        assertNotNull(response)
        assertEquals(response.getEntity().class, Error.class)
        assertEquals(response.status, 400)
    }

    @Test
    void shouldRequireBusinessCenterPositions() {
        Response response = hrResource.getPositions("", "student")
        assertNotNull(response)
        assertEquals(response.status, 400)
        assertEquals(response.getEntity().class, Error.class)
        assertEquals(response.getEntity()["developerMessage"],
                "businessCenter (query parameter) is required.")
    }

    @Before
    void setup() {
        BaseHRDAO hrDAO = new HRMockDAO(DATA_SIZE)
        hrResource = new HRResource(hrDAO, endpointUri)
    }

    @Test
    void shouldValidateBusinessCenterPositions() {
        Response response = hrResource.getPositions("invalid-bc", "student")
        assertNotNull(response)
        assertEquals(response.status, 400)
        assertEquals(response.getEntity().class, Error.class)
        assertEquals(response.getEntity()["developerMessage"],
                "The value of businessCenter (query parameter) is invalid.")
    }

    @Test
    void shouldOnlyAcceptStudent() {
        Response response = hrResource.getPositions("bcName", "faculty")
        assertNotNull(response)
        assertEquals(response.status, 400)
        assertEquals(response.getEntity().class, Error.class)
        assertEquals(response.getEntity()["developerMessage"],
                "type (query parameter) is required. " +
                        "'student' is currently the only supported type.")
    }

    @Test
    void shouldListAllDepartments() {
        Response response = hrResource.getDepartments("bcName")
        assertNotNull(response)
        assertEquals(response.getEntity().class, ResultObject.class)
        assertEquals(response.status, 200)

        ResultObject resultObject = response.getEntity()
        assertNotNull(resultObject.data)
        assertEquals(resultObject.data.class, ArrayList.class)
        assertEquals(resultObject.data.size(), DATA_SIZE)

        resultObject.data.each {
            assertEquals(it.class, ResourceObject.class)
        }
    }

    @Test
    void shouldReturn400ForEmptyListDepartments() {
        Response response = hrResource.getDepartments("empty")
        assertNotNull(response)
        assertEquals(response.getEntity().class, Error.class)
        assertEquals(response.status, 400)
    }

    @Test
    void shouldRequireBusinessCenterDepartments() {
        Response response = hrResource.getDepartments("")
        assertNotNull(response)
        assertEquals(response.status, 400)
        assertEquals(response.getEntity().class, Error.class)
    }

    @Test
    void shouldValidateBusinessCenterDepartments() {
        Response response = hrResource.getDepartments("invalid-bc")
        assertNotNull(response)
        assertEquals(response.status, 400)
        assertEquals(response.getEntity().class, Error.class)
    }
}
