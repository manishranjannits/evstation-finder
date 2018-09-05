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
		/*Vertex lg = addLocation("Lodhi Garden", 28.5931, 77.2197, "place", 0.8, 0.7, 0.5);
		Vertex jb = addLocation("Jor Bagh", 28.5878, 77.2153, "place", 0.9, 0.6, 0.7);
		Vertex ai = addLocation("AIIMS", 28.62623, 77.21808, "place", 0.7, 0.8, 0.6);
		Vertex in = addLocation("INA", 28.576278, 77.212032, "place", 0.7, 0.7, 0.7);
		Vertex hk = addLocation("Hauz Khaz", 28.549507, 77.203613, "place", 0.8, 0.7, 0.6)*/;
		Vertex lg1 = addLocation("Lodhi Garden",28.5903491,77.2206009,"place",0.8,0.7,0.5);
		//3.971
		Vertex lg2 = addLocation("AIIMS Campus",28.568436,77.20870699999999,"place",0.8,0.7,0.5);
		//4.663
		Vertex lg3 = addLocation("Lajpat Nagar",28.5677017,77.24327629999999,"place",0.8,0.7,0.5);
		//4.324
		Vertex lg4 = addLocation("Nehru Place",28.550269,77.25022009999999,"place",0.8,0.7,0.5);
		//6.625
		Vertex lg5 = addLocation("Hauz Khas",28.549447,77.200136,"place",0.8,0.7,0.5);
		//11.81
		Vertex lg6 = addLocation("Mahipalpur",28.5448775,77.1280896,"place",0.8,0.7,0.5);
		//9.807
		Vertex lg7 = addLocation("Shankar Chowk Rd",28.5080697,77.08177909999999,"place",0.8,0.7,0.5);
		//3.691
		Vertex lg8 = addLocation("DLF Phase 2",28.487734,77.0880186,"place",0.8,0.7,0.5);
		//8.328

		Vertex lg9 = addLocation("Katwaria Sarai",28.541067,77.1895186,"place",0.8,0.7,0.5);
		//4.972
		Vertex lg10 = addLocation("Vasant Kunj Marg",28.5192055,77.1606662,"place",0.8,0.7,0.5);
		//8.301
		Vertex lg11 = addLocation("National Highway 236",28.4803148,77.1253461,"place",0.8,0.7,0.5);


		addRoad("Road 1", 3.97, lg1, lg2);
		addRoad("Road 2", 4.66, lg2, lg3);
		addRoad("Road 3", 4.3, lg3, lg4);
		addRoad("Road 4", 3.2, lg4, lg5);
		addRoad("Road 4", 11.8, lg5, lg6);
		addRoad("Road 5", 9.8, lg6, lg7);
		addRoad("Road 6", 3.7, lg7, lg8);
		addRoad("Road 7", 4.972, lg5, lg9);
		addRoad("Road 8", 4.972, lg9, lg10);
		addRoad("Road 9", 3.7, lg10, lg11);

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
