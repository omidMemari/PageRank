import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;


public class Program {

	
	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
		PageRank pg; 
		pg = new PageRank("../Results/hamshahri/Graph_Hamshahri1.txt" , args[0]);
		

	}

}
