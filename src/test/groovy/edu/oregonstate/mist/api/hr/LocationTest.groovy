package edu.oregonstate.mist.api.hr

import edu.oregonstate.mist.hr.core.Location
import org.junit.Test

import java.time.LocalDate

import static org.junit.Assert.assertNull
import static org.junit.Assert.assertNotNull

class LocationTest {

    /**
     * Minimum wage should be null if the location classification is null or not.
     */
    @Test
    void minimumWageNullTest() {
        Location.minimumWageDate = LocalDate.now()

        Location location = new Location(minimumWageClassification: null)

        assertNull(location.getMinimumWage())
    }

    /**
     * The current date should return a minimum wage for all location types.
     */
    @Test
    void testGoodMinimumWage() {
        Location.minimumWageDate = LocalDate.now()

        Location standardLocation = new Location(
                minimumWageClassification: Location.standardMinWageClass
        )
        Location urbanLocation = new Location(
                minimumWageClassification: Location.urbanMinWageClass
        )
        Location nonUrbanLocation = new Location(
                minimumWageClassification: Location.nonUrbanMinWageClass
        )

        assertNotNull(standardLocation.getMinimumWage())
        assertNotNull(urbanLocation.getMinimumWage())
        assertNotNull(nonUrbanLocation.getMinimumWage())
    }
}
