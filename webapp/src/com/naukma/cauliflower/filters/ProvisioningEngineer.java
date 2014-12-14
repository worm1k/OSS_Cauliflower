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

/**
 * Created by ihor on 11.12.2014.
 */
@WebFilter(filterName = "ProvisioningEngineer",urlPatterns = "/pe_dashboard.jsp")
public class ProvisioningEngineer extends BaseFilter {
    private static final Logger logger = Logger.getLogger(ProvisioningEngineer.class);
    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
        User user = (User) request.getSession().getAttribute(CauliflowerInfo.USER_ATTRIBUTE);
        if(user==null || (user!=null && !user.getUserRole().equals(UserRole.PROVISIONING_ENG.toString()))) {
            try {
                logger.info("ProvisioningEngineer :: user == null send redirect to home jsp ");
                response.sendRedirect(CauliflowerInfo.HOME_LINK);
            } catch (IOException e) {
                logger.error(e);
            }
        }else{
            try {
                logger.info("ProvisioningEngineer :: user has permission and was send to ");
                chain.doFilter(request,response);
            } catch (IOException e) {
                logger.error(e);
            } catch (ServletException e) {
                logger.error(e);
            }
        }
    }
}
