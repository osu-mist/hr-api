package edu.oregonstate.mist.hr.mapper

import edu.oregonstate.mist.contrib.AbstractHRDAO
import edu.oregonstate.mist.hr.core.Position
import org.skife.jdbi.v2.StatementContext
import org.skife.jdbi.v2.tweak.ResultSetMapper

import java.sql.ResultSet
import java.sql.SQLException

class PositionMapper implements ResultSetMapper<Position> {
    public Position map(int i, ResultSet rs, StatementContext sc) throws SQLException {
        new Position(
                title: rs.getString(AbstractHRDAO.mapperColumnTitle),
                positionClass: rs.getString(AbstractHRDAO.mapperColumnPositionClassDesc),
                businessCenter: rs.getString("businessCenter"),
                positionNumber: rs.getString(AbstractHRDAO.mapperColumnPositionNumber),
                organizationCode: rs.getString(AbstractHRDAO.mapperColumnPositionReports),
                nationalOccupationCode: rs.getString(
                        AbstractHRDAO.mapperColumnNationalOccupationCode),
                nationalOccupationCodeDescription: rs.getString(
                        AbstractHRDAO.mapperColumnNationalOccupationCodeDesc),
                lowSalaryPoint: rs.getBigDecimal(AbstractHRDAO.mapperColumnLowSalary),
                highSalaryPoint: rs.getBigDecimal(AbstractHRDAO.mapperColumnHighSalary)
        )
    }

}