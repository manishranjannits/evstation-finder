package com.niti.simulator.data;

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
	
	public static void fillSimulatorData(OrientGraphNoTx graphNoTx){
		graph = graphNoTx;
		boolean isErrorFaced = false;
		try {
		createLocationType();
		}catch(Exception e) {
			System.out.println("Facing error in refreshing database. Retrying...");
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
		Vertex newDelhi = addLocation("New Delhi", 12.34, 12.34, "place");
		Vertex ina = addLocation("INA", 13.34, 13.34, "place");
		Vertex gp = addLocation("Green Park", 14.34, 14.34, "place");
		Vertex ai = addLocation("AIIMS", 15.34, 15.34, "place");
		Vertex hk = addLocation("Hauz Khaz", 16.34, 16.34, "place");
		
		addRoad("Road 1", 100.5, newDelhi, ina);
		addRoad("Road 2", 90.5, ina, gp);
		addRoad("Road 3", 80.5, gp, ai);
		addRoad("Road 4", 70.5, ai, hk);
		addRoad("Road 4", 30.5, hk, ai);
		addRoad("Road 5", 66.5, ina, ai);
	}
	
	public static Vertex addLocation(String name, Double lat, Double lon, String locType) {
    	Vertex location = graph.addVertex("class:Location");
    	location.setProperty("name", name);
    	location.setProperty("lat", lat);
    	location.setProperty("lon", lon);
    	location.setProperty("type", locType);
    	return location;
    }
    
    public static Edge addRoad(String name, Double distance, Vertex from, Vertex to) {
    	Edge road = graph.addEdge(null, from, to, "Road");
    	road.setProperty("name", name);
    	road.setProperty("distance", distance);
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
