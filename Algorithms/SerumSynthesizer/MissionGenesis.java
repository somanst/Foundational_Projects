// Class representing the mission of Genesis

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MissionGenesis {

    // Private fields
    private MolecularData molecularDataHuman; // Molecular data for humans
    private MolecularData molecularDataVitales; // Molecular data for Vitales

    // Getter for human molecular data
    public MolecularData getMolecularDataHuman() {
        return molecularDataHuman;
    }

    // Getter for Vitales molecular data
    public MolecularData getMolecularDataVitales() {
        return molecularDataVitales;
    }

    // Method to read XML data from the specified filename
    // This method should populate molecularDataHuman and molecularDataVitales fields once called
    public void readXML(String filename){
        File fXmlFile = new File(filename);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        Document doc = null;
        try {
            doc = dBuilder.parse(fXmlFile);
        } catch (SAXException | IOException e) {
            throw new RuntimeException(e);
        }
        doc.getDocumentElement().normalize();

        NodeList human = doc.getElementsByTagName("HumanMolecularData");
        NodeList humanMolecules =  human.item(0).getChildNodes();

        NodeList vital = doc.getElementsByTagName("VitalesMolecularData");
        NodeList vitalMolecules =  vital.item(0).getChildNodes();

        infoExtract(humanMolecules, 0);
        infoExtract(vitalMolecules, 1);
    }

    public void infoExtract(NodeList listy, int mode){
        List<Molecule> out = new ArrayList<>();
        for (int temp = 0; temp < listy.getLength(); temp++) {
            Node nNode = listy.item(temp);
            if(nNode.getNodeType() == Node.ELEMENT_NODE){
                Element moleculeElement = (Element) nNode;

                String id = moleculeElement.getElementsByTagName("ID").item(0).getTextContent();
                String bondStrength = moleculeElement.getElementsByTagName("BondStrength").item(0).getTextContent();
                NodeList bonds = moleculeElement.getElementsByTagName("Bonds");
                List<String> bondList = new ArrayList<>();

                for (int j = 0; j < bonds.getLength(); j++) {
                    Element bondElement = (Element) bonds.item(j);
                    NodeList moleculeIDs = bondElement.getElementsByTagName("MoleculeID");
                    for (int k = 0; k < moleculeIDs.getLength(); k++) {
                        String bondMoleculeID = moleculeIDs.item(k).getTextContent();
                        bondList.add(bondMoleculeID);
                    }
                }
                Molecule mol = new Molecule(id, Integer.parseInt(bondStrength), bondList);
                out.add(mol);
            }
        }
        if(mode == 0){
            molecularDataHuman = new MolecularData(out);
        } else{
            molecularDataVitales = new MolecularData(out);
        }
    }
}