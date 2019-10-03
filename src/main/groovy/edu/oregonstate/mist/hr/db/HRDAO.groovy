package edu.oregonstate.mist.hr.db

import edu.oregonstate.mist.hr.core.Department
import edu.oregonstate.mist.hr.core.Location
import edu.oregonstate.mist.hr.core.Position
import edu.oregonstate.mist.hr.mapper.DepartmentMapper
import edu.oregonstate.mist.hr.mapper.LocationMapper
import edu.oregonstate.mist.hr.mapper.PositionMapper
import org.skife.jdbi.v2.sqlobject.Bind
import org.skife.jdbi.v2.sqlobject.SqlQuery
import org.skife.jdbi.v2.sqlobject.customizers.Mapper
import edu.oregonstate.mist.contrib.AbstractHRDAO

public interface HRDAO extends Closeable {
    @SqlQuery("SELECT 1 FROM dual")
    Integer checkHealth()

    @Mapper(PositionMapper)
    @SqlQuery(AbstractHRDAO.getPositions)
    List<Position> getPositions(@Bind("businessCenter") String businessCenter)

    @Mapper(DepartmentMapper)
    @SqlQuery(AbstractHRDAO.getDepartments)
    List<Department> getDepartments(@Bind("businessCenter") String businessCenter)

    @SqlQuery(AbstractHRDAO.validateBusinessCenter)
    boolean isValidBC(@Bind("businessCenter") String businessCenter)

    @Mapper(LocationMapper)
    @SqlQuery(AbstractHRDAO.getLocations)
    List<Location> getLocations(@Bind("state") String state,
                                @Bind("name") String name)

    @Mapper(LocationMapper)
    @SqlQuery(AbstractHRDAO.getLocationById)
    Location getLocationById(@Bind("id") String id)

    @Override
    void close()
}
