package edu.oregonstate.mist.hr.core

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

import java.time.LocalDate

class Location {
    static final String type = "Location"

    // Desired date used to calculate minimum wage.
    static LocalDate minimumWageDate

     // Classifications of locations related to minimum wage.
    static final String standardMinWageClass = "Standard"
    static final String urbanMinWageClass = "Urban"
    static final String nonUrbanMinWageClass = "Nonurban"

    /**
     * List of Wages objects used with getMinimumWage().
     * Data copied from http://www.oregon.gov/boli/whd/omw/pages/minimum-wage-rate-summary.aspx
     */
    private static final List<Wages> minimumWages = [
            new Wages(effectiveDateStart: LocalDate.of(2022, 7, 1),
                    effectiveDateEnd: LocalDate.of(2023, 6, 30),
                    standardWage: 13.5, urbanWage: 14.75, nonurbanWage: 12.5),

            new Wages(effectiveDateStart: LocalDate.of(2021, 7, 1),
                    effectiveDateEnd: LocalDate.of(2022, 6, 30),
                    standardWage: 12.75, urbanWage: 14, nonurbanWage: 12),

            new Wages(effectiveDateStart: LocalDate.of(2020, 7, 1),
                    effectiveDateEnd: LocalDate.of(2021, 6, 30),
                    standardWage: 12, urbanWage: 13.25, nonurbanWage: 11.5),

            new Wages(effectiveDateStart: LocalDate.of(2019, 7, 1),
                    effectiveDateEnd: LocalDate.of(2020, 6, 30),
                    standardWage: 11.25, urbanWage: 12.5, nonurbanWage: 11),

            new Wages(effectiveDateStart: LocalDate.of(2018, 7, 1),
                    effectiveDateEnd: LocalDate.of(2019, 6, 30),
                    standardWage: 10.75, urbanWage: 12, nonurbanWage: 10.5),

            new Wages(effectiveDateStart: LocalDate.of(2017, 7, 1),
                    effectiveDateEnd: LocalDate.of(2018, 6, 30),
                    standardWage: 10.25, urbanWage: 11.25, nonurbanWage: 10),

            new Wages(effectiveDateStart: LocalDate.of(2016, 7, 1),
                    effectiveDateEnd: LocalDate.of(2017, 6, 30),
                    standardWage: 9.75, urbanWage: 9.75, nonurbanWage: 9.5),

            new Wages(effectiveDateStart: LocalDate.of(2016, 1, 1),
                    effectiveDateEnd: LocalDate.of(2016, 6, 30),
                    standardWage: 9.25, urbanWage: 9.25, nonurbanWage: 9.25)
    ]

    @JsonIgnore
    String id
    String name
    String city
    String county
    String state
    String stateCode
    String minimumWageClassification

    /**
     * Minimum wage getter. Returns minimum wage given
     * the minimumWageDate and minimumWageClassification.
     * @return
     */
    @JsonProperty("minimumWage")
    BigDecimal getMinimumWage() {
        if (minimumWageClassification) {
            for (Wages wages : minimumWages) {
                if (wages.isInEffectiveDateRange(minimumWageDate)) {
                    switch (minimumWageClassification) {
                        case standardMinWageClass:
                            return wages.standardWage
                        case urbanMinWageClass:
                            return wages.urbanWage
                        case nonUrbanMinWageClass:
                            return wages.nonurbanWage
                    }
                }
            }
        }
        null
    }
}

class Wages {
    // The start date of the effective date range.
    LocalDate effectiveDateStart

    // The end date of the effective date range.
    LocalDate effectiveDateEnd

    BigDecimal standardWage
    BigDecimal urbanWage
    BigDecimal nonurbanWage

    /**
     * Check if a given date within the effective date range of the wages instance.
     * @param testDate
     * @return
     */
    public Boolean isInEffectiveDateRange(LocalDate testDate) {
        testDate.plusDays(1).isAfter(effectiveDateStart) &&
                testDate.minusDays(1).isBefore(effectiveDateEnd)
    }
}
