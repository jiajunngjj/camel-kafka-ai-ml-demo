package com.redhat.kafkademo;

import com.google.gson.*;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.camel.TypeConversionException;

public class BrmsBean {
    Logger log=LoggerFactory.getLogger(getClass());

    public String preparePayloadSeldon(String body, Exchange exchg) {
        log.info(body);
        System.out.println("seldon BODY: " + body);
        System.out.println("seldon exchg: " + exchg);

        // Strip square brackets
        int index = body.indexOf("[");
        if (index != -1) {
            body = body.substring(index+1);
        }
        index = body.lastIndexOf("]");
        if (index != -1) {
            body = body.substring(0, index);
        }

        String[] elements = body.split(",");
        // Strip quotes
        for (int i=0; i<elements.length; i++) {
            String elem = elements[i];
            index = elem.indexOf("\"");
            if (index == -1) continue;
            elem = elem.substring(index+1);
            index = elem.lastIndexOf("\"");
            if (index == -1) continue;
            elem = elem.substring(0, index);
            elements[i] = elem;
        }

        // customer ID is in elements[0]
        if (elements.length > 0) {
            exchg.getMessage().setHeader("CustomerID", elements[0]);
        }

        StringBuffer sb = new StringBuffer();
        sb.append("{\"data\": {\"ndarray\": [ [");

        for (int i=1; i<elements.length-1; i++) {
            if (i > 1) sb.append(", ");
            sb.append(elements[i]);
        }

        sb.append("] ]}}");

        return sb.toString();
    }

    public String preparePayloadDm(String body, Exchange exchg) {

        log.info(body);

//        System.out.println("ML BODY: " + body);
        String s1 = body.substring(body.indexOf("ndarray"));
//        System.out.println("s1: " + s1);
        float reading = Float.parseFloat(s1.substring(s1.indexOf("0"), s1.lastIndexOf("]")));
        System.out.println("reading: " + reading);

        BrmsInsertElement insert = new BrmsInsertElement();
        InsertElementWrapper insertElementWrapper = new InsertElementWrapper();
        insertElementWrapper.setInsert(insert);
        FireRulesObject fireRulesObject = new FireRulesObject();
        Result result = new Result();
        result.setScore(reading);
        System.out.println("reading OBject: " + result.getScore());
        insert.getObject().setPsiDate(result);
        BrmsPayload brms = new BrmsPayload(insertElementWrapper,fireRulesObject);
        String payload = new Gson().toJson(brms);
        System.out.println("payload: " + payload);

        return payload;

    }

    public String extractValue(String body, Exchange exchg) {

        log.info(body);

        JsonParser parser=new JsonParser();

        JsonObject data=parser.parse(body)
                .getAsJsonObject().get("result")
                .getAsJsonObject().get("execution-results")
                .getAsJsonObject().getAsJsonArray("results").get(0)
                .getAsJsonObject().get("value").getAsJsonObject()
                .getAsJsonObject().get("com.myspace.drl_fraud.Result")
                .getAsJsonObject();

        String customerID = null;
        try {
            customerID = exchg.getMessage().getHeader("CustomerID", String.class);
        } catch (TypeConversionException e) {}

        String status=data.get("status")!=JsonNull.INSTANCE?data.get("status").getAsString():"";
        String score=data.get("score")!=JsonNull.INSTANCE?data.get("score").getAsString():"";
        System.out.println("customer ID: " + customerID);
        System.out.println("score: " + score);
        System.out.println("status: " + status);

        exchg.getIn().setHeader("SCORE_STATUS", status);

        log.info(status);
        return "customer ID: " + customerID + ", status: " + status + ", score: " + score;
    }
}
