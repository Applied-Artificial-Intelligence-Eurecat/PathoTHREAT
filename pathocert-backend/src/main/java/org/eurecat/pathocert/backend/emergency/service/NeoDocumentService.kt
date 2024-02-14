package org.eurecat.pathocert.backend.emergency.service

import org.eurecat.pathocert.backend.BackendApplication
import org.eurecat.pathocert.backend.ConfigProperties
import org.eurecat.pathocert.backend.close_assessments.data.DocumentControl
import org.eurecat.pathocert.backend.close_assessments.data.DocumentImpact
import org.eurecat.pathocert.backend.close_assessments.data.PathogenInfo
import org.eurecat.pathocert.backend.emergency.model.LocationNode
import org.neo4j.driver.AuthTokens
import org.neo4j.driver.GraphDatabase
import org.neo4j.driver.Transaction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.util.*


@Service
@Profile("!(test | assessment-test)")
class NeoDocumentService (
    private val properties: ConfigProperties
        ) : NeoDocumentServiceInt {
    //private val neo4juri: String = properties.getConfigValue("neo4j.expert.uri")
    //private val neo4juser: String = properties.getConfigValue("neo4j.expert.user")
    //private val neo4jpass: String = properties.getConfigValue("neo4j.expert.pass")

    private val neo4juri: String = "bolt://pathocert-neo:7687";
    private val neo4juser: String = "neo4j"
    private val neo4jpass: String = "1234"

    override fun executeLocationTransaction(query: String?): List<LocationNode> {
        NeoDriver(
            GraphDatabase.driver(
                neo4juri,
                AuthTokens.basic(neo4juser, neo4jpass)
            )
        ).use { driver ->
            driver.session().use { session ->
                return session.readTransaction<List<LocationNode>>(computeQuery(query))
            }
        }
    }

    private fun computeQuery(query: String?) = { tx: Transaction ->
        val nodes1: MutableList<LocationNode> = LinkedList()
        val result = tx.run(query)
        val recordList = result.list()
        for (record in recordList) {
            val nodeMap = record.values()[0].asMap()
            val node = LocationNode(
                (nodeMap["city"] as String?), (nodeMap["region"] as String?), (nodeMap["country"] as String?)
            )
            nodes1.add(node)
        }
        nodes1
    }

    fun extract(value: String): Int {
        return try {
            value.toInt()
        } catch (e: NumberFormatException) {
            0
        }
    }

    override fun executeImpactTransaction(query: String?): DocumentImpact? {
        NeoDriver(
            GraphDatabase.driver(
                neo4juri,
                AuthTokens.basic(neo4juser, neo4jpass)
            )
        ).use { driver ->
            driver.session().use { session ->
                return session.readTransaction { tx ->
                    val contaminants = LinkedList<String>()
                    val symptoms = LinkedList<String>()
                    val result = tx.run(query)
                    var numberPeopleExposed = 0
                    var numberPeopleHospitalized = 0
                    var numberDead = 0
                    while (result.hasNext()) {
                        val record: org.neo4j.driver.Record = result.next()!!
                        val label: String = record.get("label1").asString()
                        val label2: String = record.get("label2").asString()
                        val label3: String = record.get("label3").asString()

                        numberPeopleExposed = extract(label3)
                        numberPeopleHospitalized = extract(label2)
                        numberDead = extract(label)
                    }
                    DocumentImpact(
                        numberPeopleExposed = numberPeopleExposed,
                        numberPeopleDead = numberDead,
                        numberPeopleHospitalized = numberPeopleHospitalized,
                        pathogens = null,
                        symptoms = null,
                    )
                }
            }
        }
    }

    override fun executePathogenImpactTransaction(query: String?): PathogenInfo? {
        NeoDriver(
            GraphDatabase.driver(
                neo4juri,
                AuthTokens.basic(neo4juser, neo4jpass)
            )
        ).use { driver ->
            driver.session().use { session ->
                return session.readTransaction { tx ->
                    val contaminants = LinkedList<String>()
                    val symptoms = LinkedList<String>()
                    val result = tx.run(query)
                    while (result.hasNext()) {
                        val record: org.neo4j.driver.Record = result.next()!!
                        val label: String = record.get("label").asString()
                        val label2: String = record.get("label2").asString()

                        if (!contaminants.contains(label)) {
                            contaminants.add(label)
                        }
                        if (!symptoms.contains(label2)) {
                            symptoms.add(label2)
                        }
                    }
                    PathogenInfo(
                        contaminants,
                        symptoms
                    )
                }
            }
        }
    }

    override fun executeControlTransaction(query: String?): DocumentControl? {
        NeoDriver(
            GraphDatabase.driver(
                neo4juri,
                AuthTokens.basic(neo4juser, neo4jpass)
            )
        ).use { driver ->
            driver.session().use { session ->
                return session.readTransaction { tx ->
                    val result = tx.run(query)
                    val monitoring = HashSet<String>()
                    val restoration = HashSet<String>()
                    val prevention = HashSet<String>()
                    println("Debugging executeControlTransaction: $result")
                    while (result.hasNext()) {

                        val record: org.neo4j.driver.Record = result.next()
                        val monitor: String = record.get("monitoring").asString()
                        val restore: String = record.get("restoration").asString()
                        val prevent: String = record.get("prevention").asString()
                        println("Some result found! ${monitor}, ${restore}, $prevent")
                        monitoring.add(monitor)
                        restoration.add(restore)
                        prevention.add(prevent)
                    }
                    println("d: ${monitoring}, ${restoration}, $prevention")
                    DocumentControl(monitoring.toList(), restoration.toList(), prevention.toList())
                }
            }
        }
    }

    override fun executeLabelTransaction(query: String?): List<String> {
        NeoDriver(
            GraphDatabase.driver(
                neo4juri,
                AuthTokens.basic(neo4juser, neo4jpass)
            )
        ).use { driver ->
            driver.session().use { session ->
                return session.readTransaction<List<String>>(computeAsListOfStrings(query))
            }
        }
    }

    private fun computeAsListOfStrings(query: String?) = { tx: Transaction ->
        val nodes: MutableList<String> = LinkedList()
        val result = tx.run(query)
        while (result.hasNext()) {
            val record = result.next()
            val label = record["label"].asString()
            nodes.add(label)
        }
        nodes
    }

}