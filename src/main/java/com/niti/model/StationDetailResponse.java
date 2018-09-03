package com.niti.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StationDetailResponse {

    private List<StationDetails> listOfStationDetails;
    private String code;
    private String message;
    private Integer noOfStation;

    public List<StationDetails> getListOfStationDetails() {
        return listOfStationDetails;
    }

    public void setListOfStationDetails(List<StationDetails> listOfStationDetails) {
        this.listOfStationDetails = listOfStationDetails;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public Integer getNoOfStation() {
        return noOfStation;
    }

    public void setNoOfStation(Integer noOfStation) {
        this.noOfStation = noOfStation;
    }

}
