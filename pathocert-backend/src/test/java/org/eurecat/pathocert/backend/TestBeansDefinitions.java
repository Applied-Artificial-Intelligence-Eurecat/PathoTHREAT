package org.eurecat.pathocert.backend;

import org.eurecat.pathocert.backend.close_assessments.data.DocumentControl;
import org.eurecat.pathocert.backend.close_assessments.data.DocumentImpact;
import org.eurecat.pathocert.backend.close_assessments.data.DocumentImpactBuilder;
import org.eurecat.pathocert.backend.close_assessments.data.PathogenInfo;
import org.eurecat.pathocert.backend.emergency.jpa.Document;
import org.eurecat.pathocert.backend.emergency.jpa.Emergency;
import org.eurecat.pathocert.backend.emergency.model.ExNode;
import org.eurecat.pathocert.backend.emergency.model.GraphItem;
import org.eurecat.pathocert.backend.emergency.model.LocationNode;
import org.eurecat.pathocert.backend.emergency.repository.DocumentRepository;
import org.eurecat.pathocert.backend.emergency.repository.EmergencyRepository;
import org.eurecat.pathocert.backend.emergency.service.EmergencyComparatorInt;
import org.eurecat.pathocert.backend.emergency.service.ExpertServiceInt;
import org.eurecat.pathocert.backend.emergency.service.NeoDocumentServiceInt;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.util.*;

@Configuration
public class TestBeansDefinitions {

    @Bean
    @Profile({"test", "assessment-test"})
    public NeoDocumentServiceInt neoDocumentServiceBean(){
        return new NeoDocumentServiceInt() {
            @Override
            public List<LocationNode> executeLocationTransaction(String query) {
                return null;
            }

            @Override
            public DocumentImpact executeImpactTransaction(String query) {
                DocumentImpactBuilder doc = DocumentImpact.Companion.builder();
                doc.setPropNumberPeopleHospitalized(4);
                doc.setPropNumberPeopleExposed(100);
                doc.setPropNumberPeopleDead(1);
                return doc.build();
            }

            @Override
            public PathogenInfo executePathogenImpactTransaction(String query) {
                return new PathogenInfo(List.of("ecoli"), List.of("vomiting", "cramps"));
            }

            @Override
            public DocumentControl executeControlTransaction(String query) {
                return null;
            }

            @Override
            public List<String> executeLabelTransaction(String query) {
                var bigQuery = "MATCH (n) WHERE n:Event OR n:CascadingWNEvent OR n:ProducedEvent OR n:Contaminant " +
                        "OR n:EffectWaterTaste OR n:EffectWaterOdor OR n:EffectWater OR n:ContaminantFamily " +
                        "OR n:ContaminantTreatment OR n:EffectHealth OR n:Symptom OR n:ContaminantType " +
                        "return n.name as label";
                if (query.equals(bigQuery)){
                    return List.of("ecoli", "norovirus", "rotavirus", "vomiting", "diarrhea", "cramps");
                }
                if (query.contains("Contaminant")){
                    return List.of("ecoli", "norovirus", "rotavirus");
                } else if (query.contains("Symptom")){
                    return List.of("vomiting", "diarrhea", "cramps");
                } else if (query.contains("Detection")){
                    return List.of("Large number of hospital patients");
                } else if (query.contains("Document")){
                    return List.of("Outbreak in Granada", "Contamination in Seoul");
                } else if (query.contains("HAS_MONITORING")){
                    return List.of("Monitoring of patients");
                } else if (query.contains("HAS_PREVENTION")){
                    return List.of("Sanitize pipes");
                } else if (query.contains("HAS_RESTORATION")){
                    return List.of("Deliver medicine");
                } else if (query.contains("Event")){
                    return List.of("earthquake", "terrorist-attack");
                }
                return new LinkedList<>();
            }
        };
    }

