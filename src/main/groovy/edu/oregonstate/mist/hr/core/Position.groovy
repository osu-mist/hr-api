package edu.oregonstate.mist.hr.core

import edu.oregonstate.mist.api.jsonapi.ResourceObject

class Position {
    String title
    String positionClass
    String businessCenter
    String positionNumber
    String organizationCode
    String nationalOccupationCode
    String nationalOccupationCodeDescription

    BigDecimal lowSalaryPoint
    BigDecimal highSalaryPoint

    ResourceObject toResourceObject() {
        new ResourceObject(
                type: "position",
                id: positionNumber,
                attributes: [
                        title: title,
                        class: positionClass,
                        businessCenter: businessCenter,
                        positionNumber: positionNumber,
                        organizationCode: organizationCode,
                        nationalOccupationCode: nationalOccupationCode,
                        nationalOccupationCodeDescription: nationalOccupationCodeDescription,
                        lowSalaryPoint: lowSalaryPoint,
                        highSalaryPoint: highSalaryPoint
                ]
        )
    }

}
