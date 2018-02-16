package edu.oregonstate.mist.api

import edu.oregonstate.mist.hr.db.HRMockDAO
import org.junit.Test

import static org.junit.Assert.*

class HRMockDAOTest {
    @Test
    void shouldReturnEmptyPositionList() {
        assert !new HRMockDAO(10).generatePositions(0, null)
    }

    @Test
    void shouldGenerateManyPositions() {
        (1..10).each {
            assertEquals(new HRMockDAO(10).generatePositions(it, null).size(), it * 2)
        }
    }

    @Test
    void shouldNotGenerateNegativePositions() {
        (-10..-1).each {
            assertEquals(new HRMockDAO(10).generatePositions(it, null).size(), 0)
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
    void shouldReturnEmptyListForEmptyPositions() {
        HRMockDAO hrMockDAO
        (1..10).each {
            hrMockDAO = new HRMockDAO(it)
            assertTrue(hrMockDAO.getPositions("empty").isEmpty())
        }
    }

    @Test
    void shouldGenerateOrganizationCodesInLimitedRangePositions() {
        new HRMockDAO(10).generatePositions(100, null).each {
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
        def positions = new HRMockDAO(10).generatePositions(2000, businessCenter)

        def positionNumbers =  positions.positionNumber
        def uniquePositionNumbers = positionNumbers.unique()

        assertEquals(positionNumbers, uniquePositionNumbers)
    }

    @Test
    void shouldReturnEmptyList() {
        assert !HRMockDAO.generateDepartments(0, null)
    }

    @Test
    void shouldGenerateManyDepartments() {
        (1..10).each {
            assertEquals(HRMockDAO.generateDepartments(it, null).size(), it)
        }
    }

    @Test
    void shouldNotGenerateNegativeDepartments() {
        (-10..-1).each {
            assertEquals(HRMockDAO.generateDepartments(it, null).size(), 0)
        }
    }

    @Test
    void shouldReturnDepartmentsSpecifiedInConstructor() {
        HRMockDAO hrMockDAO
        (1..10).each {
            hrMockDAO = new HRMockDAO(it)
            def departments = hrMockDAO.getDepartments("abc")
            assertEquals(departments.size(), it)
            departments.each { assertEquals(it.businessCenter, "abc")}
        }
    }

    @Test
    void shouldReturnEmptyListForEmptyDepartments() {
        HRMockDAO hrMockDAO
        (1..10).each {
            hrMockDAO = new HRMockDAO(it)
            assertTrue(hrMockDAO.getDepartments("empty").isEmpty())
        }
    }

    @Test
    void shouldGenerateOrganizationCodesInLimitedRangeDepartments() {
        new HRMockDAO(100).getDepartments("abc").each {
            def difference = Math.abs(Integer.valueOf(it.organizationCode) - 1111)
            assert(difference <= 100)
        }
    }

    @Test
    void organizationCodeShouldBeUnique() {
        def orgCodes = new HRMockDAO(100).getDepartments("abc").organizationCode
        def uniqueOrgCodes = orgCodes.unique()

        assertEquals(orgCodes, uniqueOrgCodes)
    }

    @Test
    void invalidBcIsInvalid() {
        def mockDAO = new HRMockDAO(1)
        assertFalse mockDAO.isValidBC("invalid-bc")
    }
}
