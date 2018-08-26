package com.niti.service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.niti.constants.ServiceConstants;
import com.niti.simulator.data.SimulatorData;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

public class DijkastraTraverser {

    private OrientGraphNoTx         graph;          //graph DB
    private Set<String>         visitedSet;          //visited rids
    private Set<String>         unvisitedSet;          //to visit rids
    private Map<String,Double> idWeightMap;          //idWeightMap(i)     < @rid, weight_to_get_to_@rid >
    private Map<String,String>  idPrevNodeIdMap;          //idPrevNodeIdMap(i)     < @rid, previous_node_in_the_shortest_path >
    private String              eClass;     //edge class to use
    private String              prop;       //weight property to use on the edge

    public DijkastraTraverser(OrientGraphNoTx g, String e, String p){
        this.graph= g;
        this.eClass = e;
        this.prop = p;
        visitedSet = new HashSet<String>();
        unvisitedSet = new HashSet<String>();
        idWeightMap = new HashMap<String,Double>();
        idPrevNodeIdMap = new HashMap<String,String>();
    }


    //private methods

    // (Vertex start_vertex, Vertex dest_vertex, Direction.IN/OUT/BOTH, Set of edge rids to exclude) 
    private void findPath(Vertex startV, Vertex endV, Direction dir, Set<String> excludeEdgeRids){      

    //init
    	visitedSet.clear();
    	unvisitedSet.clear();
        idWeightMap.clear();
        idPrevNodeIdMap.clear();

    //step1
        Iterator<Vertex> vertex = graph.getVerticesOfClass(ServiceConstants.LOCATION_CLASS).iterator();
        while(vertex.hasNext()){
            Vertex ver = vertex.next();
            idWeightMap.put(ver.getId().toString(), Double.MAX_VALUE);
            unvisitedSet.add(ver.getId().toString());
        }
        idWeightMap.put(startV.getId().toString(), 0D);        //idWeightMap(startV) = 0
        idPrevNodeIdMap.put(startV.getId().toString(), null);     //idPrevNodeIdMap(startV) = null
        unvisitedSet.remove(startV.getId().toString());        //startV visited => removed from unvisitedSet
        visitedSet.add(startV.getId().toString());           //  and added in visitedSet



        Iterator<Vertex> near = startV.getVertices(dir, eClass).iterator();

        while(near.hasNext()){
            Vertex nearVertex = near.next();
            
            idPrevNodeIdMap.put(nearVertex.getId().toString(), startV.getId().toString());            //idPrevNodeIdMap(i) = startV
            idWeightMap.put(nearVertex.getId().toString(), weight(startV.getId().toString(), nearVertex.getId().toString(),dir,excludeEdgeRids));     //idWeightMap(i) = weight(startV, i)
        }

    //step2
        Boolean cont = false;
        Iterator<String> t = unvisitedSet.iterator();
        while(t.hasNext()){
            String i = t.next();
            if(idWeightMap.get(i)!=Double.MAX_VALUE){
                cont = true;
            }
        }
        while(cont){

            String j = startV.getId().toString();
            Double ff = Double.MAX_VALUE;
            t = unvisitedSet.iterator();
            while(t.hasNext()){
                String i = t.next();
                if(idWeightMap.get(i)<=ff){
                    ff = idWeightMap.get(i);
                    j = i;
                }
            }
            unvisitedSet.remove(j);
            visitedSet.add(j);
            if(unvisitedSet.isEmpty()){
                break;
            }

        //step3
            near = graph.getVertex(j).getVertices(dir, eClass).iterator();

            while(near.hasNext()){
                Vertex vic = near.next();
                String i = vic.getId().toString();
                if( (unvisitedSet.contains(i)) && (idWeightMap.get(i) > (idWeightMap.get(j) + weight(j,i,dir,excludeEdgeRids))) ){
                    if(weight(j,i,dir,excludeEdgeRids)==Double.MAX_VALUE){
                    	idWeightMap.put(i, Double.MAX_VALUE);
                    }else{
                    	idWeightMap.put(i, (idWeightMap.get(j) + weight(j,i,dir,excludeEdgeRids)));
                    }
                    idPrevNodeIdMap.put(i, j);
                }
            }

            //shall we continue?
            cont = false;
            t = unvisitedSet.iterator();
            while(t.hasNext()){
                String i = t.next();
                if(idWeightMap.get(i)!=Double.MAX_VALUE){
                    cont = true;
                }
            }
        }
    }

