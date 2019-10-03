package edu.oregonstate.mist.hr.resources

import com.codahale.metrics.annotation.Timed
import edu.oregonstate.mist.api.Resource
import edu.oregonstate.mist.api.jsonapi.ResourceObject
import edu.oregonstate.mist.api.jsonapi.ResultObject
import edu.oregonstate.mist.hr.HrUriBuilder
import edu.oregonstate.mist.hr.core.Location
import edu.oregonstate.mist.hr.db.HRDAO
import groovy.transform.TypeChecked

import javax.annotation.security.PermitAll
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Path("hr")
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
@TypeChecked
class HRResource extends Resource {
    private HRDAO hrDAO
    private HrUriBuilder uriBuilder

    public static final DateTimeFormatter dateFormat = DateTimeFormatter.ISO_LOCAL_DATE

    HRResource(HRDAO hrDAO, URI endpointUri) {
        this.hrDAO = hrDAO
        this.uriBuilder = new HrUriBuilder(endpointUri, dateFormat)
    }

    @Timed
    @GET
    @Path("positions")
    Response getPositions(@QueryParam('businessCenter') String businessCenter,
                          @QueryParam('type') String type) {
        Response businessCenterError = checkBusinessCenter(businessCenter)

        if (businessCenterError) {
            return businessCenterError
        }

        if (!type?.trim() || !type.equalsIgnoreCase("student")) {
            return badRequest("type (query parameter) is required. " +
                    "'student' is currently the only supported type.").build()
        }

        ok(new ResultObject(
                data: hrDAO.getPositions(businessCenter).collect { it.toResourceObject() }
        )).build()
    }

    @Timed
    @GET
    @Path("departments")
    Response getDepartments(@QueryParam('businessCenter') String businessCenter) {
        Response businessCenterError = checkBusinessCenter(businessCenter)

        if (businessCenterError) {
            return businessCenterError
        }

        ok(new ResultObject(
                data: hrDAO.getDepartments(businessCenter).collect { it.toResourceObject() }
        )).build()
    }

    private Response checkBusinessCenter(String businessCenter) {
        if (!businessCenter?.trim()) {
            return badRequest("businessCenter (query parameter) is required.").build()
        } else if (!hrDAO.isValidBC(businessCenter)) {
            return badRequest("The value of businessCenter (query parameter) is invalid.").build()
        } else {
            return null
        }
    }

    @Timed
    @GET
    @Path("locations")
    Response getLocations(@QueryParam('date') String date,
                          @QueryParam('state') String state,
                          @QueryParam('name') String name) {
        LocalDate effectiveDate

        try {
            effectiveDate = parseDate(date)
        } catch (DateTimeParseException) {
            return badDateResponse()
        }

        Location.minimumWageDate = effectiveDate

        ok(new ResultObject(
                data: hrDAO.getLocations(state, name).collect {
                    new ResourceObject(
                            id: it.id,
                            type: Location.type,
                            attributes: it,
                            links: ["self": uriBuilder.locationUri(it.id, effectiveDate)]
                    )
                }
        )).build()
    }

    @Timed
    @GET
    @Path("locations/{id}")
    Response getLocationById(@PathParam('id') String locationID,
                             @QueryParam('date') String date) {
        LocalDate effectiveDate

        try {
            effectiveDate = parseDate(date)
        } catch (DateTimeParseException) {
            return badDateResponse()
        }

        Location.minimumWageDate = effectiveDate

        Location location = hrDAO.getLocationById(locationID)

        if (!location) {
            notFound().build()
        } else {
            ok(new ResultObject(
                    data: new ResourceObject(
                            id: location.id,
                            type: Location.type,
                            attributes: location,
                            links: ["self": uriBuilder.locationUri(location.id, effectiveDate)]
                    ))).build()
        }
    }

    private LocalDate parseDate(String formattedDate) throws DateTimeParseException {
        if (formattedDate) {
            LocalDate.parse(formattedDate, dateFormat)
        } else {
            LocalDate.now()
        }
    }

    private Response badDateResponse() {
        badRequest("Invalid date. " +
                "Date must follow a full-date per ISO 8601. Example: 2017-12-31").build()
    }
}
