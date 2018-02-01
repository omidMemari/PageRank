import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.File;
import java.nio.charset.Charset;

public class PageRank {
	
	private ArrayList<ArrayList<Pair>> graph;  //Adjacency List
	private ArrayList<ArrayList<Pair>> revGraph; //graph with reversed edges
	private Map<String , Integer> doc;         //maps docID to integer
	private Double[] oldPR;
	private final Charset UTF8_CHARSET = Charset.forName("UTF-16");
	private Double[] newPR;
	private double[] sumOfEdgeW;
	private ArrayList<Integer> topic;
	private String filename;   
	private int numOfNodes= 166741;
	private int iteration;
	private double damp = 0.8;
	private String outputFile;
	PageRank(String filename, String mode) throws IOException, ParserConfigurationException, SAXException{
		this.filename = filename;
		int mod = Integer.parseInt(mode);
		iteration = 10;
		sumOfEdgeW = new double[numOfNodes];
		revGraph = new ArrayList<ArrayList <Pair>>();
		doc = new HashMap<String , Integer>();
		graph = new ArrayList<ArrayList <Pair>>();
		topic = new ArrayList<Integer>();
		oldPR = new Double[numOfNodes];
		newPR = new Double[numOfNodes];
		//Scanner s = new Scanner(System.in);
		//System.out.println("Enter your preferd options number: ");
		//System.out.println("1.Normal PageRank");  
		//System.out.println("2.Topic Specific PageRank for Literature and Art");
		//int input = new Integer(s.nextLine());
		//while(mod!=1 && mod!=2){
			//System.out.println("You should enter 1 or 2!");
			//input = new Integer(s.nextLine());
		//}
		System.out.println("Computing PageRank...");
		System.out.println("Please wait...");
		initGraph();
		readFile();
		fillM();
		if(mod == 1){
			computePageRank();
			outputFile = "../Results/PageRank.txt";
		}
		else if(mod == 2){
			readTopics();
			computeTopicSensetivePR();
			outputFile = "../Results/TopicSensetivePageRank.txt";
		}
		evalPR();
		printPR(outputFile);
		System.out.println("PageRank scores for all nodes stored in file : "+outputFile);
	}
	
	void fillM(){ //fills the edge weights with form of 2^(-weight) for comparing better
		for(int i = 0 ; i < graph.size() ; i++){
			sumOfEdgeW[i]=0;
			for(int j = 0 ; j < graph.get(i).size(); j++){
				sumOfEdgeW[i] += Math.pow(2, graph.get(i).get(j).getSecond());
			}
		}
		double d;
		for(int k = 0 ; k < graph.size() ; k++){
			for(int t = 0 ; t < graph.get(k).size(); t++){
				d = Math.pow(2, graph.get(k).get(t).getSecond())/sumOfEdgeW[k];
				graph.get(k).get(t).setSecond(d); 
			}
		}
	}
	void initGraph() throws IOException{
		ArrayList<Pair> listOfPairs;
		for(int j = 0 ; j < numOfNodes; j++ ){
			listOfPairs = new ArrayList<Pair>();
			graph.add(listOfPairs);
		}
		for(int j = 0 ; j < numOfNodes; j++ ){
			listOfPairs = new ArrayList<Pair>();
			revGraph.add(listOfPairs);
		}
	}
	void readFile() throws FileNotFoundException{
		Scanner file = new Scanner(new FileReader(filename));
		String source, dest;
		double edge;
		int numOfVertex=0, sourceIndex, destIndex;
		while(file.hasNextLine()){
			String line = file.nextLine();
			StringTokenizer st = new StringTokenizer(line);
			source = st.nextElement().toString();
			dest = st.nextElement().toString();
			edge = Double.parseDouble(st.nextElement().toString());
			
			if(!doc.containsKey(source)){
				doc.put(source, numOfVertex);
				sourceIndex = numOfVertex;
				numOfVertex+=1;
			}
			else
			{
				sourceIndex = doc.get(source);
			}
			if(!doc.containsKey(dest)){
				doc.put(dest, numOfVertex);
				destIndex = numOfVertex;
				numOfVertex+=1;
			}
			else
			{
				destIndex = doc.get(dest);
			}
			Pair p1 = new Pair(destIndex , edge);
			graph.get(sourceIndex).add(p1);
			Pair p2 = new Pair(sourceIndex , edge);
			revGraph.get(destIndex).add(p2);
		}
	}
	
