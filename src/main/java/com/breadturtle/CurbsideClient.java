package com.breadturtle;

import com.breadturtle.model.ChallengeResponse;
import com.breadturtle.model.Session;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

class CurbsideClient {
    private final WebTarget baseTarget;
    private final Session session;

    CurbsideClient() {
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        JacksonJsonProvider jacksonJsonProvider = new JacksonJaxbJsonProvider();
        jacksonJsonProvider.setMapper(objectMapper);

        this.baseTarget = ClientBuilder.newClient()
                .register(jacksonJsonProvider)
                .target("https://challenge.curbside.com/");
        this.session = getSession();
    }

    private Session getSession() {
        return this.baseTarget.path("get-session").request().get(Session.class);
    }

    ChallengeResponse getChallenge(String id) {
        return this.baseTarget.path(id).request()
                .header("Session", session.getId())
                .get(ChallengeResponse.class);
    }

    String getSessionId() {
        return session.getId();
    }
}