    /**
     * Returns the weight/distance between 2 Vertices
     * @param rid_a
     * @param rid_b
     * @param dir
     * @param excl
     * @return
     */
    private double weight(String rid_a, String rid_b, Direction dir, Set<String> excl){        //in case of multiple/duplicate edges return the lightest
        Double d = Double.MAX_VALUE;
        Double dd = 0D;
        rid_b = "v(Location)["+rid_b+"]";
        String rid_b_station = "v(Station)["+rid_b+"]";

        if(excl==null){
            excl = new HashSet<String>();
        }
        Vertex a = graph.getVertex(rid_a);

        Iterator<Edge> eS = a.getEdges(dir, eClass).iterator();
        Set<Edge> goodEdges = new HashSet<Edge>();
        
        while(eS.hasNext()){
            Edge e = eS.next();
            
            if(e.getProperty("out").toString().equals(rid_b) || e.getProperty("in").toString().equals(rid_b) || 
            		e.getProperty("out").toString().equals(rid_b_station) || e.getProperty("in").toString().equals(rid_b_station) ) {
            	goodEdges.add(e);
            }
            
        }
        Iterator<Edge> edges= goodEdges.iterator();
        while(edges.hasNext()){
            Edge e=edges.next();
                dd = e.getProperty(prop);
                if(dd<d){
                    d=dd;
                }
        }
        
        return d;
    }
    
    /**
     * Add Required number of Stations between 2 Vertices
     * @param prev
     * @param current
     * @param distance
     * @param mileage
     * @param path
     */
    
    private void addStationsBetween2Nodes(Vertex prev, Vertex current, Double distance, Double mileage, List<Vertex> path) {
    	Vertex chargerVertex = null;
    	double prevLat;
    	double prevLon;
    	double currentLat;
    	double currentLon;
    	double lat;
    	double lon;
    	double distanceLeft;
    	int countOfStations = (int)(distance/mileage);
    	
    	for(int c=1; c<= countOfStations && distance>mileage; c++) {
    		prevLat = prev.getProperty("lat");
        	prevLon = prev.getProperty("lon");
        	currentLat = current.getProperty("lat");
        	currentLon = current.getProperty("lon");
        	lat = (currentLat + prevLat) * mileage * c/distance;
        	lon = (currentLon + prevLon) * mileage * c/distance; 
        	chargerVertex = SimulatorData.addStation(ServiceConstants.CHARGER_CLASS, lat, lon, ServiceConstants.CHARGER_CLASS);
        	SimulatorData.addRoad("Charger 1", mileage, prev, chargerVertex);
			distanceLeft = distance - mileage;
			SimulatorData.addRoad("Charger 2", distanceLeft, chargerVertex, current);
			Edge edge = getEdge(prev, current);
			graph.removeEdge(edge);
			path.add(chargerVertex);
			prev = chargerVertex;
			distance = distanceLeft;
    	}
    }
    
    /**
     * Returns Edge between 2 neighbouring Vertices
     * @param start
     * @param next
     * @return
     */
    private Edge getEdge(Vertex start, Vertex next) {
    	Iterator<Edge> edgeIter = start.getEdges(Direction.OUT, ServiceConstants.ROAD_CLASS).iterator();
    	while (edgeIter.hasNext()) {
    		Edge edge = edgeIter.next();
    		Vertex foundVertex = edge.getVertex(Direction.IN);
    		if(foundVertex!= null && foundVertex.getId().equals(next.getId())) {
    			return edge;
    		}
		}
    	return null;
    }
    
