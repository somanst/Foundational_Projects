import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class UrbanInfrastructureDevelopment implements Serializable {
    static final long serialVersionUID = 88L;

    /**
     * Given a list of Project objects, prints the schedule of each of them.
     * Uses getEarliestSchedule() and printSchedule() methods of the current project to print its schedule.
     * @param projectList a list of Project objects
     */
    public void printSchedule(List<Project> projectList) {
        // TODO: YOUR CODE HERE
        for(Project project : projectList){
            project.printSchedule(project.getEarliestSchedule());
        }
    }

    /**
     * TODO: Parse the input XML file and return a list of Project objects
     *
     * @param filename the input XML file
     * @return a list of Project objects
     */
    public List<Project> readXML(String filename){
        List<Project> projectList = new ArrayList<>();

        File fXmlFile = new File(filename);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
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

        NodeList xmlProjectList = doc.getElementsByTagName("Project");

        for (int temp = 0; temp < xmlProjectList.getLength(); temp++) {
            Node nNode = xmlProjectList.item(temp);
            if(!nNode.getNodeName().equals("Project")) continue;

            NodeList xmlTaskList = nNode.getChildNodes().item(3).getChildNodes();
            String projectName = nNode.getChildNodes().item(1).getTextContent();

            List<Task> taskList = new ArrayList<>();

            for(int i = 0; i < xmlTaskList.getLength(); i++){
                Node taskNode = xmlTaskList.item(i);
                if(!taskNode.getNodeName().equals("Task")) continue;
                taskList.add(taskCreator(taskNode));
            }

            Project project = new Project(projectName, taskList);
            projectList.add(project);
        }


        return projectList;
    }

    private Task taskCreator(Node taskNode){
        Element taskElement = (Element) taskNode;

        int id = Integer.parseInt(taskElement.getElementsByTagName("TaskID").item(0).getTextContent());;
        String description = taskElement.getElementsByTagName("Description").item(0).getTextContent();
        int duration = Integer.parseInt(taskElement.getElementsByTagName("Duration").item(0).getTextContent());
        List<Integer> dependencies = new ArrayList<>();

        NodeList dependList = taskElement.getElementsByTagName("Dependencies").item(0).getChildNodes();
        for(int i = 0; i < dependList.getLength(); i++){
            if(!dependList.item(i).getNodeName().equals("DependsOnTaskID")) continue;
            dependencies.add(Integer.parseInt((dependList.item(i)).getTextContent()));
        }

        return new Task(id, description, duration, dependencies);
    }
}
