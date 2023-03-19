package com.enigmaschool.enigmaschool3springjpa.Model.Dtos;

public class SearchDto {

    private String searchFirstName;

    private String searchLastName;

    public String getSearchFirstName() {
        return searchFirstName;
    }

    public void setSearchFirstName(String searchFirstName) {
        this.searchFirstName = searchFirstName;
    }

    public String getSearchLastName() {
        return searchLastName;
    }

    public void setSearchLastName(String searchLastName) {
        this.searchLastName = searchLastName;
    }
}
