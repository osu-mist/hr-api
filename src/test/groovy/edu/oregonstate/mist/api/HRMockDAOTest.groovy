package edu.oregonstate.mist.api

import edu.oregonstate.mist.hr.db.HRMockDAO
import org.junit.Test

import static org.junit.Assert.*

class HRMockDAOTest {
    @Test
    void shouldReturnEmptyPositionList() {
        assert !new HRMockDAO(10).generate(0, null)
    }

    @Test
    void shouldGenerateManyPositions() {
        (1..10).each {
            assertEquals(new HRMockDAO(10).generate(it, null).size(), it * 2)
        }
    }

    @Test
    void shouldNotGenerateNegativePositions() {
        (-10..-1).each {
            assertEquals(new HRMockDAO(10).generate(it, null).size(), 0)
        }
    }

    @Test
    void shouldReturnPositionsSpecifiedInConstructor() {
        HRMockDAO hrMockDAO
        (1..10).each {
            hrMockDAO = new HRMockDAO(it)
            def positions = hrMockDAO.getPositions("abc")
            assertEquals(positions.size(), it * 2)
            positions.each { assertEquals(it.businessCenter, "abc")}
        }
    }

    @Test
    void shouldReturnEmptyListForEmpty() {
        HRMockDAO hrMockDAO
        (1..10).each {
            hrMockDAO = new HRMockDAO(it)
            assertTrue(hrMockDAO.getPositions("empty").isEmpty())
        }
    }

    @Test
    void shouldGenerateOrganizationCodesInLimitedRange() {
        new HRMockDAO(10).generate(100, null).each {
            def difference = Math.abs(Integer.valueOf(it.organizationCode) - 1111)
            assertTrue(difference <= 100)
        }
    }

    @Test
    void positionNumberShouldBeUnique() {
        ["abc", "bcd", "xyz", "ccc"].each {
            checkPositionNumbersUniqueInBC(it)
        }
    }

    void checkPositionNumbersUniqueInBC(String businessCenter) {
        def positions = new HRMockDAO(10).generate(2000, businessCenter)

        def positionNumbers =  positions.positionNumber
        def uniquePositionNumbers = positionNumbers.unique()

        assertEquals(positionNumbers, uniquePositionNumbers)
    }

    @Test
    void invalidBcIsInvalid() {
        def mockDAO = new HRMockDAO(1)
        assertFalse mockDAO.isValidBC("invalid-bc")
    }
}
