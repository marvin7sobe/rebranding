package com.cdl.rebranding;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class XMLFileRebrandingWorker extends Thread {
    private File file;
    private String from;
    private String to;

    public XMLFileRebrandingWorker(File file) {
        //todo get parameters as well and from and to from them
        this.file = file;
        this.from = "Tridion";
        this.to = "Tridion1";
    }

    @Override
    public void run() {
        parseFile();
    }

    private void parseFile() {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            if (doc.hasChildNodes()) {
                boolean wasAnyRebranding = makeRebranding(doc.getChildNodes());
                if (wasAnyRebranding) {
                    saveRebrandedToFile(doc);
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR happened during processing xml file: " + file.getName());
        }
    }

    private void saveRebrandedToFile(Document doc) throws TransformerException {
        String directory = file.getParent() + "/";
        String fileNameWithoutExtension = file.getName().split("\\.xml$")[0];
        new File(directory+ fileNameWithoutExtension + ".bak").delete();
        file.renameTo(new File(directory+ fileNameWithoutExtension + ".bak"));

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(directory+ fileNameWithoutExtension + ".xml"));
        transformer.transform(source, result);
    }

    private boolean makeRebranding(NodeList nodeList) {
        boolean wasAnyRebranding = false;
        for (int count = 0; count < nodeList.getLength(); count++) {
            Node node = nodeList.item(count);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                String nodeText = node.getTextContent();
                if (nodeText.contains(from)) {
                    node.setTextContent(makeRebrandingInString(nodeText, from, to));
                    wasAnyRebranding = true;
                }
                if (node.hasAttributes()) {
                    NamedNodeMap nodeMap = node.getAttributes();
                    for (int i = 0; i < nodeMap.getLength(); i++) {
                        Node attribute = nodeMap.item(i);
                        if (attribute.getNodeValue().contains(from)) {
                            attribute.setNodeValue(makeRebrandingInString(attribute.getNodeValue(), from, to));
                            wasAnyRebranding = true;
                        }

                    }
                }
                if (node.hasChildNodes()) {
                    boolean wasAnyRebrandingInChild = makeRebranding(node.getChildNodes());
                    if (wasAnyRebranding == false) {
                        wasAnyRebranding = wasAnyRebrandingInChild;
                    }
                }
            }
        }
        return wasAnyRebranding;
    }

    private String makeRebrandingInString(String string, String from, String to) {
        //todo more clear algorith to not replace not needed
        //todo move to another place
        return string.replace(from, to);
    }
}
