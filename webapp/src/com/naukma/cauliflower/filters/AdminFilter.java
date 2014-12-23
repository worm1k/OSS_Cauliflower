package com.naukma.cauliflower.filters;

import com.naukma.cauliflower.dao.UserRole;
import com.naukma.cauliflower.entities.User;
import com.naukma.cauliflower.info.CauliflowerInfo;
import org.apache.log4j.Logger;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**  AdminFilter checks if the user has permission to see admdashboard.jsp.
 *   Otherwise user will be redirected to   home.jsp
 * Created by ihor on 11.12.2014.
 */
@WebFilter(filterName = "AdminFilter", urlPatterns = "/admdashboard.jsp")
public class AdminFilter extends BaseFilter {
    private static final Logger logger = Logger.getLogger(AdminFilter.class);

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException {
        User user = (User) request.getSession().getAttribute(CauliflowerInfo.USER_ATTRIBUTE);
        if (user == null || (user != null && !user.getUserRole().equals(UserRole.ADMINISTRATOR.toString()))) {
            try {
                logger.info("AdminFilter ::  user has not permission to see /admdashboard.jsp ");
                response.sendRedirect(CauliflowerInfo.HOME_LINK);
            } catch (IOException e) {
                logger.error(e);
            }
        } else {
            try {
                chain.doFilter(request, response);
                logger.info("AdminFilter ::  user has permission to /admdashboard.jsp ");
            } catch (IOException e) {
                logger.error(e);
            }
        }
    }
}
