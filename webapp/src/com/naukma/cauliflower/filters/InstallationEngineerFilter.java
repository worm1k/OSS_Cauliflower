package com.naukma.cauliflower.filters;

import com.naukma.cauliflower.dao.UserRole;
import com.naukma.cauliflower.entities.User;
import com.naukma.cauliflower.info.CauliflowerInfo;
import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**     InstallationEngineerFilter checks if the user has permission to see ie_dashboard.jsp.
 *   Otherwise user will be redirected to   home.jsp
 * Created by ihor on 11.12.2014.
 */
@WebFilter(filterName = "InstallationEngineerFilter", urlPatterns = "/ie_dashboard.jsp")
public class InstallationEngineerFilter extends BaseFilter {
    private static final Logger logger = Logger.getLogger(InstallationEngineerFilter.class);

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException {
        User user = (User) request.getSession().getAttribute(CauliflowerInfo.USER_ATTRIBUTE);
        if (user == null || (user != null && !user.getUserRole().equals(UserRole.INSTALLATION_ENG.toString()))) {
            try {
                logger.info("InstallationEngineerFilter :: user == null send redirect to home jsp ");
                response.sendRedirect(CauliflowerInfo.HOME_LINK);
            } catch (IOException e) {
                logger.error(e);
            }
        } else {
            try {
                logger.info("InstallationEngineerFilter ::  user has permission to /ie_dashboard.jsp ");
                chain.doFilter(request, response);
            } catch (IOException e) {
                logger.error(e);
            }
        }
    }
}
