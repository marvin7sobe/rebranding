package com.cdl.rebranding;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class FileRebrandingWorker implements Runnable {
    private File file;

    public FileRebrandingWorker(File file) {
        //todo get parameters as well
        this.file = file;
    }

    @Override
    public void run() {
        String lowerFileName = file.getName().toLowerCase();
        if(lowerFileName.endsWith("xml")){
            parseFile();
        }else if(lowerFileName.endsWith("xsl")){

        }else {
            //todo throw not supported file type exception
        }
    }

    private void parseFile() {
        try {
           DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            if (doc.hasChildNodes()) {
                printNodes(doc.getChildNodes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printNodes(NodeList nodeList) {

        for (int count = 0; count < nodeList.getLength(); count++) {
            Node tempNode = nodeList.item(count);
            // make sure it's element node.
            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
                // get node name and value
                System.out.println("\nNode Name =" + tempNode.getNodeName() + " [OPEN]");
                System.out.println("Node Value =" + tempNode.getTextContent());
                if (tempNode.hasAttributes()) {
                    // get attributes names and values
                    NamedNodeMap nodeMap = tempNode.getAttributes();
                    for (int i = 0; i < nodeMap.getLength(); i++) {
                        Node node = nodeMap.item(i);
                        System.out.println("attr name : " + node.getNodeName());
                        System.out.println("attr value : " + node.getNodeValue());
                    }
                }
                if (tempNode.hasChildNodes()) {
                    // loop again if has child nodes
                    printNodes(tempNode.getChildNodes());
                }
                System.out.println("Node Name =" + tempNode.getNodeName() + " [CLOSE]");
            }
        }
    }
}
