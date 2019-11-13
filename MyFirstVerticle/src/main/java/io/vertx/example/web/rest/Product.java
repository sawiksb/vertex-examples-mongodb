package io.vertx.example.web.rest;

import io.vertx.core.json.JsonObject;

public class Product extends JsonObject {

    private String id;
    private String name;
    private String team;
    private String skill;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public Product(String id, String name, String team, String skill) {
        super();
        this.id = id;
        this.name = name;
        this.team = team;
        this.skill = skill;
    }

    public Product() {
        super();
        // TODO Auto-generated constructor stub
    }
}
