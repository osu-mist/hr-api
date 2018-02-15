package edu.oregonstate.mist.hr.db

import edu.oregonstate.mist.hr.core.Position

abstract class BaseHRDAO {
    List<Position> getPositions(String businessCenter) {
        new ArrayList<Position>()
    }
}