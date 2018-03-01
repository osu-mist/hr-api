package edu.oregonstate.mist.hr.core

import edu.oregonstate.mist.api.jsonapi.ResourceObject

class Department {
    String name
    String businessCenter
    String organizationCode

    ResourceObject toResourceObject() {
        new ResourceObject(
                type: "department",
                id: organizationCode,
                attributes: [
                        name: name,
                        businessCenter: businessCenter,
                        organizationCode: organizationCode
                ]
        )
    }
}
