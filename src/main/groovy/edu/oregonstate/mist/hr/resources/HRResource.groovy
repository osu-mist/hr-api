package edu.oregonstate.mist.hr.resources

import com.codahale.metrics.annotation.Timed
import edu.oregonstate.mist.api.Resource
import edu.oregonstate.mist.api.jsonapi.ResultObject
import edu.oregonstate.mist.hr.db.HRDAO
import groovy.transform.TypeChecked

import javax.annotation.security.PermitAll
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("positions")
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
@TypeChecked
class HRResource extends Resource {
    private HRDAO hrDAO

    HRResource(HRDAO hrDAO) {
        this.hrDAO = hrDAO
    }

    @Timed
    @GET
    Response getPositions(@QueryParam('businessCenter') String businessCenter,
                          @QueryParam('type') String type) {
        if (!businessCenter?.trim()) {
            return badRequest("businessCenter (query parameter) is required.").build()
        }

        if (!hrDAO.isValidBC(businessCenter)) {
            return badRequest("The value of businessCenter (query parameter) is invalid.").build()
        }

        if (!type?.trim() || !type.equalsIgnoreCase("student")) {
            return badRequest("type (query parameter) is required. " +
                    "'student' is currently the only supported type.").build()
        }

        ok(new ResultObject(
                data: hrDAO.getPositions(businessCenter).collect { it.toResourceObject() }
        )).build()
    }

}
