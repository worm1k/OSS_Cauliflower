package com.naukma.cauliflower.filters;

import com.naukma.cauliflower.entities.User;
import com.naukma.cauliflower.info.CauliflowerInfo;
import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by ihor on 09.12.2014.
 */
@WebFilter(filterName = "AuthenticationFilter",urlPatterns = "/auth.jsp")
public  class AuthenticationFilter extends BaseFilter {
    private static final Logger logger = Logger.getLogger(AuthenticationFilter.class);


    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
        logger.info("AuthenticationFilter :: */auth.jsp   ");
        User user = (User) request.getSession().getAttribute(CauliflowerInfo.USER_ATTRIBUTE);
        if(user!=null){
            logger.info("AuthenticationFilter :: user !=null");
            try {
                response.sendRedirect(CauliflowerInfo.HOME_LINK);
            } catch (IOException e) {
                logger.error(e);
            }

        }else{
            try {
                logger.info("AuthenticationFilter :: user ==null goto /auth.jsp ");
                chain.doFilter(request,response);
            } catch (IOException e) {
                logger.error(e.toString());
            } catch (ServletException e) {
                logger.error(e.toString());
            }
        }
    }

}
