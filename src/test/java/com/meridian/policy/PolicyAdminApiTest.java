package com.meridian.policy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PolicyAdminApiTest {

    @Autowired MockMvc mvc;

    @Test
    void healthIsUp() throws Exception {
        mvc.perform(get("/api/policies/health"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void createPartyProducerThenPolicyEndorseCancel() throws Exception {
        String partyBody = """
                {"legalName":"Sunshine Hospitality Group LLC","dbaName":"Sunshine Hospitality",
                 "entityTypeCode":"LLC","naicsCode":"722511","mailingCity":"Tampa","mailingStateCode":"FL",
                 "mailingPostalCode":"33602","primaryContactName":"Ana Rivera","primaryContactEmail":"ana@sunshine.example"}""";
        String partyResp = mvc.perform(post("/api/parties").contentType(MediaType.APPLICATION_JSON).content(partyBody))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long partyId = extractId(partyResp, "policyholderId");

        String producerBody = """
                {"agencyCode":"TPA-%d","agencyName":"Bayview Independent Agency","producerTypeCode":"RETAIL_AGENT",
                 "licenseStateCode":"FL","contractedCommissionPct":10.00}""".formatted(System.nanoTime() % 100000);
        String producerResp = mvc.perform(post("/api/producers").contentType(MediaType.APPLICATION_JSON).content(producerBody))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long producerId = extractId(producerResp, "producerId");

        String policyBody = """
                {"policyholderId":%d,"producerId":%d,"productCode":"CPROP","underwritingCompanyCode":"MMIC",
                 "policyTermEffectiveDate":"2026-01-01","policyTermExpirationDate":"2027-01-01",
                 "writtenPremiumAmount":25000.00,
                 "coverageLines":[
                   {"lineOfBusinessCode":"PROP","coveragePartCode":"BUILDING","limitPerOccurrenceAmount":1000000,
                    "coveragePremiumAmount":15000.00,"perilSetCode":"SPECIAL"},
                   {"lineOfBusinessCode":"GL","coveragePartCode":"OCCURRENCE","limitPerOccurrenceAmount":1000000,
                    "coveragePremiumAmount":10000.00,"perilSetCode":"SPECIAL"}],
                 "riskLocations":[
                   {"locationSequenceNumber":1,"addressLine1":"100 Bay St","city":"Tampa","stateCode":"FL","postalCode":"33602",
                    "constructionClassCode":"MASONRY_NC","buildingValueAmount":800000,"contentsValueAmount":200000}]}"""
                .formatted(partyId, producerId);

        String policyResp = mvc.perform(post("/api/policies").contentType(MediaType.APPLICATION_JSON).content(policyBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.policyNumber").exists())
                .andExpect(jsonPath("$.policyStatusCode").value("BOUND"))
                .andExpect(jsonPath("$.coverageLines.length()").value(2))
                .andReturn().getResponse().getContentAsString();
        String policyNumber = extractString(policyResp, "policyNumber");

        mvc.perform(get("/api/policies/" + policyNumber))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.policyNumber").value(policyNumber));

        String endorseBody = """
                {"endorsementTypeCode":"ADD_LOCATION","endorsementEffectiveDate":"2026-06-01",
                 "endorsementProcessedDate":"2026-06-10","premiumDeltaAmount":3500.00,"initiatingSourceCode":"INSURED"}""";
        mvc.perform(post("/api/policies/" + policyNumber + "/endorsements").contentType(MediaType.APPLICATION_JSON).content(endorseBody))
           .andExpect(status().isCreated());

        mvc.perform(get("/api/policies/" + policyNumber))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.policyStatusCode").value("ENDORSED"))
           .andExpect(jsonPath("$.writtenPremiumAmount").value(28500.00));

        mvc.perform(post("/api/policies/" + policyNumber + "/cancel")
                .contentType(MediaType.APPLICATION_JSON).content("{\"reasonCode\":\"INSURED_REQUEST\",\"cancellationDate\":\"2026-09-01\"}"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.policyStatusCode").value("CANCELLED"));
    }

    private static long extractId(String json, String key) {
        String token = "\"" + key + "\":";
        int i = json.indexOf(token);
        int start = i + token.length();
        int end = json.indexOf(",", start);
        if (end == -1) end = json.indexOf("}", start);
        return Long.parseLong(json.substring(start, end).trim());
    }
    private static String extractString(String json, String key) {
        String token = "\"" + key + "\":\"";
        int i = json.indexOf(token);
        int start = i + token.length();
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }
}
