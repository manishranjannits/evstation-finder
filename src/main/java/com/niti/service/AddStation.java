package com.niti.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.niti.constants.ServiceConstants;
import com.niti.simulator.data.SimulatorData;
import com.niti.web.ui.Application;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

public class AddStation {

	public void addStations(boolean isWeighted) {
		try {
			//COde for dijkastra
			addByDijkastra(isWeighted);

		}catch(Exception e) {

		}
		try {
			Thread.sleep(5000);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}


	private void addByDijkastra(boolean isWeighted) {
		String edgeClassName = "Road";
		String weightProp = "distance";

		OrientGraphNoTx graph = null;
		try {
			graph = Application.getGraphNoTx();
			DijkastraTraverser traverser = new DijkastraTraverser(graph, edgeClassName, weightProp, isWeighted);

			List<Vertex> listOfVertices = new CopyOnWriteArrayList<>();
			graph.getVertices().forEach(v -> {
				listOfVertices.add(v);
			});
			System.out.println("No. of nodes : "+listOfVertices.size());
			listOfVertices.parallelStream().filter(c -> c != null).forEach(current -> {
						try{
							for (int nextCounter = 0; nextCounter < listOfVertices.size(); nextCounter++) {
								if (current.getProperty("id") == listOfVertices.get(nextCounter).getProperty("id")) {
									continue;
								}
								//Solve for each location as starting poin1t
								Vertex next = listOfVertices.get(nextCounter);
								traverser.getPathWithStationString(current, next, Direction.OUT, null);
							}
						} catch (Exception e){}		}

			);
			addForDelta(graph, isWeighted);

		}catch(Exception e) {
			e.printStackTrace();
		}

	}

	private void addForDelta(OrientGraphNoTx graph, boolean isWeighted) {

		try {

			//Add code here
			graph.getVerticesOfClass("Location").forEach(v -> {
				StringBuilder sb = new StringBuilder();
				sb.append(v.getId().toString()+" (");
				for(String property : v.getPropertyKeys()) {
					sb.append(property+": "+v.getProperty(property)).append(" ");
				}
				sb.append(")\n");
				List<Edge> incomingEdges = new ArrayList<>();

				v.getEdges(Direction.IN, "Road").forEach(edge -> {
					incomingEdges.add(edge);
				});

				for (Edge incoming : incomingEdges) {
					Vertex incomingVertex = incoming.getVertex(Direction.OUT);
					v.getEdges(Direction.OUT, "Road").forEach(outgoing -> {
						Vertex outgoingVertex = outgoing.getVertex(Direction.IN);
						if(incomingVertex.getProperty("type").toString().equalsIgnoreCase("Charger")) {
							double incomingDistance = incoming.getProperty("distance");
							double distance = incomingDistance + (double)outgoing.getProperty("distance");
							double mileage = ServiceConstants.MILEAGE;
							if(isWeighted) {
								mileage = getMileage(v);
							}

							if(distance > mileage) {
								outgoing = addStationsBetween2Stations(outgoingVertex, v, incomingDistance, mileage, graph);
							}
						}
					});
				}

				System.out.println(sb.toString());
			});
			graph.commit();
		} catch(Exception e){
			graph.rollback();
			e.printStackTrace();
		}
	}

	private double getMileage(Vertex concernedVertex) {
		double mileage = 0D;
		double claimedMilage = ServiceConstants.CLAIMED_MILEAGE;
		double trafficFactor;
		double climateFactor;
		double roadQualityFactor;

		trafficFactor = concernedVertex.getProperty("trafficFactor");
		climateFactor = concernedVertex.getProperty("climateFactor");
		roadQualityFactor = concernedVertex.getProperty("roadQualityFactor");

		double combinedFactor = trafficFactor * climateFactor * roadQualityFactor;
		mileage = claimedMilage * combinedFactor;

		return mileage;
	}

	private Edge addStationsBetween2Stations(Vertex currentStation,Vertex connectingLocation,
											 Double distance, Double mileage, OrientGraphNoTx graph) {
		Vertex chargerVertex = null;
		double prevLat;
		double prevLon;
		double currentLat;
		double currentLon;
		double lat;
		double lon;
		double distanceLeft = mileage - distance;
		//int countOfStations = (int)(distance/mileage);

		//for(int c=1; c<= countOfStations && distance>mileage; c++) {
		prevLat = connectingLocation.getProperty("lat");
		prevLon = connectingLocation.getProperty("lon");
		currentLat = currentStation.getProperty("lat");
		currentLon = currentStation.getProperty("lon");
		lat = (currentLat + prevLat) * distanceLeft /mileage;
		lon = (currentLon + prevLon) * distanceLeft /mileage;
		double distFromNewStation = mileage - distanceLeft;
		chargerVertex = SimulatorData.addStation("Charger", lat, lon, "Charger");
		Edge newOutgoing = SimulatorData.addRoad("Charger 1", distanceLeft, connectingLocation, chargerVertex);
		//distanceLeft = distance - mileage;
		SimulatorData.addRoad("Charger 2", distFromNewStation, chargerVertex, currentStation);
		Edge edge = getEdge(connectingLocation, currentStation);
		graph.removeEdge(edge);
		//prev = chargerVertex;
		//distance = distanceLeft;
		//}
		return newOutgoing;
	}

	/**
	 * Returns Edge between 2 neighbouring Vertices
	 * @param start
	 * @param next
	 * @return
	 */
	private Edge getEdge(Vertex start, Vertex next) {
		Iterator<Edge> edgeIter = start.getEdges(Direction.OUT, "Road").iterator();
		while (edgeIter.hasNext()) {
			Edge edge = edgeIter.next();
			Vertex foundVertex = edge.getVertex(Direction.IN);
			if(foundVertex!= null && foundVertex.getId().equals(next.getId())) {
				return edge;
			}
		}
		return null;
	}

}
