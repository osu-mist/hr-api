package edu.oregonstate.mist.hr.core

import edu.oregonstate.mist.api.jsonapi.ResourceObject

class Position {
    String title
    String businessCenter
    String positionNumber
    String organizationCode

    BigDecimal lowSalaryPoint
    BigDecimal highSalaryPoint

    ResourceObject toResourceObject() {
        new ResourceObject(
                type: "position",
                id: positionNumber,
                attributes: [
                        title: title,
                        businessCenter: businessCenter,
                        positionNumber: positionNumber,
                        organizationCode: organizationCode,
                        lowSalaryPoint: lowSalaryPoint,
                        highSalaryPoint: highSalaryPoint
                ]
        )
    }

}
