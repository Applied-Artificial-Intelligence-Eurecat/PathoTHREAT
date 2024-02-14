package org.eurecat.pathocert.backend.close_assessments.data

data class DocumentControl(
    var monitoring: List<String>,
    var restoration: List<String>,
    var prevention: List<String>
)