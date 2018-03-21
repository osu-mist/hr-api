package edu.oregonstate.mist.api.hr

import edu.oregonstate.mist.api.Error
import edu.oregonstate.mist.api.jsonapi.ResourceObject
import edu.oregonstate.mist.api.jsonapi.ResultObject
import edu.oregonstate.mist.hr.db.BaseHRDAO
import edu.oregonstate.mist.hr.db.HRMockDAO
import edu.oregonstate.mist.hr.resources.HRResource
import org.junit.Before
import org.junit.Test

import javax.ws.rs.core.Response
import javax.ws.rs.core.UriBuilder
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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

    @Test
    void getLocationsShouldRejectBadDate() {
        rejectBadDates(hrResource.getLocations("31-12-2019", null))
    }

    @Test
    void getLocationByIdShouldRejectBadDate() {
        rejectBadDates(hrResource.getLocationById(null, "31-12-2019"))
    }

    private void rejectBadDates(Response response) {
        assertNotNull(response)
        assertEquals(response.status, 400)
        assertEquals(response.getEntity().class, Error.class)

        Error error = response.getEntity()

        assertEquals(error.developerMessage, "Invalid date. Date must follow a full-date " +
                "per ISO 8601. Example: 2017-12-31")
    }

    @Test
    void getLocationsShouldAcceptGoodDate() {
        String goodISO8601Date = "2017-12-31"
        Response locationsResponse = hrResource.getLocations(goodISO8601Date, null)

        assertNotNull(locationsResponse.entity)
        assertEquals(locationsResponse.status, 200)

        ResourceObject sampleLocation = locationsResponse.entity.data[0]

        Response locationResponse = hrResource.getLocationById(sampleLocation.id, goodISO8601Date)

        assertNotNull(locationResponse.entity)
        assertEquals(locationResponse.status, 200)
    }

    @Test
    void getLocationByIdShouldReturn404() {
        String badID = "fooBar"
        Response response = hrResource.getLocationById(badID, null)

        assertEquals(response.status, 404)
    }

    @Test
    void testSelfLink() {
        String date = "2015-09-20"
        Response response = hrResource.getLocations(date, null)

        assertEquals(date, parseDateFromSelfLink(response))
    }

    @Test
    void defaultDateShouldBeCurrentDate() {
        Response response = hrResource.getLocations(null, null)
        String currentDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

        assertEquals(currentDate, parseDateFromSelfLink(response))
    }

    String parseDateFromSelfLink(Response response) {
        ResourceObject sampleLocation = response.getEntity().data[0]
        String selfLink = sampleLocation.links["self"]
        URI selfLinkUri = UriBuilder.fromUri(selfLink).build()

        selfLinkUri.getQuery().split("=")[1]
    }
}
