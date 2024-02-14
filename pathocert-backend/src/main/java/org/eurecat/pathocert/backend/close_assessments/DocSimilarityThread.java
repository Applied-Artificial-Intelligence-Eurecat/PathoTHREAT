package org.eurecat.pathocert.backend.close_assessments;

import org.eurecat.pathocert.backend.close_assessments.data.DocumentControl;
import org.eurecat.pathocert.backend.close_assessments.data.DocumentImpact;
import org.eurecat.pathocert.backend.close_assessments.data.DocumentSimilarity;
import org.eurecat.pathocert.backend.emergency.jpa.Emergency;
import org.eurecat.pathocert.backend.emergency.repository.DocumentRepository;
import org.eurecat.pathocert.backend.emergency.service.EmergencyComparator;
import org.eurecat.pathocert.backend.emergency.service.EmergencyComparatorInt;
import org.eurecat.pathocert.backend.emergency.service.NeoDocumentService;
import org.eurecat.pathocert.backend.emergency.service.NeoDocumentServiceInt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

@Service
public class DocSimilarityThread extends Thread {
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private NeoDocumentServiceInt neoDocumentService;
    private String title;
    private Emergency emergency;
    @Autowired
    private EmergencyComparatorInt emergencyComparator;
    public DocSim simil;

    public DocSimilarityThread() {
        title = "";
        emergency = null;
    }

    public DocSimilarityThread(String title,
                               Emergency emergency,
                               DocumentRepository documentRepository,
                               NeoDocumentServiceInt neoDocumentService,
                               EmergencyComparatorInt emergencyComparator) {
        this.title = title;
        this.emergency = emergency;
        this.documentRepository = documentRepository;
        this.neoDocumentService = neoDocumentService;
        this.emergencyComparator = emergencyComparator;
    }

    @Async
    public CompletableFuture<DocSim> runSimil() {
        // NullPointerException aquí??? amb documentRepository
        var docOpt = documentRepository.findByNameEquals(title);
        // Titles were changed as they are shown to the client. Source should always be correct
        if (docOpt.isPresent()) {
            var e = docOpt.get();
            simil = new DocSim();
            simil.similarity = emergencyComparator.compareService(emergency, e);
            simil.document_title = title;
        } else {
            return null;
        }
        return CompletableFuture.completedFuture(simil);
    }

    private DocumentImpact getDocumentImpact(String documentName) {
        var query = "MATCH (n {title: \"" + documentName + "\"}) WITH n " +
                "MATCH (n)-[:HAS_IMPACT]->(n2) WITH n2 " +
                "RETURN n2.people_dead as label1, n2.people_hospitalized as label2, n2.people_ill as label3";
        var docImpact = neoDocumentService.executeImpactTransaction(query);
        var query2 = "MATCH (n {title: \"" + documentName + "\"}) WITH n " +
                "MATCH (n)-[:HAS_CONTAMINANT]->(n2) " +
                "WITH n2 MATCH (n2)-[:HAS_SYMPTOMS]->(n3) " +
                "RETURN n2.name as label, n3.name as label2";
        var docPathogensInfo = neoDocumentService.executePathogenImpactTransaction(query2);
        assert docPathogensInfo != null;
        assert docImpact != null;
        docImpact.setPathogens(docPathogensInfo.pathogens);
        docImpact.setSymptoms(docPathogensInfo.symptoms);
        return docImpact;
    }

    private DocumentControl getDocumentControl(String documentName) {
        var query = "MATCH (n {title: \"" + documentName + "\"})-[:HAS_MONITORING]->(n2) return n2.description as label";
        var monitoring = neoDocumentService.executeLabelTransaction(query);
        query = "MATCH (n {title: \"" + documentName + "\"})-[:HAS_RESTORATION]->(n2) return n2.description as label";
        var restoration = neoDocumentService.executeLabelTransaction(query);
        query = "MATCH (n {title: \"" + documentName + "\"})-[:HAS_PREVENTION]->(n2) return n2.description as label";
        var prevention = neoDocumentService.executeLabelTransaction(query);
        return new DocumentControl(monitoring, restoration, prevention);
    }
}
