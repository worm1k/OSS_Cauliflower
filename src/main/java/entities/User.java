package entities;

/**
 * Created by Eugene on 26.11.2014.
 */
public class User {

    private  int ID_USER;
    private int ID_USERROLE;
    private String E_MAIL;
    private String PASSWORD;
    private String F_NAME;
    private String L_NAME;
    private String PHONE;

    public User(int id_user, int id_userrole, String e_mail, String password, String f_name, String l_name, String phone) {
        this.ID_USER = id_user;
        this.ID_USERROLE = id_userrole;
        this.E_MAIL = e_mail;
        this.PASSWORD = password ;
        this.F_NAME = f_name;
        this.L_NAME = l_name;
        this.PHONE = phone;
    }

    @Override
    public String toString() {
        return "User{" +
                "ID_USER=" + ID_USER +
                ", ID_USERROLE=" + ID_USERROLE +
                ", E_MAIL='" + E_MAIL + '\'' +
                ", PASSWORD='" + PASSWORD + '\'' +
                ", F_NAME='" + F_NAME + '\'' +
                ", L_NAME='" + L_NAME + '\'' +
                ", PHONE='" + PHONE + '\'' +
                '}';
    }

    public String getEmail() {
        return E_MAIL;
    }

    public String getName() {
        return F_NAME;
    }

    public String getSurname() {
        return L_NAME;
    }
}
