package com.redhat.kafkademo;

import com.google.gson.*;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrmsBean {
    Logger log=LoggerFactory.getLogger(getClass());

    public String preparePayloadSeldon(String body, Exchange exchg) {
        log.info(body);
        System.out.println("seldon BODY: " + body);
        System.out.println("seldon exchg: " + exchg);

        String strNew = body.replace("\"", "");

        String s1 = strNew.substring(strNew.indexOf(" ")+1);

        String s2 = s1.substring(0,s1.lastIndexOf(","));

        String payload = "{\"data\": {\"ndarray\": [ [" + s2 + "] ]}}";

        return payload;

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

        String status=data.get("status")!=JsonNull.INSTANCE?data.get("status").getAsString():"";
        String score=data.get("score")!=JsonNull.INSTANCE?data.get("score").getAsString():"";
        System.out.println("score: " + score);
        System.out.println("status: " + status);

        exchg.getIn().setHeader("SCORE_STATUS", status);

        log.info(status);
        return status;
    }
}
