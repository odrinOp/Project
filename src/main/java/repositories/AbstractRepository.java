package repositories;

import exceptions.ValidationException;
import domain.Entity;
import org.w3c.dom.*;
import validators.Validator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.LinkedList;

public abstract class AbstractRepository<ID,E extends Entity<ID>> implements CrudRepository<ID,E> {

    protected LinkedList<E> list;
    protected Validator validator;


    public AbstractRepository(Validator validator) {
        list = new LinkedList<>();
        this.validator = validator;
    }


    @Override
    public E findOne(ID id) {
        if(id == null)
            throw new IllegalArgumentException("The id is null!");

        for(E elem:list){
            if(elem.getId().equals( id))
                return elem;
        }
        return null;
    }

    @Override
    public Iterable<E> findAll() {
        return list;
    }

    @Override
    public E save(E entity) throws ValidationException {
        if(entity == null)
            throw new IllegalArgumentException("The entity is null!");

        validator.validate(entity);
        if(list.contains(entity))
            return entity;

        list.add(entity);
        return null;

    }

    @Override
    public E delete(ID id) {
        if(id == null)
            throw new IllegalArgumentException("The id is null!");

        E removedElem = findOne(id);
        if(removedElem == null)
            return null;

        list.remove(removedElem);
        return removedElem;
    }

    @Override
    public E update(E entity) throws ValidationException {

        if(entity == null)
            throw new IllegalArgumentException("The entity is null!");

        validator.validate(entity);
        for(int i = 0; i< list.size(); i++){
            if(list.get(i).getId().equals(entity.getId())) {
                list.set(i, entity);
                return null;
            }
        }


        return entity;
    }


    public abstract String entityToString(E e);
    public abstract E stringToEntity(String[] data);

    public abstract E elementToEntity(Element e);
    public abstract Element[] entityToListOfElements(E e, Document doc);

    /**
     *
     * @param file - the location where the data is stored
     * @throws IOException
     */
    public void readFromFile(String file) throws IOException{
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = reader.readLine();
        while(line!=null){
            String[] data = line.split("/");
            E entity = stringToEntity(data);
            list.add(entity);
            line = reader.readLine();
        }

        reader.close();
    }


    /**
     *
     * @param file-the file location where we want to store data.
     * @throws IOException
     */
    public void writeToFile(String file) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

        String dataToWrite = "";
        for(E e:list){

            dataToWrite += entityToString(e) + "\n";
        }

        writer.write(dataToWrite);
        writer.close();
    }


    public int size(){
        return list.size();
    }

    public void clearAll(){
        list.clear();
    }


    /**
     *
     * @param filename - the file location for our data
     * @param nodeName - the name of the main node
     */
    public void readFromXmlFile(String filename, String nodeName){
        try {
            File fXmlFile = new File(filename);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName(nodeName);
            for(int temp = 0; temp< nodeList.getLength(); temp++){
                Node n = nodeList.item(temp);
                if(n.getNodeType() == Node.ELEMENT_NODE){
                    Element e = (Element)n;
                    list.add(elementToEntity(e));
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void writeToXmlFile(String filename,String nodeName,String nodeName2){

        try{
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            Element root = doc.createElement(nodeName);
            doc.appendChild(root);


            for(E e:list){
                //append the id of the entity
                Element entity = doc.createElement(nodeName2);
                root.appendChild(entity);
                Attr attr = doc.createAttribute("id");
                attr.setValue(e.getId().toString());
                entity.setAttributeNode(attr);

                Element[] args = entityToListOfElements(e,doc);
                for(int temp = 0; temp < args.length; temp++){
                    entity.appendChild(args[temp]);
                }

            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(doc);
            StreamResult streamResult = new StreamResult(new File(filename));

            transformer.transform(domSource, streamResult);


        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
