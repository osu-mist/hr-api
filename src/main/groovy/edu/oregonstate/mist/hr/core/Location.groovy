package edu.oregonstate.mist.hr.core

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

import java.time.LocalDate

class Location {
    static LocalDate minimumWageDate

    static final String standardMinWageClass = "Standard"
    static final String urbanMinWageClass = "Urban"
    static final String nonUrbanMinWageClass = "Nonurban"

    private static final List<Wages> minimumWages = [
            new Wages(effectiveDate: LocalDate.of(2022, 7, 1),
                    standardWage: 13.5, urbanWage: 14.75, nonurbanWage: 12.5),
            new Wages(effectiveDate: LocalDate.of(2021, 7, 1),
                    standardWage: 12.75, urbanWage: 14, nonurbanWage: 12),
            new Wages(effectiveDate: LocalDate.of(2020, 7, 1),
                    standardWage: 12, urbanWage: 13.25, nonurbanWage: 11.5),
            new Wages(effectiveDate: LocalDate.of(2019, 7, 1),
                    standardWage: 11.25, urbanWage: 12.5, nonurbanWage: 11),
            new Wages(effectiveDate: LocalDate.of(2018, 7, 1),
                    standardWage: 10.75, urbanWage: 12, nonurbanWage: 10.5),
            new Wages(effectiveDate: LocalDate.of(2017, 7, 1),
                    standardWage: 10.25, urbanWage: 11.25, nonurbanWage: 10),
            new Wages(effectiveDate: LocalDate.of(2016, 7, 1),
                    standardWage: 9.75, urbanWage: 9.75, nonurbanWage: 9.5),
            new Wages(effectiveDate: LocalDate.of(2016, 1, 1),
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

    @JsonProperty("minimumWage")
    BigDecimal getMinimumWage() {
        if (minimumWageClassification) {
            for (Wages wages : minimumWages) {
                if (wages.isGreaterThanOrEqualToEffectiveDate(minimumWageDate)) {
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
    LocalDate effectiveDate
    BigDecimal standardWage
    BigDecimal urbanWage
    BigDecimal nonurbanWage

    public Boolean isGreaterThanOrEqualToEffectiveDate(LocalDate testDate) {
        testDate.plusDays(1).isAfter(effectiveDate)
    }
}
