package com.niti.controller;

import com.niti.model.StationDetailResponse;
import com.niti.model.StationDetails;
import com.niti.service.StationDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/v1")
public class StationController {

    @Autowired
    private StationDetailService stationDetailService;

    @GetMapping("/getStationDetails")
    @ResponseBody
    public StationDetailResponse getStationLatLonDetails(){
        return stationDetailService.getStationLatLongDetails();
    }


}
