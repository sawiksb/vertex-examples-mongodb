package io.vertx.example.web.rest;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.examples.utils.Runner;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.UpdateOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.HashMap;
import java.util.Map;

//import io.vertx.core.json.DecodeException;
//import io.vertx.ext.web.impl.RoutingContextImplBase;

//This is a simple CRUD operation vertx microservice.
public class SimpleREST extends AbstractVerticle {

    MongoClient mongoClient;
    //final MongoClient mongoClient = MongoClient.createShared(vertx, new JsonObject().put("db_name", "emp_db"));

    public static void main(String[] args) {
        Runner.runExample(SimpleREST.class);
    }

    private Map<String, JsonObject> products = new HashMap<>();

    @Override
    public void start() {
        //connecting to Mongo to collection: emp_db
        JsonObject config = Vertx.currentContext().config();

        String uri = config.getString("mongo_uri");
        if (uri == null) {
            uri = "mongodb://localhost:27017";
        }
        String db = "emp_db";  //mongo_db
        if (db == null) {
            db = "test";
        }

        JsonObject mongoconfig = new JsonObject()
                .put("connection_string", uri)
                .put("db_name", db);

         mongoClient = MongoClient.createShared(vertx, mongoconfig);

        //-----------------------------------------------------------------------------------------------------------
        //setUpInitialData();  /// uncomment for hard coding

        //Just for testing  ------<uncomment below for testing >------------------------
        //testingDatatoMongo(mongoClient);

        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());
        router.get("/products").handler(this::handleListProducts); //Fetch all products (to read)
        router.get("/products/:productID").handler(this::handleGetProduct); //Fetch perticular product (to read)

        router.put("/products/:productID").handler(this::handleUpdateProduct); //update perticular product (to update)

        router.post("/products/create").handler((this::handleCreateProduct)); //Create new product

        router.delete("/products/:productID").handler((this::handleDeleteProduct)); //Delete a product(Remove by ID)


        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
    }


    private void handleDeleteProduct(RoutingContext routingContext) {
        String productID = routingContext.request().getParam("productID");
        HttpServerResponse response = routingContext.response();
        JsonObject emp = new JsonObject().put("id", productID);
        mongoClient.removeDocument("employee", emp, res -> {
//            if (productID == null) {
//                routingContext.response().setStatusCode(400).end();
//            } else {
//                Integer idAsInteger = Integer.valueOf(productID);
//                products.remove(idAsInteger);
//            }
//            routingContext.response()
//                    .setStatusCode(204)
//                    .end();
            if (res.succeeded()) {
                System.out.println("Delete Success");
            }else{
                System.out.println("Delete Failed");
            }
        });
        response.end();
    }


    //    private void handleCreateProductOLD(RoutingContext routingContext) {
//        ;
//        final Product product = Json.decodeValue(routingContext.getBodyAsString(), Product.class);
//        products.put(product.getId(), routingContext.getBodyAsJson());
//        routingContext.response()
//                .setStatusCode(201)
//                .putHeader("content-type", "application/json; charset=utf-8")
//                .end(Json.encodePrettily(product));
//        //serverResponse.end(product.size() + "Added Successfully");
//        System.out.println("Added Successfuly");
//    }
    private void handleCreateProduct(RoutingContext routingContext) {
        //insert
        //String productID = routingContext.request().getParam("productID");
        HttpServerResponse response = routingContext.response();
        JsonObject document = routingContext.getBodyAsJson();
        mongoClient.insert("employee", document, res -> {
            if (res.succeeded()) {
                System.out.println("Inserted");
            } else {
                res.cause().printStackTrace();
            }

        });
        response.end();
    }

    //To read from mongo
//    private void handleGetProductOLD(RoutingContext routingContext) {
//        String productID = routingContext.request().getParam("productID");
//        HttpServerResponse response = routingContext.response();
//        if (productID == null) {
//            sendError(400, response);
//        } else {
//            JsonObject product = products.get(productID);
//            if (product == null) {
//                sendError(404, response);
//            } else {
//                response.putHeader("content-type", "application/json").end(product.encodePrettily());
//            }
//        }
//    }
    private void handleGetProduct(RoutingContext routingContext) {
        String productID = routingContext.request().getParam("productID");
        JsonArray arr = new JsonArray();
        JsonObject emp = new JsonObject().put("id",productID);
        mongoClient.find("employee", emp, res -> {
            if (res.succeeded()) {
                for (JsonObject json : res.result()) {
                    arr.add(json);
                }
                routingContext.response().putHeader("content-type", "application/json").end(arr.encodePrettily());
            } else {
                res.cause().printStackTrace();
            }
        });
    }

    //    private void handleUpdateProductOLD(RoutingContext routingContext) {
