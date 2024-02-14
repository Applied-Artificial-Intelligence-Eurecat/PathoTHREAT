package org.eurecat.pathocert.backend.emergency.service;

import org.eurecat.pathocert.backend.emergency.jpa.Document;
import org.eurecat.pathocert.backend.emergency.jpa.Emergency;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.http.HttpClient;

public interface EmergencyComparatorInt {
    BigDecimal compareService(Emergency emergency, Document e);

    JSONObject mergeInNeo4j(String bodyString, HttpClient client) throws ParseException, IOException, InterruptedException;
}
