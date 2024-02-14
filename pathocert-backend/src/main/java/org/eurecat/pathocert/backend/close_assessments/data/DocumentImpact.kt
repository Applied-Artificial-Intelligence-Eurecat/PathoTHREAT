package org.eurecat.pathocert.backend.close_assessments.data

data class DocumentImpact(
    val numberPeopleExposed: Int,
    val numberPeopleHospitalized: Int,
    val numberPeopleDead: Int,
    var pathogens: List<String>?,
    var symptoms: List<String>?
) {
    companion object {
        fun builder() = DocumentImpactBuilder()
    }
}

class DocumentImpactBuilder {
    var propNumberPeopleExposed: Int? = null
    var propNumberPeopleHospitalized: Int? = null
    var propNumberPeopleDead: Int? = null
    var propPathogens: List<String>? = null
    var propSymptoms: List<String>? = null

    fun numberPeopleExposed(value: Int): DocumentImpactBuilder {
        propNumberPeopleExposed = value
        return this
    }

    fun numberPeopleHospitalized(value: Int): DocumentImpactBuilder {
        propNumberPeopleHospitalized = value
        return this
    }

    fun numberPeopleDead(value: Int): DocumentImpactBuilder {
        propNumberPeopleDead = value
        return this
    }

    fun pathogens(value: List<String>): DocumentImpactBuilder {
        propPathogens = value
        return this
    }

    fun symptoms(value: List<String>): DocumentImpactBuilder {
        propSymptoms = value
        return this
    }

    fun build() = DocumentImpact(
        propNumberPeopleExposed!!,
        propNumberPeopleHospitalized!!,
        propNumberPeopleDead!!,
        null,
        null
    )
}
