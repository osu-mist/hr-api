package edu.oregonstate.mist.hr

import javax.ws.rs.core.UriBuilder
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HrUriBuilder {
    URI endpointUri
    DateTimeFormatter dateFormat

    HrUriBuilder(URI endpointUri, DateTimeFormatter dateFormat) {
        this.endpointUri = endpointUri
        this.dateFormat = dateFormat
    }

    URI locationUri(String locationID, LocalDate date) {
        UriBuilder.fromUri(this.endpointUri)
                .path("hr/locations/{locationID}")
                .queryParam("date", "{date}")
                .build(locationID, date.format(dateFormat))
    }
}
