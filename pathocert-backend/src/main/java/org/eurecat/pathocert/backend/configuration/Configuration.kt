package org.eurecat.pathocert.backend.configuration

import org.eurecat.pathocert.backend.configuration.internal.CommonParseFunctions
import org.eurecat.pathocert.backend.configuration.internal.ReaderConfiguration

/**
 * # Configuration Creator.
 *
 *
 * This class contains a method to check and parse all the configuration.
 * It should be added different steps for a builder, so it can parse it flawlessly, as an
 * example, see baseConfig method.
 *
 *
 * Run ConfigurationTest to check if all the parameters can be read.
 *
 *
 * ## Steps for adding your own parameters
 * 1. Add a method with the NonNull parameter. This annotation will check if the
 * builder uses the parameter *at runtime*. This is why you should check your configuration with ConfigurationTest.
 * 2. Add the parameter at a builder. You probably will add a bunch of fields for the same algorithm, so aggregate them
 * in a method (as with baseConfig).
 * a. If it is a required field, use the getRequiredProperty method with a usable parser.
 * b. If it is an optional field. use the getOptionalProperty with .orElse method to get the default. This will make
 * it easier.
 *
 *
 * ## Design concerns
 * Instead of a general parser for integers, longs, strings and booleans it was prefered
 * to make the reader always accept a function for parsing. This makes it easier to
 * get a custom type as a configuration parameter. For example, providing an Enum parser
 * instead of a String makes it easier to check with a test if the configuration is okay,
 * and allows for earlier error messages.
 *
 *
 * Although @NonNull annotations should be possible at a compiled time, it would sacrifice
 * readability, so it was preferred to get a Runnable that checks it.
 */
data class Configuration(
    val springDataRestBasePath: String,
    val serverPort: Int,
    val springJpaDatabase: String,
    val springDatasourcePlatform: String,
    val springDatasourceUrl: String,
    val springDatasourceUsername: String,
    val springDatasourcePassword: String,
    val springJpaShowSql: Boolean,
    val springJpaGenerateDdl: Boolean,
    val springJpaHibernateDdlAuto: String,
    val springJpaPropertiesHibernateJdbcLobNon_contextual_creation: Boolean,
    val springDatasourceDriverClassName: String,
    val springDatasourceInitializationMode: String,
    val loggingLevelRoot: String,
    val springSecurityUserName: String,
    val springSecurityUserPassword: String,
    val loggingLevelOrgSpringframeworkWebFilterCommonsRequestLoggingFilter: String,
    val springdocSwaggerUiCsrfEnabled: Boolean,
    val springJpaDeferDatasourceInitialization: Boolean,
    val neo4jExpertUser: String,
    val neo4jExpertPass: String,
    val neo4jExpertUri: String
)

class Builder {
    var springDataRestBasePath: String? = null
    var serverPort: Int? = null
    var springJpaDatabase: String? = null
    var springDatasourcePlatform: String? = null
    var springDatasourceUrl: String? = null
    var springDatasourceUsername: String? = null
    var springDatasourcePassword: String? = null
    var springJpaShowSql: Boolean? = null
    var springJpaGenerateDdl: Boolean? = null
    var springJpaHibernateDdlAuto: String? = null
    var springJpaPropertiesHibernateJdbcLobNon_contextual_creation: Boolean? = null
    var springDatasourceDriverClassName: String? = null
    var springDatasourceInitializationMode: String? = null
    var loggingLevelRoot: String? = null
    var springSecurityUserName: String? = null
    var springSecurityUserPassword: String? = null
    var loggingLevelOrgSpringframeworkWebFilterCommonsRequestLoggingFilter: String? = null
    var springdocSwaggerUiCsrfEnabled: Boolean? = null
    var springJpaDeferDatasourceInitialization: Boolean? = null
    var neo4jExpertUser: String? = null
    var neo4jExpertPass: String? = null
    var neo4jExpertUri: String? = null

    fun build() = Configuration(
        springDataRestBasePath!!,
        serverPort!!,
        springJpaDatabase!!,
        springDatasourcePlatform!!,
        springDatasourceUrl!!,
        springDatasourceUsername!!,
        springDatasourcePassword!!,
        springJpaShowSql!!,
        springJpaGenerateDdl!!,
        springJpaHibernateDdlAuto!!,
        springJpaPropertiesHibernateJdbcLobNon_contextual_creation!!,
        springDatasourceDriverClassName!!,
        springDatasourceInitializationMode!!,
        loggingLevelRoot!!,
        springSecurityUserName!!,
        springSecurityUserPassword!!,
        loggingLevelOrgSpringframeworkWebFilterCommonsRequestLoggingFilter!!,
        springdocSwaggerUiCsrfEnabled!!,
        springJpaDeferDatasourceInitialization!!,
        neo4jExpertUser!!,
        neo4jExpertPass!!,
        neo4jExpertUri!!
    )

