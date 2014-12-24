package com.naukma.cauliflower.filters;

import com.naukma.cauliflower.dao.UserRole;
import com.naukma.cauliflower.entities.User;
import com.naukma.cauliflower.info.CauliflowerInfo;
import org.apache.log4j.Logger;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * CustomerSupportEngineerFilter checks if the user has permission to see cse_dashboard.jsp or cse_dashboard_user.jsp
 * Otherwise user will be redirected to   home.jsp
 * Created by ihor on 12.12.2014.
 */
public class CustomerSupportEngineerFilter extends BaseFilter {
    private static final Logger logger = Logger.getLogger(InstallationEngineerFilter.class);

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException {
        User user = (User) request.getSession().getAttribute(CauliflowerInfo.USER_ATTRIBUTE);
        if (user == null || (user != null && !user.getUserRole().equals(UserRole.CUST_SUP_ENG.toString()))) {
            try {
                logger.info("CustomerSupportEngineerFilter ::  user has not permission to see customer support engineer page");
                response.sendRedirect(CauliflowerInfo.HOME_LINK);
            } catch (IOException e) {
                logger.error(e);
            }
        } else {
            try {
                logger.info("CustomerSupportEngineerFilter ::  user has  permission to see customer support engineer page");
                chain.doFilter(request, response);
            } catch (IOException e) {
                logger.error(e);
            }
        }
    }
}
