package edu.oregonstate.mist.hr.db

import edu.oregonstate.mist.hr.core.Department
import edu.oregonstate.mist.hr.core.Position

class HRMockDAO extends BaseHRDAO implements HRDAO {
    private static List<String> titles = ["Office Manager", "Retail Food Service",
                                          "Mock Position", "Science Lab Worker"]

    private List<String> takenPositions = new ArrayList<>()

    private static List<String> businessCenters = ["UABC", "AABC", "FOBC"]

    private static List<String> departments = ["HR Sample", "Finance Dept",
                                               "Mock Dept", "Audits"]
    // Number of positions/departments to return
    public int size = 0

    HRMockDAO(int size) {
        this.size = size
    }

    @Override
    List<Position> getPositions(String businessCenter) {
        if (businessCenter == "empty") {
            return generatePositions(0, null)
        }

        generatePositions(size, businessCenter)
    }

    List<Position> generatePositions(int size, String businessCenter) {
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
            candidateNumber = random.nextInt(999999).toString()
        }

        takenPositions += candidateNumber
        "C5" + candidateNumber
    }

    private static String chooseTitle() {
        def random = new Random()
        titles[random.nextInt(titles.size())] + " " +
                random.nextInt(111)
    }

    static List<Department> generateDepartments(int size, String businessCenter) {
        List<Department> result = new ArrayList<>()

        if (size) {
            size.times {
                result += new Department(
                        name: chooseName(),
                        businessCenter: businessCenter ?: chooseBusinessCenter(),
                        organizationCode: 1111 + it
                )
            }
        }
        result
    }

    private static String chooseBusinessCenter() {
        def random = new Random()
        businessCenters[random.nextInt(businessCenters.size())]
    }

    private static String chooseName() {
        def random = new Random()
        departments[random.nextInt(departments.size())] + " " +
                random.nextInt(111)
    }

    List<Department> getDepartments(String businessCenter) {
        if (businessCenter == "empty") {
            return generateDepartments(0, null)
        }

        generateDepartments(size, businessCenter)
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