    fun readProperties(conf: ReaderConfiguration): Configuration {
        springDataRestBasePath = (conf.getRequiredProperty("spring.data.rest.basePath") { t: String? ->
            CommonParseFunctions.identity(
                t
            )
        })
        serverPort = (conf.getRequiredProperty("server.port") { value: String? ->
            CommonParseFunctions.parseInt(
                value
            )
        })
        springJpaDatabase = (conf.getRequiredProperty("spring.jpa.database") { t: String? ->
            CommonParseFunctions.identity(
                t
            )
        })
        springDatasourcePlatform = (conf.getRequiredProperty("spring.datasource.platform") { t: String? ->
            CommonParseFunctions.identity(
                t
            )
        })
        springDatasourceUrl = (conf.getRequiredProperty("spring.datasource.url") { t: String? ->
            CommonParseFunctions.identity(
                t
            )
        })
        springDatasourceUsername = (conf.getRequiredProperty("spring.datasource.username") { t: String? ->
            CommonParseFunctions.identity(
                t
            )
        })
        springDatasourcePassword = (conf.getRequiredProperty("spring.datasource.password") { t: String? ->
            CommonParseFunctions.identity(
                t
            )
        })
        springJpaShowSql = (conf.getRequiredProperty("spring.jpa.show-sql") { value: String? ->
            CommonParseFunctions.parseBoolean(
                value
            )
        })
        springJpaGenerateDdl = (conf.getRequiredProperty("spring.jpa.generate-ddl") { value: String? ->
            CommonParseFunctions.parseBoolean(
                value
            )
        })
        springJpaHibernateDdlAuto = (conf.getRequiredProperty("spring.jpa.hibernate.ddl-auto") { t: String? ->
            CommonParseFunctions.identity(
                t
            )
        })
        springJpaPropertiesHibernateJdbcLobNon_contextual_creation =
            (conf.getRequiredProperty("spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation") { value: String? ->
                CommonParseFunctions.parseBoolean(
                    value
                )
            })
        springDatasourceDriverClassName =
            (conf.getRequiredProperty("spring.datasource.driver-class-name") { t: String? ->
                CommonParseFunctions.identity(
                    t
                )
            })
        springDatasourceInitializationMode =
            (conf.getRequiredProperty("spring.datasource.initialization-mode") { t: String? ->
                CommonParseFunctions.identity(
                    t
                )
            })
        loggingLevelRoot = (conf.getRequiredProperty("logging.level.root") { t: String? ->
            CommonParseFunctions.identity(
                t
            )
        })
        springSecurityUserName = (conf.getRequiredProperty("spring.security.user.name") { t: String? ->
            CommonParseFunctions.identity(
                t
            )
        })
        springSecurityUserPassword = (conf.getRequiredProperty("spring.security.user.password") { t: String? ->
            CommonParseFunctions.identity(
                t
            )
        })
        loggingLevelOrgSpringframeworkWebFilterCommonsRequestLoggingFilter = (
                conf.getRequiredProperty("logging.level.org.springframework.web.filter.CommonsRequestLoggingFilter") { t: String? ->
                    CommonParseFunctions.identity(
                        t
                    )
                }
                )
        springdocSwaggerUiCsrfEnabled =
            (conf.getRequiredProperty("springdoc.swagger-ui.csrf.enabled") { value: String? ->
                CommonParseFunctions.parseBoolean(
                    value
                )
            })
        springJpaDeferDatasourceInitialization =
            (conf.getRequiredProperty("spring.jpa.defer-datasource-initialization") { value: String? ->
                CommonParseFunctions.parseBoolean(
                    value
                )
            })
        neo4jExpertUser = (conf.getRequiredProperty("neo4j.expert.user") { t: String? ->
            CommonParseFunctions.identity(
                t
            )
        })
        neo4jExpertPass = (conf.getRequiredProperty("neo4j.expert.pass") { t: String? ->
            CommonParseFunctions.identity(
                t
            )
        })
        neo4jExpertUri = (conf.getRequiredProperty("neo4j.expert.uri") { t: String? ->
            CommonParseFunctions.identity(
                t
            )
        })
        return build()
    }

}

fun parseProperties(configs: String?): Configuration {
    val conf = ReaderConfiguration(configs)
    return Builder().readProperties(conf)
}
