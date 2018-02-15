package edu.oregonstate.mist.hr.db

import edu.oregonstate.mist.hr.core.Position
import edu.oregonstate.mist.hr.mapper.PositionMapper
import org.skife.jdbi.v2.sqlobject.Bind
import org.skife.jdbi.v2.sqlobject.SqlQuery
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper
import edu.oregonstate.mist.contrib.AbstractHRDAO

@RegisterMapper(PositionMapper)
public interface HRDAO extends Closeable {
      @SqlQuery("SELECT 1 FROM dual")
       Integer checkHealth()

      @SqlQuery(AbstractHRDAO.getPositions)
      List<Position> getPositions(@Bind("businessCenter") String businessCenter)

      @SqlQuery(AbstractHRDAO.validateBusinessCenter)
      boolean isValidBC(@Bind("businessCenter") String businessCenter)

        @Override
        void close()
    }