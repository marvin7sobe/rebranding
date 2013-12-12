package com.cdl.rebranding.api;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.Properties;

import static com.cdl.rebranding.api.Utils.*;

public class XMLFileRebrandingWorker implements Runnable {
    private File file;
    private String from;
    private String to;
    private Properties props;
    boolean wasDocumentRebranded = false;

    public XMLFileRebrandingWorker(File file, Properties props) {
        this.file = file;
        this.props = props;
        this.from = props.getProperty(PROP_REBRANDING_FROM);
        this.to = props.getProperty(PROP_REBRANDING_TO);
    }

    @Override
    public void run() {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            if (doc.hasChildNodes()) {
                makeRebranding(doc.getChildNodes());
                if (wasDocumentRebranded) {
                    String fileNameToSave = file.getAbsolutePath();
                    makeBackUp();
                    saveRebrandedToFile(doc, fileNameToSave);
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR happened during rebranding in file: " + file.getName());
        }
    }

    private void makeRebranding(NodeList nodeList) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            switch (node.getNodeType()) {
                case Node.TEXT_NODE:
                    makeRebrandingInNodeAndUpdateRebrandingStatus(node);
                    break;
                case Node.ELEMENT_NODE:
                    if (node.hasAttributes()) {
                        NamedNodeMap nodeMap = node.getAttributes();
                        for (int j = 0; j < nodeMap.getLength(); j++) {
                            makeRebrandingInNodeAndUpdateRebrandingStatus(nodeMap.item(j));
                        }
                    }
                    if (node.hasChildNodes()) {
                        makeRebranding(node.getChildNodes());
                    }
                    break;
            }
        }
    }

    private void makeRebrandingInNodeAndUpdateRebrandingStatus(Node node) {
        boolean wasRebranded = false;
        String nodeText = node.getTextContent();
        if (nodeText != null && nodeText.length() >= to.length()) {
            String nodeTextRebranded = makeReplacement(nodeText, from, to);
            if (!nodeText.equals(nodeTextRebranded)) {
                node.setTextContent(nodeTextRebranded);
                wasRebranded = true;
            }
        }
        if (wasDocumentRebranded == false && wasRebranded == true) {
            wasDocumentRebranded = true;
        }
    }

    private void makeBackUp() {
        String directory = file.getParent() + "/";
        String fileNameWithoutExtension = file.getName().split("\\.xml$")[0];
        new File(directory + fileNameWithoutExtension + Utils.BAKUP_EXTENSION).delete();
        file.renameTo(new File(directory + fileNameWithoutExtension + Utils.BAKUP_EXTENSION));
    }

    private void saveRebrandedToFile(Document doc, String absoluteFileNamePath) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(absoluteFileNamePath));
        transformer.transform(source, result);
    }
}
