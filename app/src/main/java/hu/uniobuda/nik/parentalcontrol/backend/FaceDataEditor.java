package hu.uniobuda.nik.parentalcontrol.backend;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import hu.uniobuda.nik.parentalcontrol.backend.FaceData;

public class FaceDataEditor {
    public static ArrayList<FaceData> faceData = new ArrayList<>();

    private static int[] loadXMLforIDs(String file) throws ParserConfigurationException, IOException, SAXException {
        ArrayList<Integer> ids = new ArrayList<Integer>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document;
        InputStream stream = new FileInputStream(file);
        document = builder.parse(stream);

        NodeList nodeList = document.getDocumentElement().getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
;
            if (node.getNodeName().equals("labels")) {
                NodeList childNodes = node.getChildNodes();

                for (int j = 0; j < childNodes.getLength(); j++) {
                    if (childNodes.item(j).getNodeName().equals("data")) {
                        String[] l = childNodes.item(j).getTextContent().trim().split(" ");
                        for (String s : l) {
                            ids.add(Integer.parseInt(s));
                        }
                    }
                }
            }
        }

        int[] array = new int[ids.size()];
        for (int i = 0; i < ids.size(); i++) array[i] = ids.get(i);
        return array;
    }

    public static void loadXML(String file) throws ParserConfigurationException, IOException, SAXException {
        int[] ids = loadXMLforIDs(file);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document;
        InputStream stream = new FileInputStream(file);
        document = builder.parse(stream);
        NodeList nodeList = document.getDocumentElement().getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            if (node.getNodeName().equals("histograms")) {
                NodeList childNodes = node.getChildNodes();

                for (int j = 0; j < childNodes.getLength(); j++) {
                    if (childNodes.item(j).getNodeName().equals("_")) {
                        NodeList dataNodes = childNodes.item(j).getChildNodes();
                        FaceData data = new FaceData();

                        for (int h = 0; h < dataNodes.getLength(); h++) {
                            if (dataNodes.item(h).getNodeName().equals("rows")) {
                                data.setRows(dataNodes.item(h).getTextContent());
                            } else if (dataNodes.item(h).getNodeName().equals("cols")) {
                                data.setCols(dataNodes.item(h).getTextContent());
                            } else if (dataNodes.item(h).getNodeName().equals("dt")) {
                                data.setDt(dataNodes.item(h).getTextContent());
                            } else if (dataNodes.item(h).getNodeName().equals("data")) {
                                data.setData(dataNodes.item(h).getTextContent());
                            }
                        }
                        data.setId(ids[faceData.size()]);
                        faceData.add(data);
                    }
                }
            }
        }
    }

    public static void writeXML(String file) throws IOException, InterruptedException {
        File f = new File(file);
        f.getParentFile().mkdirs();

        if (f.exists())
            f.delete();

        f.createNewFile();

        PrintWriter writer = new PrintWriter(file, "UTF-8");
        writer.append("<?xml version=\"1.0\"?>\n");
        writer.append("<opencv_storage>\n");
        writer.append("<radius>1</radius>\n");
        writer.append("<neighbors>8</neighbors>\n");
        writer.append("<grid_x>8</grid_x>\n");
        writer.append("<grid_y>8</grid_y>\n");
        writer.append("<histograms>\n");

        writer.flush();

        String indices = "";
        for (FaceData d : faceData) {
            writer.append("  <_ type_id=\"opencv-matrix\">\n");
            writer.append("    <rows>" + d.getRows() + "</rows>\n");
            writer.append("    <cols>" + d.getCols() + "</cols>\n");
            writer.append("    <dt>" + d.getDt() + "</dt>\n");
            writer.append("    <data>" + d.getData() + "</data>");
            writer.append("  </_>");

            indices += d.getId() + " ";
            writer.flush();
        }
        writer.println("</histograms>");

        writer.append("<labels type_id=\"opencv-matrix\">\n");
        writer.append("  <rows>" + faceData.size() + "</rows>\n");
        writer.append("  <cols>1</cols>\n");
        writer.append("  <dt>i</dt>\n");
        writer.append("  <data>\n");
        writer.append("    " + indices.trim());
        writer.append("  </data>\n");
        writer.append("</labels>\n");

        writer.append("<labelsInfo>\n");
        writer.append("</labelsInfo>\n");
        writer.append("</opencv_storage>\n");
        writer.flush();
        writer.close();
    }
}
