import java.util.ArrayList;

public class Employee {
    public Integer ID;
    public String fullName;
    public String position;
    public String organisation;
    public String mail;
    public ArrayList<String> phone;

    Employee(Integer id, String fn, String pos, String org, String mail, ArrayList<String> ph) {
        ID = id;
        fullName = fn;
        position = pos;
        organisation = org;
        this.mail = mail;
        phone = ph;
    }

    void Print() {
        System.out.printf("ID: %d\nfullName: %s\nposition: %s\norganisation: %s\nE-mail: %s\n", ID, fullName, position, organisation, mail);
        for (int i = 0; i < phone.size(); i++) {
            System.out.printf("phone: %s\n", phone.get(i));
        }
    }
}
