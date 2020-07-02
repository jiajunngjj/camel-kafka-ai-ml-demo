package com.redhat.kafkademo;

import com.redhat.kafkademo.Result;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrmsProcessor implements Processor {

    Logger log=LoggerFactory.getLogger(getClass());
    @Override
    public void process(Exchange exchg) throws Exception {
        String output=new String((byte[])exchg.getIn().getBody());
        log.info(output);
        JsonParser parser=new JsonParser();
        JsonObject value=parser.parse(output).getAsJsonObject();
        log.info(value.get("psi").getAsString());
        int reading=Integer.valueOf(value.get("psi").getAsString());
        Gson gson=new GsonBuilder().setPrettyPrinting().create();
        BrmsInsertElement insert=new BrmsInsertElement();
        InsertElementWrapper insertElementWrapper=new InsertElementWrapper();
        insertElementWrapper.setInsert(insert);
        FireRulesObject fireRulesObject=new FireRulesObject();
        Result psi=new Result();
        psi.setScore(reading);
        insert.getObject().setPsiDate(psi);
        BrmsPayload brms=new BrmsPayload(insertElementWrapper,fireRulesObject);
        String payload=new Gson().toJson(brms);
        log.info(payload);
        exchg.getIn().setBody(payload);

    }

}