    private double getMileage(Vertex concernedVertex) {
    	double mileage = 0D;
    	double claimedMilage = 40D;
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

    //public methods

    /**
     * This method returns path by traversing without inserting Stations
     * @param startV
     * @param endV
     * @param dir
     * @param exclECl
     * @return
     */
    public List<Vertex> getPath (Vertex startV, Vertex endV, Direction dir, Set<String> exclECl){
        String j,i;
        List<Vertex> ppp = new ArrayList<Vertex>();
        List<Vertex> path = new ArrayList<Vertex>();

        findPath(startV, endV, dir, exclECl);
        i = endV.getId().toString();
        path.add(endV);
        if(idWeightMap.get(endV.getId().toString()) == Double.MAX_VALUE){
            return null;
        }
        while(!i.equals(startV.getId().toString())){
            j = idPrevNodeIdMap.get(i);
            if(j == null){
                return null;
            }
            path.add(graph.getVertex(j));
            i = j;
        }
        for(int a=0, b=path.size()-1;a<path.size();a++, b--){
            ppp.add(a, path.get(b));
        }

        return ppp;
    }
    
    /**
     * This Method returns the path with Stations inserted
     * @param startV
     * @param endV
     * @param dir
     * @param exclECl
     * @return
     */
    public List<Vertex> getPathWithStation (Vertex startV, Vertex endV, Direction dir, Set<String> exclECl){
        String j,i;
        List<Vertex> ppp = new ArrayList<Vertex>();
        List<Vertex> path = new ArrayList<Vertex>();
        
        //Traverse to find path and populated the required variables
        findPath(startV, endV, dir, exclECl);
        
        i = endV.getId().toString();
        path.add(endV);
        
        if(idWeightMap.get(endV.getId().toString()) == Double.MAX_VALUE){
            return null;
        }
        //Mileage to be a calculative value
        Double mileage = 40D;
        
        //Start Traversing from End Vertex till Start Vertex is Reached
        while(!i.equals(startV.getId().toString())){
        	
            j = idPrevNodeIdMap.get(i);
            if(j == null){
            	//If Previous vertex to End Vertex is null, return null
                return null;
            }
            Vertex current = graph.getVertex(i);
            Vertex prev = graph.getVertex(j);
            //Continue backward traversal to find previous vertex which is of Location class or type = place
            while(!prev.getProperty(ServiceConstants.PROPERTY_TYPE).toString().equalsIgnoreCase(ServiceConstants.TYPE_PLACE) && j != null) {
            	j = idPrevNodeIdMap.get(j);
            	prev = graph.getVertex(j);
            }
            
            Double distance = getWeight(prev, current, Direction.OUT, exclECl);
            mileage = getMileage(prev);
            
            if(distance > mileage) {
            	addStationsBetween2Nodes(prev, current, distance, mileage, path);
            }
            
            path.add(graph.getVertex(j));
            i = j;
        }
        for(int a=0, b=path.size()-1;a<path.size();a++, b--){
            ppp.add(a, path.get(b));
        }

        return ppp;
    }
    
    
 
    
    /**
     * Returns String representation of Path with Stations
     * @param startV
     * @param endV
     * @param dir
     * @param exclECl
     * @return
     */
    public List<String> getPathWithStationString(Vertex startV, Vertex endV, Direction dir, Set<String> exclECl){
    	List<String> pathS = new ArrayList<String>();
    	List<Vertex> path = getPathWithStation(startV, endV, dir, exclECl);
    	if(path == null){
            return null;
        }
        for(Vertex v : path){
            pathS.add(v.getProperty(ServiceConstants.PROPERTY_NAME).toString());
        }
        return pathS;
    }

    /**
     * Returns String representation of Path without Stations
     * @param startV
     * @param endV
     * @param dir
     * @param exclECl
     * @return
     */
    public List<String> getPathString (Vertex startV, Vertex endV, Direction dir, Set<String> exclECl){
        List<String> pathS = new ArrayList<String>();
        List<Vertex> path = getPath(startV, endV, dir, exclECl);

        if(path == null){
            return null;
        }
        for(Vertex v : path){
            pathS.add(v.getProperty(ServiceConstants.PROPERTY_NAME).toString());
        }
        return pathS;
    }

    /**
     * Returns weight between a Start Vertex and End Vertex
     * @param startV
     * @param endV
     * @param dir
     * @param exclECl
     * @return
     */
    public Double getWeight(Vertex startV, Vertex endV, Direction dir, Set<String> exclECl){
        findPath(startV, endV, dir,exclECl);
        return idWeightMap.get(endV.getId().toString());
    }
}