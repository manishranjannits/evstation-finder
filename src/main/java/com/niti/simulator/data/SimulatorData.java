package com.niti.simulator.data;

import java.text.DecimalFormat;

import com.orientechnologies.orient.core.metadata.schema.OType;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientEdgeType;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;

public class SimulatorData {
	
	private static OrientGraphNoTx graph = null;
	private static OrientVertexType locationType;
	private static OrientVertexType chargerType;
	private static OrientEdgeType roadType;
	private static DecimalFormat df2 = new DecimalFormat(".##");
	
	public static void fillSimulatorData(OrientGraphNoTx graphNoTx){
		graph = graphNoTx;
		boolean isErrorFaced = false;
		try {
		createLocationType();
		}catch(Exception e) {
			System.out.println("Refreshing database is taking time. Please wait...");
			try {
			Thread.sleep(3000);
			isErrorFaced = true;
			}catch(Exception ex) {
				//do nothing
			}
		}
		
		if(isErrorFaced) {
			createLocationType();
		}
		createRoadType();
		createChargingPointType();
		addLocations();
		
	}
	
	private static void addLocations() {
		Vertex newDelhi = addLocation("New Delhi", 12.34, 12.34, "place", 0.8, 0.7, 0.5);
		Vertex ina = addLocation("INA", 13.34, 13.34, "place", 0.9, 0.6, 0.7);
		Vertex gp = addLocation("Green Park", 14.34, 14.34, "place", 0.7, 0.8, 0.6);
		Vertex ai = addLocation("AIIMS", 15.34, 15.34, "place", 0.7, 0.7, 0.7);
		Vertex hk = addLocation("Hauz Khaz", 16.34, 16.34, "place", 0.8, 0.7, 0.6);
		
		addRoad("Road 1", 100.5, newDelhi, ina);
		addRoad("Road 2", 90.5, ina, gp);
		addRoad("Road 3", 80.5, gp, ai);
		addRoad("Road 4", 70.5, ai, hk);
		addRoad("Road 4", 30.5, hk, ai);
		addRoad("Road 5", 66.5, ina, ai);
	}
	
	public static Vertex addLocation(String name, Double lat, Double lon, String locType, 
			Double trafficFactor, Double climateFactor, Double roadQualityFactor) {
    	Vertex location = graph.addVertex("class:Location");
    	location.setProperty("name", name);
    	location.setProperty("lat", lat);
    	location.setProperty("lon", lon);
    	location.setProperty("type", locType);
    	location.setProperty("trafficFactor", trafficFactor);
    	location.setProperty("climateFactor", climateFactor);
    	location.setProperty("roadQualityFactor", roadQualityFactor);
    	return location;
    }
    
    public static Edge addRoad(String name, Double distance, Vertex from, Vertex to) {
    	Edge road = graph.addEdge(null, from, to, "Road");
    	road.setProperty("name", name); 
    	road.setProperty("distance", df2.format(distance));
    	return road;
    }
    
    public static Vertex addStation(String name, Double lat, Double lon, String locType) {
    	Vertex location = graph.addVertex("class:Station");
    	location.setProperty("name", name);
    	location.setProperty("lat", lat);
    	location.setProperty("lon", lon);
    	location.setProperty("type", locType);
    	return location;
    }
    
    private static void createLocationType() {
    
        locationType = graph.createVertexType("Location");
    	locationType.createProperty("name", OType.STRING);
    	locationType.createProperty("lat", OType.DOUBLE);
    	locationType.createProperty("lon", OType.DOUBLE);
    	locationType.createProperty("type", OType.STRING);
    	locationType.createProperty("trafficFactor", OType.DOUBLE);
    	locationType.createProperty("climateFactor", OType.DOUBLE);
    	locationType.createProperty("roadQualityFactor", OType.DOUBLE);
    }

    private static void createRoadType() {
    	roadType = graph.createEdgeType("Road");
    	roadType.createProperty("name", OType.STRING);
    	roadType.createProperty("distance", OType.DOUBLE);
    }
    
    private static void createChargingPointType() {
    	chargerType = graph.createVertexType("Station");
    	chargerType.createProperty("name", OType.STRING);
    	chargerType.createProperty("lat", OType.DOUBLE);
    	chargerType.createProperty("lon", OType.DOUBLE);
    	chargerType.createProperty("type", OType.STRING);
    }
}
