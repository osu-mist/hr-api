package edu.oregonstate.mist.hr.db

import edu.oregonstate.mist.hr.core.Department
import edu.oregonstate.mist.hr.core.Location
import edu.oregonstate.mist.hr.core.Position

abstract class BaseHRDAO {
    List<Position> getPositions(String businessCenter) {
        new ArrayList<Position>()
    }

    List<Department> getDepartments(String businessCenter)  {
        new ArrayList<Department>()
    }

    List<Location> getLocations(String state, String name) {
        new ArrayList<Location>()
    }

    Location getLocationById(String id) {
        new Location()
    }
}