	void computePageRank(){
		double t = (double)1/numOfNodes;
		for(int i = 0; i < numOfNodes ; i++){
			oldPR[i] = t;
			newPR[i] = 0.0;
			
		}
		double dp;
		int temp;
		double temp2,d;
		while (iteration > 0)
		{
			dp = 0;
			for(int j = 0; j< numOfNodes; j++){
				if(graph.get(j).size() == 0)
					dp = dp + damp/numOfNodes*oldPR[j];
			}
			for (int k = 0 ; k < numOfNodes ; k++){
				t = (double)(dp + (1-damp)/numOfNodes);
						newPR[k] = t;
				for(int s = 0; s < revGraph.get(k).size(); s++){
					temp = revGraph.get(k).get(s).getFirst();
					temp2 = revGraph.get(k).get(s).getSecond();
					d = Math.pow(2, temp2)/sumOfEdgeW[temp];
					t = damp*oldPR[temp]*(d);
					newPR[k] = newPR[k] + t; 
				}
			}
			for(int i =0 ; i < numOfNodes ; i++){
				oldPR[i] = newPR[i];
			}
			iteration -= 1;
		}
	}
	void evalPR(){
		double sum=0;
		for(int i = 0 ; i < numOfNodes; i++){
			sum += oldPR[i];
		}
		System.out.println("the sum of PageRanks for all nodes : "+ sum);
		
	}
	void printPR(String filename) throws IOException{
		FileWriter fw = new FileWriter(filename);
		BufferedWriter output = new BufferedWriter(fw);
		//sort(oldPR);
		Arrays.sort(oldPR);
		Doc_PR_Pair p;
		ArrayList<Doc_PR_Pair> doc_PR = new ArrayList<Doc_PR_Pair>();
		for (Map.Entry<String, Integer> entry : doc.entrySet()) {
		    String docID = entry.getKey();
		    Integer docIndex = entry.getValue();
		    p = new Doc_PR_Pair(docID, oldPR[docIndex]);
		    doc_PR.add(p);
		}
		Doc_PR_Pair[] array = new Doc_PR_Pair[numOfNodes];
		for(int i = 0 ; i < numOfNodes ; i++)
			array[i] = doc_PR.get(i);
		Arrays.sort(array);
		for(Doc_PR_Pair dp : array){
			output.write(dp.getFirst()+"  "+dp.getSecond()+"\n");
		}
		output.close();
	}
	
	void readTopics() throws ParserConfigurationException, SAXException, IOException{
		
		File fXmlFile = new File("../Results/hamshahri/categories.txt");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document Doc = dBuilder.parse(fXmlFile);
		Doc.getDocumentElement().normalize();
	    NodeList nList = Doc.getElementsByTagName("DOC");
	    int docID;
	    Node nNode1 = nList.item(0);
	    Element eElement1 = (Element) nNode1;
	    String s1 = eElement1.getElementsByTagName("CAT").item(1).getTextContent();
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				String s2 = eElement.getElementsByTagName("CAT").item(1).getTextContent();
				String DocNo = eElement.getElementsByTagName("DOCNO").item(0).getTextContent();
				Charset.forName("UTF-8").encode(DocNo);
				if(s2.equals(s1)){
					docID = doc.get(DocNo);
					topic.add(docID);	
				}
			}	
	}	
	}
	
	void computeTopicSensetivePR(){
		double t = (double)1/numOfNodes;
		for(int i = 0; i < numOfNodes ; i++){
			oldPR[i] = t;
		}
		double dp;
		int temp;
		double temp2,d;
		while (iteration > 0)
		{
			dp = 0;
			for(int j = 0; j< numOfNodes; j++){
				if(graph.get(j).size() == 0)
					dp = dp + damp/numOfNodes*oldPR[j];
			}
			for (int k = 0 ; k < numOfNodes ; k++){
				newPR[k] = dp;
				t = (double)((1-damp)/topic.size());
				if(topic.contains(k))
					newPR[k] = newPR[k] + t;
				for(int s = 0; s < revGraph.get(k).size(); s++){
					temp = revGraph.get(k).get(s).getFirst();
					temp2 = revGraph.get(k).get(s).getSecond();
					d = Math.pow(2, temp2)/sumOfEdgeW[temp];
					t = damp*oldPR[temp]*(d);
					newPR[k] = newPR[k] + t; 
				}
			}
			for(int i =0 ; i < numOfNodes ; i++){
				oldPR[i] = newPR[i];
			}
			iteration -= 1;
		}
	}
	
	byte[] encodeUTF8(String string) {
	    return string.getBytes(UTF8_CHARSET);
	}
	void sort(Double[] oldPR2){
		double temp;
		System.out.println("in sort function");
		for(int i = 0 ; i < oldPR2.length ; i++)
			for(int j = i ; j < oldPR2.length - 1 ; j++)
				if(oldPR2[j] > oldPR2[j+1]){
					temp = oldPR2[j];
					oldPR2[j] = oldPR2[j+1];
					oldPR2[j+1] = temp;
				}
		System.out.println("sort ended!");
	}
	
}
