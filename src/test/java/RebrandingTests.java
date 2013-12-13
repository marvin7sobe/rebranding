import static com.cdl.rebranding.api.FilesRebrandingManager.CONF_PROPERTIES_FILE;
import static com.cdl.rebranding.api.XMLFileRebrandingWorker.BAKUP_EXTENSION;
import static com.cdl.rebranding.api.XMLFileRebrandingWorker.PROP_REBRANDING_TO;
import static com.cdl.rebranding.api.XMLFileRebrandingWorker.TITLE_ATTR_NAME;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.cdl.rebranding.api.FilesRebrandingManager;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RebrandingTests {

    private final String TEST_DIR = "test_dir";
    private final String TEST_FILE = "test.xml";

    @Test
    public void testRebranding() throws IOException, URISyntaxException {
        prepareTestData();
        makeRebranding();
        checkRebrandedDoc();
        checkBackupFile();
        deleteDirectory(new File(TEST_DIR));
        System.out.println("Test 'testRebranding' passed");
    }

    private void prepareTestData() throws IOException {
        new File(TEST_DIR).mkdir();
        copyTestFileToTestDir();
    }

    private void copyTestFileToTestDir() throws IOException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(TEST_FILE);
        File testFile = new File(TEST_DIR + "/" + TEST_FILE);
        OutputStream os = new FileOutputStream(testFile);
        try {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            is.close();
            os.close();
        }
        assertTrue(testFile.exists());
    }

    private void makeRebranding() throws IOException {
        FilesRebrandingManager rebrandingManager = new FilesRebrandingManager(new File(TEST_DIR).getAbsolutePath());
        rebrandingManager.startRebranding();
    }

    private void checkRebrandedDoc() throws IOException {
        Properties props = new Properties();
        props.load(new FileInputStream(CONF_PROPERTIES_FILE));
        assertNotNull(props);
        String to = props.getProperty(PROP_REBRANDING_TO);
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new File(TEST_DIR + "/" + TEST_FILE));
            assertDocNodes(doc.getChildNodes(), to);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void assertDocNodes(NodeList nodeList, String to) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            switch (node.getNodeType()) {
                case Node.TEXT_NODE:
                    if (node.getTextContent().length() >= to.length()) {
                        assertTrue(node.getTextContent().contains(to));
                    }
                    break;
                case Node.ELEMENT_NODE:
                    if (node.hasAttributes()) {
                        NamedNodeMap nodeMap = node.getAttributes();
                        for (int j = 0; j < nodeMap.getLength(); j++) {
                            String attrName = nodeMap.item(j).getNodeName();
                            if (attrName.equalsIgnoreCase(TITLE_ATTR_NAME)) {
                                assertTrue(nodeMap.item(j).getTextContent().contains(to));
                            }
                        }
                    }
                    if (node.hasChildNodes()) {
                        assertDocNodes(node.getChildNodes(), to);
                    }
                    break;
            }
        }
    }


    private void checkBackupFile() {
        String backUpFileName = TEST_FILE.split(".xml")[0];
        assertNotNull(backUpFileName);
        File backupFile = new File(TEST_DIR + "/" + backUpFileName + BAKUP_EXTENSION);
        assertTrue(backupFile.exists());
    }

    public void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (null != files) {
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        deleteDirectory(files[i]);
                    } else {
                        files[i].delete();
                    }
                }
            }
        }
        directory.delete();
    }
}
