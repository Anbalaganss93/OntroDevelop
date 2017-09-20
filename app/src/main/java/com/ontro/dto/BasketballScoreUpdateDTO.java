package com.ontro.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by umm on 25-Jul-17.
 */

public class BasketballScoreUpdateDTO {

    @SerializedName("player_id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("point_one")
    private String point_one;
    @SerializedName("point_two")
    private String point_two;
    @SerializedName("point_three")
    private String point_three;
    private String is_played;


    public BasketballScoreUpdateDTO(String id, String name, String point_one, String point_two, String point_three, String is_played) {
        this.id = id;
        this.name = name;
        this.point_one = point_one;
        this.point_two = point_two;
        this.point_three = point_three;
        this.is_played = is_played;
    }

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

    public String getPoint_one() {
        return point_one;
    }

    public void setPoint_one(String point_one) {
        this.point_one = point_one;
    }

    public String getPoint_two() {
        return point_two;
    }

    public void setPoint_two(String point_two) {
        this.point_two = point_two;
    }

    public String getPoint_three() {
        return point_three;
    }

    public void setPoint_three(String point_three) {
        this.point_three = point_three;
    }

    public String getIs_played() {
        return is_played;
    }

    public void setIs_played(String is_played) {
        this.is_played = is_played;
    }
}
