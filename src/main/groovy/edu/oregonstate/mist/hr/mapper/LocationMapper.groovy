package edu.oregonstate.mist.hr.mapper

import edu.oregonstate.mist.hr.core.Location
import org.skife.jdbi.v2.StatementContext
import org.skife.jdbi.v2.tweak.ResultSetMapper

import java.sql.ResultSet
import java.sql.SQLException

class LocationMapper implements ResultSetMapper<Location> {
    private final String oregon = "Oregon"
    private final String oregonStateCode = "OR"

    private final Map minWageMap = [
            "MWS": Location.standardMinWageClass,
            "MWP": Location.urbanMinWageClass,
            "MWR": Location.nonUrbanMinWageClass
    ].withDefault { key -> 'New minimum wage location code. Please contact API support.'}

    public Location map(int i, ResultSet rs, StatementContext sc) throws SQLException {
        new Location(
                id: rs.getString("id"),
                name: rs.getString("name"),
                county: rs.getString("county"),
                city: getCity(rs.getString("city"), rs.getString("state_code")),
                state: getState(rs.getString("city"), rs.getString("state_code")),
                stateCode: rs.getString("state_code"),
                minimumWageClassification: mapMinimumWageClassification(
                        rs.getString("min_wage_class"))
        )
    }

    /**
     * Data source uses one column for city, but sometimes uses it for state too.
     * Only Oregon locations use the city column for a city, therefore we should
     * only use the city column if the state code is OR.
     * @param city
     * @param stateCode
     * @return
     */
    private String getCity(String city, String stateCode) {
        if (isOregon(stateCode)) {
            return city
        }

        // If state is not Oregon, the city column is being used as a state name,
        // therefore return null
        null
    }

    /**
     * Data source uses city column for state name, except if the state is Oregon.
     * @param city
     * @param stateCode
     * @return
     */
    private String getState(String city, String stateCode) {
        if (isOregon(stateCode)) {
            return oregon
        }

        // If state is not Oregon, the city column actually contains the state name
        city
    }

    private Boolean isOregon(String stateCode) {
        stateCode == oregonStateCode
    }

    private String mapMinimumWageClassification(String locationCode) {
        if (locationCode) {
           return minWageMap[locationCode]
        }
        null
    }
}
