package edu.oregonstate.mist.hr.mapper

import edu.oregonstate.mist.contrib.AbstractHRDAO
import edu.oregonstate.mist.hr.core.Department
import org.skife.jdbi.v2.StatementContext
import org.skife.jdbi.v2.tweak.ResultSetMapper

import java.sql.ResultSet
import java.sql.SQLException

class DepartmentMapper implements ResultSetMapper<Department> {
    public Department map(int i, ResultSet rs, StatementContext sc) throws SQLException {
        new Department(
                name: rs.getString(AbstractHRDAO.mapperColumnDepartmentName),
                businessCenter: rs.getString(AbstractHRDAO.mapperColumnBusinessCenter),
                organizationCode: rs.getString(AbstractHRDAO.mapperColumnOrganizationCode)
        )
    }
}