//        String productID = routingContext.request().getParam("productID");
//        HttpServerResponse response = routingContext.response();
//        if (productID == null) {
//            sendError(400, response);
//        } else {
//            JsonObject product = routingContext.getBodyAsJson();
//            if (product == null) {
//                sendError(400, response);
//            } else {
//                products.put(productID, product);
//                response.end();
//            }
//        }
//    }
    private void handleUpdateProduct(RoutingContext routingContext) {
        String productID = routingContext.request().getParam("productID");
        HttpServerResponse response = routingContext.response();
        if (productID == null) {
            sendError(400, response);
        } else {
            JsonObject update = new JsonObject().put("$set",routingContext.getBodyAsJson());
            if (update == null) {
                sendError(400, response);
            } else {
                JsonObject query = new JsonObject().put("id",productID);
                JsonObject update1 = new JsonObject().put("$set", routingContext.getBodyAsJson());
                UpdateOptions options = new UpdateOptions().setMulti(true);
                mongoClient.updateCollectionWithOptions("employee",query,update1,options,res -> {
                    if (res.succeeded()) {
                        System.out.println("Updated" + productID);
                    } else {
                        res.cause().printStackTrace();
                    }
                });
                response.end();
            }
        }
    }


    //    private void handleListProductsOLD(RoutingContext routingContext) {
//        JsonArray arr = new JsonArray();
//        products.forEach((k, v) -> arr.add(v));
//        routingContext.response().putHeader("content-type", "application/json").end(arr.encodePrettily());
//    }
    private void handleListProducts(RoutingContext routingContext) {
        JsonArray arr = new JsonArray();
        JsonObject emp = new JsonObject();
        mongoClient.find("employee", emp, res -> {
            if (res.succeeded()) {
                for (JsonObject json : res.result()) {
                    arr.add(json);
                }
                routingContext.response().putHeader("content-type", "application/json").end(arr.encodePrettily());
            } else {
                res.cause().printStackTrace();
            }
        });
    }

    private void sendError(int statusCode, HttpServerResponse response) {
        response.setStatusCode(statusCode).end();
    }

//    private void setUpInitialData() {
//        JsonObject emp1;
//        addProduct(JsonObject emp1 = new JsonObject().put("id", "111").put("name", "Satwik").put("team", "Visenti").put("skill", "Cyber");
//        JsonObject emp2;
//        addProduct(JsonObject emp2 = new JsonObject().put("id", "222").put("name", "Sahithya").put("team", "BMS").put("skill", "CSE");
//        JsonObject emp3;
//        addProduct(JsonObject emp3 = new JsonObject().put("id", "333").put("name", "Sunanda").put("team", "Sensor").put("skill", "AI");
//
//        mongoClient.save("employee", emp1, id -> {
//            if (id.succeeded()) {
//                System.out.println("Inserted doc " + id.result());
//                mongoClient.find("employee", new JsonObject().put("_id", "007"), res -> {
//                    if (res.succeeded()) {
//                        System.out.println("Name is " + res.result().get(0).getString("name"));
//                        System.out.println("Team is " + res.result().get(0).getString("team"));
//                    } else {
//                        System.out.println("else loop");
//                    }
//                });
//            } else {
//                System.out.println("else loop for save");
//            }
//
//        });
//
//        mongoClient.save("employee", emp2, id -> {
//            if (id.succeeded()) {
//                System.out.println("Inserted doc " + id.result());
//                mongoClient.find("employee", new JsonObject().put("_id", "007"), res -> {
//                    if (res.succeeded()) {
//                        System.out.println("Name is " + res.result().get(0).getString("name"));
//                        System.out.println("Team is " + res.result().get(0).getString("team"));
//                    } else {
//                        System.out.println("else loop");
//                    }
//                });
//            } else {
//                System.out.println("else loop for save");
//            }
//
//        });
//
//        mongoClient.save("employee", emp3, id -> {
//            if (id.succeeded()) {
//                System.out.println("Inserted doc " + id.result());
//                mongoClient.find("employee", new JsonObject().put("_id", "007"), res -> {
//                    if (res.succeeded()) {
//                        System.out.println("Name is " + res.result().get(0).getString("name"));
//                        System.out.println("Team is " + res.result().get(0).getString("team"));
//                    } else {
//                        System.out.println("else loop");
//                    }
//                });
//            } else {
//                System.out.println("else loop for save");
//            }
//
//        });
//    }

//    private void addProduct(JsonObject product) {
//        products.put(product.getString("id"), product);
//
//    }

    //Uncomment this to test the data to MongoDB
//    private void testingDatatoMongo (MongoClient mongoClient){
//        JsonObject document = new JsonObject()
//                .put("id", "000").put("name", "Kiwtas").put("team", "Visenti").put("skill", "Cyber");
//        mongoClient.save("employee", document, res -> {
//            if (res.succeeded()) {
//                String productID = res.result();
//                System.out.println("Saved employee details with id:" + productID);
//            } else {
//                res.cause().printStackTrace();
//            }
//        });
//    }

}