    @Bean
    @Profile({"test", "assessment-test"})
    public EmergencyComparatorInt emergencyComparatorBean(){
        return new EmergencyComparatorInt() {
            @Override
            public BigDecimal compareService(Emergency emergency, Document e) {
                return BigDecimal.valueOf(new Random().nextFloat()*100);
            }

            @Override
            public JSONObject mergeInNeo4j(String bodyString, HttpClient client) {
                var ob = new JSONObject();
                var impact = new JSONObject();
                impact.put("people_ill", "4-10");
                impact.put("people_hospitalized", "20-40");
                impact.put("people_dead", "1-3");
                ob.put("ContaminantD", "llista,separada");
                ob.put("Detection", "llista,separada");
                ob.put("Cause", "llista,separada");
                ob.put("Mitigation", "llista,separada");
                ob.put("Monitoring", "llista,separada");
                ob.put("Prevention", "llista,separada");
                ob.put("Restoration", "llista,separada");
                ob.put("Source", "llista,separada");
                ob.put("SymptomD", "llista,separada");
                ob.put("Impact", impact);
                return ob;
            }
        };
    }

    @Bean
    @Profile({"test", "assessment-test"})
    public DocumentRepository documentRepositoryBean() {
        return new DocumentRepository() {
            @Override
            public Optional<Document> findByNameEquals(String name) {
                var doc = new Document();
                doc.setName(name);
                return Optional.of(doc);
            }

            @Override
            public Optional<Document> findBySourceEquals(String source) {
                return Optional.empty();
            }

            @Override
            public <S extends Document> S save(S entity) {
                return null;
            }

            @Override
            public <S extends Document> Iterable<S> saveAll(Iterable<S> entities) {
                return null;
            }

            @Override
            public Optional<Document> findById(Long aLong) {
                return Optional.empty();
            }

            @Override
            public boolean existsById(Long aLong) {
                return false;
            }

            @Override
            public Iterable<Document> findAll() {
                return null;
            }

            @Override
            public Iterable<Document> findAllById(Iterable<Long> longs) {
                return null;
            }

            @Override
            public long count() {
                return 2;
            }

            @Override
            public void deleteById(Long aLong) {

            }

            @Override
            public void delete(Document entity) {

            }

            @Override
            public void deleteAllById(Iterable<? extends Long> longs) {

            }

            @Override
            public void deleteAll(Iterable<? extends Document> entities) {

            }

            @Override
            public void deleteAll() {

            }
        };
    }

    @Bean
    @Profile({"assessment-test"})
    public EmergencyRepository emergencyRepositoryBean() {
        return new EmergencyRepository() {
            @Override
            public List<Emergency> findByReportingOrganization_IdAndArchived(Long id, boolean archived) {
                return null;
            }

            @Override
            public List<Emergency> findByArchived(Boolean archived) {
                return null;
            }

            @NotNull
            @Override
            public <S extends Emergency> S save(@NotNull S var1) {
                return null;
            }

            @NotNull
            @Override
            public <S extends Emergency> Iterable<S> saveAll(@NotNull Iterable<S> var1) {
                return null;
            }

            @NotNull
            @Override
            public Optional<Emergency> findById(@NotNull Long var1) {
                return Optional.of(new Emergency());
            }

            @Override
            public boolean existsById(@NotNull Long var1) {
                return false;
            }

            @NotNull
            @Override
            public Iterable<Emergency> findAll() {
                return null;
            }

            @NotNull
            @Override
            public Iterable<Emergency> findAllById(@NotNull Iterable<Long> var1) {
                return null;
            }

            @Override
            public long count() {
                return 0;
            }

            @Override
            public void deleteById(@NotNull Long var1) {

            }

            @Override
            public void delete(@NotNull Emergency var1) {

            }

            @Override
            public void deleteAll(@NotNull Iterable<? extends Emergency> var1) {

            }

            @Override
            public void deleteAll() {

            }

            @Override
            public void deleteAllById(Iterable<? extends Long> longs) {

            }
        };
    }

    @Bean
    @Profile("test")
    public ExpertServiceInt expertServiceIntBean(){
        return new ExpertServiceInt() {
            @Override
            public List<ExNode> getTerm1andTerm2Search(String term1, String term2) {
                return List.of(new ExNode("ecoli"), new ExNode("norovirus"));
            }

            @Override
            public List<GraphItem> getRelationsOfType(String rel_type) {
                return null;
            }

            @Override
            public String getQuery(String term1, String term2) {
                return "";
            }

            @Override
            public List<GraphItem> findPath(String term1, String term2) {
                return null;
            }
        };
    }
}
