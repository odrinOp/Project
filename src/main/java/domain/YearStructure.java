package domain;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;

public class YearStructure extends Entity<String> {

    private SemesterStructure sem1;
    private SemesterStructure sem2;

    public YearStructure(String id) {
        this.setId(id);

        /*
        //first semester dates
        LocalDate start1 = LocalDate.of(2019,9,30);
        LocalDate end1 = LocalDate.of(2020,1,19);

        LocalDate startHoliday1 = LocalDate.of(2019,12,23);
        LocalDate endHoliday1 = LocalDate.of(2020,1,5);

        //second semester dates;
        LocalDate start2 = LocalDate.of(2020,2,24);
        LocalDate end2 = LocalDate.of(2020,6,7);

        LocalDate startHoliday2 = LocalDate.of(2020,4,20);
        LocalDate endHoliday2 = LocalDate.of(2020,4,26);


        sem1 = new SemesterStructure(start1,end1,startHoliday1,endHoliday1);
        sem2 = new SemesterStructure(start2,end2,startHoliday2,endHoliday2);

        */
        readFromXmlFile();

    }


    public void readFromFile() throws IOException {
        String file = "src/sample/files/structAnUniversitar.csv";
        BufferedReader reader = new BufferedReader(new FileReader(file));
        LocalDate[] allDates = new LocalDate[9];
        String date = "";
        date = reader.readLine();
        int i = 0;
        while (date != null) {

            String[] dates = date.split("-");
            if (dates.length == 3) {
                LocalDate current = LocalDate.of(Integer.valueOf(dates[0]), Integer.valueOf(dates[1]), Integer.valueOf(dates[2]));
                allDates[i++] = current;
            }
            date = reader.readLine();
        }

        sem1 = new SemesterStructure(allDates[0], allDates[1], allDates[2], allDates[3]);
        sem2 = new SemesterStructure(allDates[4], allDates[5], allDates[6], allDates[7]);


        reader.close();

        sem1.initialize();
        sem2.initialize();

        writeToXmlFile();

    }

    public SemesterStructure getSem1() {
        return sem1;
    }

    public void setSem1(SemesterStructure sem1) {
        this.sem1 = sem1;
    }

    public SemesterStructure getSem2() {
        return sem2;
    }

    public void setSem2(SemesterStructure sem2) {
        this.sem2 = sem2;
    }


    public int getSemesterByDate(LocalDate now) {
        int current = sem1.getWeekNumber(now);
        if (current != -1)
            return 1;
        return 2;
    }


    public int getWeekByDate(LocalDate now) {
        int curent = sem1.getWeekNumber(now);
        if (curent != -1) {
            return curent;
        }
        return sem2.getWeekNumber(now);
    }

    public LocalDate getDateByWeek(int week, int semester) {
        if (week < 1 || week > 14)
            return null;

        if (semester < 1 || semester > 2)
            return null;


        if (semester == 1)
            return sem1.getWeekDates().get(week - 1).getStartDate();

        return sem2.getWeekDates().get(week - 1).getStartDate();
    }

    @Override
    public String toString() {
        return sem1 + "||" + sem2;
    }


    public void writeToXmlFile() {
        try {
            String filename = "src/sample/files/xmlFiles/structAnUniversitar.xml";
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            Element root = doc.createElement("structAnUniveristar");
            doc.appendChild(root);

            Element sem1 = doc.createElement("structSem1");
            root.appendChild(sem1);

            Element[] args = this.sem1.toElements(doc);
            for (int i = 0; i < args.length; i++)
                sem1.appendChild(args[i]);

            Element sem2 = doc.createElement("structSem2");
            root.appendChild(sem2);

            args = this.sem2.toElements(doc);
            for (int i = 0; i < args.length; i++)
                sem2.appendChild(args[i]);


            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(doc);
            StreamResult streamResult = new StreamResult(new File(filename));

            transformer.transform(domSource, streamResult);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readFromXmlFile() {
        try {
            String filename = "src/main/java/files/xmlFiles/structAnUniversitar.xml";
            File fXmlFile = new File(filename);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("structSem1");
            for (int temp = 0; temp < nodeList.getLength(); temp++) {
                Node n = nodeList.item(temp);
                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    Element e = (Element) n;
                    String startDate = e.getElementsByTagName("startDate").item(0).getTextContent();
                    String endDate = e.getElementsByTagName("endDate").item(0).getTextContent();
                    String startHolidayDate = e.getElementsByTagName("startHolidayDate").item(0).getTextContent();
                    String endHolidayDate = e.getElementsByTagName("endDate").item(0).getTextContent();

                    sem1 = new SemesterStructure(stringToLocalDate(startDate), stringToLocalDate(endDate), stringToLocalDate(startHolidayDate), stringToLocalDate(endHolidayDate));
                }
            }

            nodeList = doc.getElementsByTagName("structSem2");
            for (int temp = 0; temp < nodeList.getLength(); temp++) {
                Node n = nodeList.item(temp);
                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    Element e = (Element) n;
                    String startDate = e.getElementsByTagName("startDate").item(0).getTextContent();
                    String endDate = e.getElementsByTagName("endDate").item(0).getTextContent();
                    String startHolidayDate = e.getElementsByTagName("startHolidayDate").item(0).getTextContent();
                    String endHolidayDate = e.getElementsByTagName("endDate").item(0).getTextContent();

                    sem2 = new SemesterStructure(stringToLocalDate(startDate), stringToLocalDate(endDate), stringToLocalDate(startHolidayDate), stringToLocalDate(endHolidayDate));
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        sem1.initialize();
        sem2.initialize();
    }

    private LocalDate stringToLocalDate(String s){
        String date[] = s.split("-");
        if(date.length!=3)
            return null;

        LocalDate date1 = LocalDate.of(Integer.parseInt(date[0]),Integer.parseInt(date[1]),Integer.parseInt(date[2]));
        return date1;
    }
}