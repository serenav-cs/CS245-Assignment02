import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.json.simple.parser.ParseException;

import java.io.Reader;
import java.io.FileReader;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.*;
import java.util.NoSuchElementException;

public class A2 {

    public static class Graph
    {
        private HashMap<String, ArrayList<String>> adjHash;

        //Constructor
        public Graph(){
            adjHash = new HashMap<String, ArrayList<String>>();
        }

        //Adds a vertex to the graph.
        public void addVertex(String vertex){
            ArrayList<String> list = new ArrayList<String>();
            adjHash.putIfAbsent(vertex, list);		//Will not add a vertex that already exists.
        }

        //Adds an edge between two vertices.
        public void addEdge(String v1, String v2){
            ArrayList<String> v1list = adjHash.get(v1);
            //if edge not there, add it
            if (!v1list.contains(v2)){
                v1list.add(v2);
            }
            adjHash.replace(v1, v1list);
        }

        //If there's an edge between two vertices it returns true.
        public boolean edgeExists(String v1, String v2){
            ArrayList<String> v1edges = adjHash.get(v1);
            if (v1edges.contains(v2)){
                return true;
            }
            return false;
        }

        //Returns the number of elements in the hashtable.
        public int getSize(){
            return adjHash.size();
        }

        //Returns a list of all the edges a vertex is connected to.
        public ArrayList<String> neighbors(String vertex){
            return adjHash.get(vertex);
        }

        //Returns all the vertices in the graph produced.
        public ArrayList<String> getVertices(){
            ArrayList<String> keys = new ArrayList<String>(adjHash.keySet());
            return keys;
        }

        //Prints out all the key-value pairs of the hashtable.
        public void printGraph(){
            for (String key : adjHash.keySet()){
                String value = adjHash.get(key).toString();
                System.out.println();
                System.out.println(key + " " + value);
            }
        }
    }

    //Circular Queue
    public static class ArrayQueue<T>
    {
        Object[] tempArray = new Object[10];
        T[] arr;
        int head;
        int tail;

        //Constructor
        public ArrayQueue(){
            arr = (T[]) tempArray;
            head = 0;
            tail = 0;
        }

        //See if queue is empty.
        public boolean empty(){
            if (head == tail){
                return true;
            }
            return false;
        }

        //Removing and returning the 1st element in the queue.
        public T dequeue(){
            if (empty()){
                throw new NoSuchElementException();
            }
            T temp = arr[head];
            head = (head + 1) % arr.length;
            return temp;
        }

        //Adding item to the END of the queue.
        public void enqueue(T item)
        {
            if ((tail + 1) % arr.length == head){
                grow_array();
            }
            arr[tail++] = item;		//Copies item + increments tail.
            tail = tail % arr.length;
        }

        //Expanding the array
        protected void grow_array(){
            Object[] tempObject = new Object[arr.length * 2];	//Creates a temporary array that's 2x bigger.
            T[] temp = (T[]) tempObject;
            for (int i = 0; i < arr.length; i++){		//Iterates through each slot in queue.
                temp[i] = arr[(head + i) % arr.length];		//Copies each item in queue to temporary array
            }
            tail = arr.length - 1;		//Re-initializing tail
            arr = temp;
            head = 0;		//Re-initializing head.
        }
    }

    //BFS algorithm for shortest path between two vertices (unweighted)
    private static ArrayList<String> BFS(Graph graph, String actor1, String actor2)
    {
        HashMap<String, Boolean> visited = new HashMap<String, Boolean>();		//Tracking what vertices were visited
        HashMap<String, String> previous = new HashMap<String, String>();
        ArrayList<String> path = new ArrayList<String>();						//Records the path
        ArrayQueue<String> queue = new ArrayQueue<String>();

        String current = actor1;		//Adds the source vertex to the queue and visited table.
        queue.enqueue(current);
        visited.put(current, true);

        while (!queue.empty())
        {
            current = queue.dequeue();						//Removes the 1st item from the queue.
            if (current.equalsIgnoreCase(actor2))			//Go until we find the destination vertex.
            {
                break;
            }
            else
            {
                for (String actor : graph.neighbors(current))		//Loops the current vertex's edges, adds them to queue and the visited table to keep track
                {
                    if (visited.get(actor) == null)
                    {
                        queue.enqueue(actor);
                        visited.put(actor, true);
                        previous.put(actor, current);
                    }
                }
            }
        }
        if (!current.equalsIgnoreCase(actor2))				//If no destination found after the loop, we can conclude theres no path from the actors
        {
            System.out.println("There is no path that exists between Actor 1 and Actor 2.");
            return null;
        }

        String a = actor2;			//add previos to final
        while (a != null)
        {
            path.add(a);
            a = previous.get(a);
        }

        return path;
    }

    public static void main(String[] args) {

        Graph graph = new Graph();

        try {
            Reader reader = new FileReader("/Users/serenavillanueva/IdeaProjects/cs245/assignment-02-cs245-serenav-cs/src/creds.csv");
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
            JSONParser jsonParser = new JSONParser();

            boolean firstround = true;
            for (CSVRecord csvRecord : csvParser) {

                if (!firstround) {

                    try {

                        String cast = csvRecord.get(2);
                        Object obj = jsonParser.parse(cast);
                        JSONArray castarray = (JSONArray)obj;

                        for (int j = 0; j < castarray.size(); j++) {

                            Object item1 = castarray.get(j);
                            JSONObject jsonitem1 = (JSONObject)item1;
                            String name1 = (String)jsonitem1.get("name");
                            graph.addVertex(name1);

                            for (int i = 0; i < castarray.size(); i++) {

                                Object item2 = castarray.get(i);
                                JSONObject jsonitem2 = (JSONObject)item2;
                                String name2 = (String)jsonitem2.get("name");
                                graph.addEdge(name1, name2);

                            }
                        }

                    }
                    catch(ParseException e){
                        //e.printStackTrace();
                    }
                }
                firstround = false;
            }
        } catch(Exception e) {
            //System.out.println("File " + args[0] + "is invalid or is in the wrong format.");
            System.out.println("File is invalid or is in the wrong format.");
        }



        // Getting input from user

        Scanner scan = new Scanner(System.in);
        System.out.println();
        System.out.print("Enter the first actors name: ");
        String actor1 = scan.nextLine();
        System.out.print("Enter the second actors name: ");
        String actor2 = scan.nextLine();

        //ignore capitalization from input + valid actor name
        boolean actor1found = false;
        boolean actor2found = false;
        ArrayList<String> vertices = graph.getVertices();
        for (String vertex : vertices) {
            if (actor1.equalsIgnoreCase(vertex)) {
                actor1found = true;
                actor1 = vertex;
            }
            if (actor2.equalsIgnoreCase(vertex)) {
                actor2found = true;
                actor2 = vertex;
            }
        }
        if (!actor1found || !actor2found) {
            System.out.println("No such actor.");
            return;
        }

        //Runs BFS on graph
        ArrayList<String> path = BFS(graph, actor1, actor2);

        //Results displayed
        System.out.println("------------------------------------------------");
        System.out.println("Path from " + actor1 + " to " + actor2 + ": ");
        System.out.print(path.get(path.size() - 1) + " ");
        for (int k = path.size() - 2; k >= 0; k--) {
            System.out.print("--> ");
            System.out.print(path.get(k) + " ");
        }
        System.out.println();
        System.out.println("------------------------------------------------");
    }

}


