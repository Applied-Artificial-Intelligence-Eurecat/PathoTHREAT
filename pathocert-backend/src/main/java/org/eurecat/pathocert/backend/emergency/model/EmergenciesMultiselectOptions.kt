package org.eurecat.pathocert.backend.emergency.model

/**
 * This File contains the information that is used to display the options that an Emergency could have
 * in the parameters:
 * - Pathogens
 * - Sympyoms
 * - Infrastructure
 *
 * It declares a class to contain all of them, as well as a function that provides the data necessary to show in
 * the front-end.
 *
 * There are two pairs of names:
 * - The key, which is how it will be stored.
 * - The value, which is how it will be displayed.
 *
 * This will let us change the language without changing what is stored in the database.
 *
 * @author: sergi.simon@eurecat.org
 */
data class EmergenciesMultiselectOptions(
    val symptoms: Map<String, String>,
    val infrastructures: Map<String, String>,
    val contaminants: Map<String, String>
)

fun getMultiselectOptionsDefault(): EmergenciesMultiselectOptions {
    return EmergenciesMultiselectOptions(
        mapOf(
            Pair("water-smells", "Water smells bad"),
            Pair("water-color", "The colour of water is strange"),
            Pair("water-tastes", "The taste of water is strange")
        ),
        mapOf(
            Pair("distribution-broken", "Drinking water distribution network broken"),
            Pair("sewage-failure", "Sewage network failure"),
            Pair("flooding", "Flooding")
        ),
        mapOf(
            Pair("ecoli", "Escherichia Coli"),
            Pair("norovirus", "Norovirus"),
            Pair("rotavirus", "Rotavirus")
        )
    )
}