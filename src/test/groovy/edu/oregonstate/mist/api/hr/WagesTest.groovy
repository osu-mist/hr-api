package edu.oregonstate.mist.api.hr

import edu.oregonstate.mist.hr.core.Wages
import org.junit.Test

import java.time.LocalDate

import static org.junit.Assert.assertTrue
import static org.junit.Assert.assertFalse

class WagesTest {

    @Test
    void testDateInRange() {
        LocalDate startDate = LocalDate.of(2018, 2, 15)
        LocalDate endDate = LocalDate.of(2019, 1, 10)

        Wages wages = new Wages(
                effectiveDateStart: startDate,
                effectiveDateEnd: endDate
        )

        // Test date in the range of the start and end date should return true
        assertTrue(wages.isInEffectiveDateRange(LocalDate.of(2018, 10, 1)))

        // Test date equaling the start or end date should return true
        assertTrue(wages.isInEffectiveDateRange(startDate))
        assertTrue(wages.isInEffectiveDateRange(endDate))

        // Test date one day after the end date or one day before the start date should return false
        assertFalse(wages.isInEffectiveDateRange(startDate.minusDays(1)))
        assertFalse(wages.isInEffectiveDateRange(endDate.plusDays(1)))
    }
}
