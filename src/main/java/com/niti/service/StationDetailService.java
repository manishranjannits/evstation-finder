package com.niti.service;

import com.niti.constants.ServiceConstants;
import com.niti.model.StationDetailResponse;
import com.niti.model.StationDetails;
import com.niti.web.ui.Application;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class StationDetailService {

    OrientGraphNoTx graph = null;


    public StationDetailResponse getStationLatLongDetails(){

        List<StationDetails> detailsList=new ArrayList<>();
        graph = Application.getGraphNoTx();
        Iterator<Vertex> vertex = graph.getVerticesOfClass(ServiceConstants.STATION_CLASS).iterator();
        while(vertex.hasNext()){
            StationDetails stationDetails=new StationDetails();
            Vertex  nodeVertex=vertex.next();
            if(nodeVertex!=null){
                stationDetails.setLatitude(nodeVertex.getProperty("lat"));
                stationDetails.setLongitude(nodeVertex.getProperty("lon"));
                detailsList.add(stationDetails);
            }
        }
        return populateStationDetailsResponse(detailsList);
    }

    private StationDetailResponse populateStationDetailsResponse(List<StationDetails> listOfStationDetails) {
        StationDetailResponse stationDetailResponse=new StationDetailResponse();
        if(CollectionUtils.isNotEmpty(listOfStationDetails)){
            stationDetailResponse.setMessage(ServiceConstants.SUCCESS_MESSAGE);
            stationDetailResponse.setCode(ServiceConstants.SUCCESS_CODE);
            stationDetailResponse.setListOfStationDetails(listOfStationDetails);
            stationDetailResponse.setNoOfStation(listOfStationDetails.size());
        }else{
            stationDetailResponse.setMessage(ServiceConstants.ERROR_MESSAGE);
            stationDetailResponse.setCode(ServiceConstants.ERROR_CODE);
            stationDetailResponse.setListOfStationDetails(null);
            stationDetailResponse.setNoOfStation(null);
        }
        return stationDetailResponse;
    }


}
