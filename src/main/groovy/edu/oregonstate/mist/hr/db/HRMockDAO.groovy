package edu.oregonstate.mist.hr.db

import edu.oregonstate.mist.hr.core.Position

class HRMockDAO extends BaseHRDAO implements HRDAO {
    private static List<String> titles = ["Office Manager", "Retail Food Service",
                                          "Mock Dept", "Science Lab"]
    public int positionSize = 0

    private List<String> takenPositions = new ArrayList<>()

    HRMockDAO(int positionSize) {
        this.positionSize = positionSize
    }

    @Override
    List<Position> getPositions(String businessCenter) {
        if (businessCenter == "empty") {
            return generate(0, null)
        }

        generate(positionSize, businessCenter)
    }

    List<Position> generate(int size, String businessCenter) {
        List<Position> result = new ArrayList<>()
        if (size) {
            size.times {

                // add two positions so to the same dept
                result += singlePosition(it, businessCenter)
                result += singlePosition(it, businessCenter)
            }
        }
        result
    }

    private Position singlePosition(int it, String businessCenter) {
        def random = new Random()
        new Position(
                title: chooseTitle(),
                businessCenter: businessCenter,
                positionNumber: getPositionNumber(random),
                organizationCode: 1111 + it,
                lowSalaryPoint: 10,
                highSalaryPoint: 15.5
        )
    }

    private String getPositionNumber(Random random) {
        String candidateNumber = ""
        while(candidateNumber == "" || takenPositions.contains(candidateNumber)) {
            candidateNumber = random.nextInt( 999999).toString()
        }

        takenPositions += candidateNumber
        "C5" + candidateNumber
    }

    private static String chooseTitle() {
        def random = new Random()
        titles[random.nextInt(titles.size())] + " " +
                random.nextInt(111)
    }

    boolean isValidBC(String businessCenter) {
        def invalidBusinessCenters = ["empty", "invalid-bc"]
        !invalidBusinessCenters.contains(businessCenter)
    }

    @Override
    void close() { }

    @Override
    Integer checkHealth() {
        return null
    }
}
