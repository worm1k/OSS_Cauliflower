import com.naukma.cauliflower.dao.DAO;
import com.naukma.cauliflower.dao.UserRoles;
import com.naukma.cauliflower.entities.User;
import org.junit.Test;

import java.sql.SQLException;

/**
 * Created by Max on 07.12.2014.
 */

public class UserTest {

    @Test
    public void testUser() throws SQLException {
        DAO dao = DAO.INSTANCE;
        User user = new User(1,1, UserRoles.ADMINISTRATOR.toString(),"aaa","aaa","aaa","123",false);
        String password = "12345678";
        int userId = dao.createUser(user,password);
        User userRes = dao.getUserByLoginAndPassword(user.getEmail(),password);

        assert (user.getEmail().equals(userRes.getEmail()));
        assert (user.getUserId() == userRes.getUserId());

    }
}
