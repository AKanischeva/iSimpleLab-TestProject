/*
Программа предназначена для хранения, просмотра и редактирования данных по клиентам.
Использовать консольный ввод/вывод.

Информация по клиенту:
    - Уникальный код клиента в органайзере;
    - Ф.И.О.;
    - должность;
    - наименование организации;
    - e-mail;
    - телефон (список).

Команды:
    help - вывод справки по командам органайзера;
    insert     - добавить нового клиента;
    update  - редактировать;
    delete - удалить;
    list fieldName1;fieldName2;....;fieldNameN - вывести список клиентов. поля, по которым идет сортировка, не обязательно для ввода;
    find - поиск клиента до первого совпадения с любым из полей.
* */

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main {

    private static final String FILENAME = "staff.xml";

    /**
     * Recieve XML file and makes list to print or to sort
     * @return
     * @throws JDOMException
     * @throws IOException
     */
    public static ArrayList<Employee> fromXMLToList() throws JDOMException, IOException {
        Document document = null;
        Element root = null;
        Integer newId = 0;
        File xmlFile = new File(FILENAME);
        if (xmlFile.exists()) {                                 //checking existance of file
            FileInputStream fis = new FileInputStream(xmlFile);
            SAXBuilder sb = new SAXBuilder();
            document = sb.build(fis);
            root = document.getRootElement();
            fis.close();
        }
        int id;
        String fullName;
        String position;
        String organisation;
        String mail;
        ArrayList<Employee> employee = new ArrayList<>();
        List<Element> staffList = root.getChildren("staff");
        for (Element nextStaff : staffList) {
            ArrayList<String> phone = new ArrayList<String>();
            id = Integer.parseInt(nextStaff.getAttributeValue("id"));
            fullName = nextStaff.getChildText("fullName");
            position = nextStaff.getChildText("position");
            organisation = nextStaff.getChildText("organisation");
            mail = nextStaff.getChildText("E-mail");
            List<Element> l = nextStaff.getChildren("phone");
            for (Element nextPhone : l) {
                phone.add(nextPhone.getValue());
            }
            Employee data = new Employee(id, fullName, position, organisation, mail, phone);
            employee.add(data);
        }
        return employee;
    }

    /**
     * Function for sorting list the way we need. For command list param1 param2 ... paramN
     * @param persons
     * @param splittedString
     */
    private static void order(List<Employee> persons, String[] splittedString) {

        Collections.sort(persons, new Comparator() {

                    public Integer compare1(Employee data1, Employee data2) {
                        try {
                            for (int i = 1; i < splittedString.length; i++) {
                                Field field = data1.getClass().getField(splittedString[i]);

                                Comparable id1 = (Comparable) field.get(data1);
                                Comparable id2 = (Comparable) field.get(data2);
                                int c = id1.compareTo(id2);
                                if (c == 0)     //If fields are equal, compare next param
                                    continue;
                                else
                                    return c;
                            }
                        } catch (Exception ex) {
                            return -2;
                        }
                        return 0;
                    }

                    public int compare(Object o1, Object o2) {

                        return compare1((Employee) o1, (Employee) o2);
                    }
                }
        );
    }

    /**
     * Function for inserting new employees in existing XML file or in new file
     * @param org
     * @return
     * @throws JDOMException
     * @throws IOException
     */
    public static Integer Writer(Employee org) throws JDOMException, IOException {
        Document document = null;
        Element root = null;
        Integer newId = 0;
        File xmlFile = new File(FILENAME);
        if (xmlFile.exists()) {
            FileInputStream fis = new FileInputStream(xmlFile);
            SAXBuilder sb = new SAXBuilder();
            document = sb.build(fis);
            root = document.getRootElement();
            fis.close();
        } else {
            document = new Document();
            root = new Element("Organiser");
        }

        String fn = "fullName";
        String pos = "position";
        String organ = "organisation";
        String mail = "E-mail";
        String ph = "phone";
        Element child = new Element("staff");
        child.setAttribute("id", String.valueOf(org.ID));
        child.addContent(new Element(fn).setText(org.fullName));
        child.addContent(new Element(pos).setText(org.position));
        child.addContent(new Element(organ).setText(org.organisation));
        child.addContent(new Element(mail).setText(org.mail));
        for (String l : org.phone) {
            child.addContent(new Element(ph).setText(l));
        }
        root.addContent(child);
        document.setContent(root);
        List<Element> al = root.getChildren("staff");
        Element a = al.get(al.size() - 1);
        newId = Integer.parseInt(a.getAttributeValue("id"));
        try {
            FileWriter writer = new FileWriter(FILENAME);
            XMLOutputter outputter = new XMLOutputter();
            outputter.setFormat(Format.getPrettyFormat());
            outputter.output(document, writer);
            //outputter.output(document, System.out);
            writer.close(); // close writer
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newId;
    }

    /**
     * Function for updating information
     * @param id
     * @param field
     * @param newInformation
     */
    public static void update(Integer id, String field, String newInformation) {
        SAXBuilder saxBuilder = new SAXBuilder();
        File xmlFile = new File(System.getProperty("user.dir")
                + File.separator + FILENAME);
        try {
            Document doc = saxBuilder.build(xmlFile);
            Element rootNode = doc.getRootElement();
            List<Element> staffList = rootNode.getChildren("staff");
            for (Element nextStaff : staffList) {
                if (nextStaff.getAttributeValue("id").compareToIgnoreCase(String.valueOf(id)) == 0) {
                    if (field.compareToIgnoreCase("id") == 0)
                        nextStaff.setAttribute("id", newInformation);
                    else
                        nextStaff.getChild(field).setText(newInformation);
                }
            }

            XMLOutputter xmlOutputter = new XMLOutputter();
            xmlOutputter.setFormat(Format.getPrettyFormat());
            try {
                xmlOutputter.output(doc, new FileOutputStream(
                        System.getProperty("user.dir") + File.separator
                                + FILENAME));
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName())
                        .log(Level.SEVERE, null, ex);
            }


        } catch (JDOMException | IOException ex) {
            Logger.getLogger(Main.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Deleting employees
     * @param idToDelete
     */
    public static void delete(Integer idToDelete) {
        SAXBuilder saxBuilder = new SAXBuilder();
        File xmlFile = new File(System.getProperty("user.dir")
                + File.separator + FILENAME);
        try {
            Document doc = saxBuilder.build(xmlFile);
            Element rootNode = doc.getRootElement();
            List<Element> staffList = rootNode.getChildren("staff");
            for (Element nextStaff : staffList) {
                if (nextStaff.getAttributeValue("id").compareToIgnoreCase(String.valueOf(idToDelete)) == 0) {
                    nextStaff.getParent().removeContent(nextStaff);
                    break;
                }
            }

            XMLOutputter xmlOutputter = new XMLOutputter();
            xmlOutputter.setFormat(Format.getPrettyFormat());
            try {
                xmlOutputter.output(doc, new FileOutputStream(
                        System.getProperty("user.dir") + File.separator
                                + FILENAME));
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName())
                        .log(Level.SEVERE, null, ex);
            }


        } catch (JDOMException | IOException ex) {
            Logger.getLogger(Main.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Fiinding employees
     * @param informationToFind
     */
    public static void find(String informationToFind) {
        SAXBuilder saxBuilder = new SAXBuilder();
        File xmlFile = new File(System.getProperty("user.dir")
                + File.separator + FILENAME);
        try {
            Document doc = saxBuilder.build(xmlFile);
            Element rootNode = doc.getRootElement();
            List<Element> staffList = rootNode.getChildren("staff");
            for (Element nextStaff : staffList) {
                List<Element> list = nextStaff.getChildren();
                for (Element nextChild : list) {
                    if (nextStaff.getChildText(nextChild.getName()).compareToIgnoreCase(informationToFind) == 0 ||
                            nextStaff.getAttributeValue("id").compareToIgnoreCase(informationToFind) == 0) {
                        System.out.println("ID: "
                                + nextStaff.getAttributeValue("id"));
                        System.out.println("Full Name: "
                                + nextStaff.getChildText("fullName"));
                        System.out.println("position: "
                                + nextStaff.getChildText("position"));
                        System.out.println("organisation: "
                                + nextStaff.getChildText("organisation"));
                        System.out.println("E-mail: "
                                + nextStaff.getChildText("E-mail"));

                        List<Element> l = nextStaff.getChildren("phone");
                        for (Element nextPhone : l) {
                            System.out.println("phone: "
                                    + nextPhone.getValue());
                        }
                        return;
                    }

                }
            }
        } catch (JDOMException | IOException ex) {
            Logger.getLogger(Main.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Testing incoming strings for being phone number
     * @param testString
     * @return
     */
    public static boolean testPhone(String testString) {
        Pattern p = Pattern.compile("[+]7[0-9]{10}$");
        Matcher m = p.matcher(testString);
        return m.matches();
    }

    /**
     * Checking E-mails for it's being correct
     * @param testString
     * @return
     */
    public static boolean testMail(String testString){
        Pattern p = Pattern.compile("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$");
        Matcher m = p.matcher(testString);
        return m.matches();
    }

    public static void main(String[] args) throws JDOMException, IOException {
        Integer id = 1;
        String fullName = null;
        String position = null;
        String organisation = null;
        String mail = null;
        Scanner in = new Scanner(System.in);
        ArrayList<Employee> list = new ArrayList<Employee>();
        ArrayList<String> ph = new ArrayList<String>();
        Stack<Integer> deletedId;
        deletedId = new Stack<Integer>();
        String input;
        System.out.println("Введите команду");
        input = in.nextLine();
        Element company = new Element("Organiser");
        Document document = new Document(company);
        String[] incomingString = input.split(" ");
        String forTests;
        while (!incomingString[0].equals("exit")) {
            switch (incomingString[0]) {
                case ("insert"):
                    System.out.println("Введите ФИО\n");
                    fullName = in.nextLine();
                    System.out.println("Введите должность\n");
                    position = in.nextLine();
                    System.out.println("Введите название организации\n");
                    organisation = in.nextLine();
                    System.out.println("Введите e-mail\n");
                    forTests = in.nextLine();
                    while (!testMail(forTests)) {
                        System.out.println("Ошибка ввода. Повторите");
                        forTests = in.nextLine();
                    }
                    mail = forTests;
                    System.out.println("Введите кол-во номеров");
                    Integer number = in.nextInt();
                    in.nextLine();
                    ph.clear();
                    System.out.println("Введите номера телефонов в формате +79991234567");
                    for (int i = 0; i < number; i++) {
                        forTests = in.nextLine();
                        while (!testPhone(forTests)) {
                            System.out.println("Ошибка ввода. Повторите");
                            forTests = in.nextLine();
                        }
                        ph.add(forTests);
                    }

                    if (!deletedId.isEmpty()) {
                        id = deletedId.pop();
                    }
                    Employee data = new Employee(id, fullName, position, organisation, mail, ph);
                    id = Writer(data);
                    //list.add(data);
                    ph.clear();
                    id++;
                    break;
                case ("help"):
                    System.out.printf("help - вывод справки по командам органайзера;\n" +
                            "insert     - добавить нового клиента;\n" +
                            "update  - редактировать;\n" +
                            "delete - удалить;\n" +
                            "list fieldName1 fieldName2 ... fieldNameN - вывести список клиентов. поля, по которым идет сортировка, не обязательно для ввода;\n" +
                            "find - поиск клиента до первого совпадения с любым из полей.\n");
                    break;
                case ("update"):
                    System.out.println("Введите id пользователя");
                    Integer incomingID = in.nextInt();
                    in.nextLine();
                    System.out.println("Введите поле, которое хотите изменить");
                    String field = in.nextLine();
                    System.out.println("Введите новую информацию");
                    String newInformation = in.nextLine();
                    if(field.equals("phone"))
                        while (!testPhone(newInformation))
                        {
                            System.out.println("Неверный формат телефона (+79991234567)");
                            newInformation = in.nextLine();
                        }
                    if(field.equals("E-mail"))
                        while (!testMail(newInformation))
                        {
                            System.out.println("Неверный формат почты");
                            newInformation = in.nextLine();
                        }
                    update(incomingID, field, newInformation);
                    break;
                case ("delete"):
                    System.out.println("Введите id пользователя");
                    Integer idToDelete = in.nextInt();
                    in.nextLine();
                    delete(idToDelete);
                    deletedId.push(idToDelete);
                    break;
                case ("list"):
                    list = fromXMLToList();
                    order(list, incomingString);
                    for (Employee el : list) {
                        el.Print();
                    }
                    break;
                case ("find"):
                    System.out.println("Введите информацию, которую надо найти");
                    String informationToFind = in.nextLine();
                    find(informationToFind);
                    break;
                default:
                    System.out.println("Ошибка ввода");
                    break;

            }
            System.out.println("Введите команду");
            input = in.nextLine();
            incomingString = input.split(" ");
        }

    }

}
