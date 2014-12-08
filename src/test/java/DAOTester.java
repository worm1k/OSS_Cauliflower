import com.naukma.cauliflower.dao.DAO;
import com.naukma.cauliflower.dao.Scenario;
import com.naukma.cauliflower.dao.UserRoles;
import com.naukma.cauliflower.entities.ServiceLocation;
import com.naukma.cauliflower.entities.ServiceOrder;
import com.naukma.cauliflower.entities.User;
import oracle.jdbc.pool.OracleConnectionPoolDataSource;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.JUnitCore;

import javax.naming.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.sql.SQLException;
import java.util.List;


/**
 * Created by Max on 07.12.2014.
 */

public class DAOTester {

    @BeforeClass
    public static void setUpClass() throws Exception {
        try {
            // Create initial context
            System.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                    "org.apache.naming.java.javaURLContextFactory");
            System.setProperty(Context.URL_PKG_PREFIXES,
                    "org.apache.naming");
            InitialContext ic = new InitialContext();

            ic.createSubcontext("jdbc");
            OracleConnectionPoolDataSource ds = new OracleConnectionPoolDataSource();
            // Construct DataSource

            ds.setURL("jdbc:oracle:thin:@localhost:1521/xe");
            ds.setUser("CAULIFLOWER");
            ds.setPassword("11111");

            ic.bind("jdbc/oraclesource", ds);
        } catch (NamingException ex) {
            System.out.println("Failed to start junit testing");
            ex.printStackTrace();
        }
    }

  //  private int userIdTest = 0;

    @Test
    public void testUser() throws SQLException {
        DAO dao = DAO.INSTANCE;

        UserRoles role = UserRoles.ADMINISTRATOR;
        int idStub = -1;
        int userRoleId = 1;
        String email = "aaaaaaafdgdfh";
        String firstName = "FirstName";
        String lastName = "LastName";
        String phoneNumber = "12345";
        boolean isBlocked = false;
        String password = "1234567891sfsdg";
        String newPassword = "12345"; //will be used for change password method

        User user = new User(idStub, userRoleId, role.toString(), email, firstName, lastName, phoneNumber, isBlocked);

        int userId = dao.createUser(user,password);
        assert(dao.checkForExistingUserById(userId));
        assert(dao.checkForExistingUserByEmail(email));


        User userRes = dao.getUserByLoginAndPassword(email,password);
        assert(userId == userRes.getUserId());
        assert(userRes.getUserRoleId() == dao.getUserRoleIdFor(role));
        assert(userRes.getUserRole().equals(role.toString()));
        assert(dao.getUserRoleNameByUserRoleId(userRoleId).equals(role.toString()));
        assert(userRes.getEmail().equals(email));
        assert(userRes.getFirstName().equals(firstName));
        assert(userRes.getLastName().equals(lastName));
        assert(userRes.getPhone().equals(phoneNumber));
        assert(userRes.isBlocked() == isBlocked);

        userRes = dao.blockUserById(userId);
        assert(userRes.isBlocked() == true);

        userRes = dao.changeUserPasswordById(userId, newPassword);
        assert(userRes.equals(dao.getUserByLoginAndPassword(email, newPassword)));
    }


//    @Test
//    public void testOrder() throws SQLException{
//      int orderId = DAO.INSTANCE.createServiceOrder(userIdTest, Scenario.NEW,null);
//      List<ServiceOrder>res = DAO.INSTANCE.getOrders(userIdTest);
//        res.
//
//    }

}
