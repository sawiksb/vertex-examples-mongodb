package io.vertx.examples.mongo;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.examples.utils.Runner;
import io.vertx.ext.mongo.MongoClient;

public class MongoClientVerticle extends AbstractVerticle {

    public static void main(String[] args) {
        Runner.runExample(MongoClientVerticle.class);
    }

    @Override
    public void start() throws Exception {

        JsonObject config = Vertx.currentContext().config();

        String uri = config.getString("mongo_uri");
        if (uri == null) {
            uri = "mongodb://localhost:27017";
        }
        String db = "emp_db";  //mongo_db
        /*if (db == null) {
            db = "test";
        }*/

        JsonObject mongoconfig = new JsonObject()
                .put("connection_string", uri)
                .put("db_name", db);

        MongoClient mongoClient = MongoClient.createShared(vertx, mongoconfig);

        JsonObject emp1 = new JsonObject()
                .put("_id", "007")
                .put("name", "Satwik")
                .put("team", "Visenti");

        JsonObject emp2 = new JsonObject()
                .put("_id", "077")
                .put("name", "Sahithya")
                .put("team", "BMS");

        System.out.println("");
        mongoClient.save("employee", emp1, id -> {
            if (id.succeeded()) {
                System.out.println("Inserted doc " + id.result());
                mongoClient.find("employee", new JsonObject().put("_id", "007"), res -> {
                    if (res.succeeded()) {
                        System.out.println("Name is " + res.result().get(0).getString("name"));
                        System.out.println("Team is " + res.result().get(0).getString("team"));
                    } else {
                        System.out.println("else loop");
                    }
                });
            } else {
                System.out.println("else loop for save");
            }

        });

        //------------To delete -----
                /*mongoClient.remove("employee", new JsonObject().put("Id", "007"), rs -> {
                    if (rs.succeeded()) {
                        System.out.println("employee" + id.result() + "is removed ");
                    }
                });*/

        mongoClient.save("employee", emp2, id2 -> {
            if (id2.succeeded()) {
                System.out.println("Inserted id: " + id2.result());
                mongoClient.find("employee", new JsonObject().put("_id", "077"), res2 -> {
                    if (res2.succeeded()) {

                        System.out.println("Name is " + res2.result().get(0).getString("name"));
                        System.out.println("Team is " + res2.result().get(0).getString("team"));
                    } else {
                        System.out.println("else loop fro second find");
                    }

                });
            } else {
                System.out.println("second save else loop");
            }
        });
    }
}
