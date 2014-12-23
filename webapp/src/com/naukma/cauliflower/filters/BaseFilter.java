package com.naukma.cauliflower.filters;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by ihor on 09.12.2014.
 */
public abstract class BaseFilter implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {
        //NOP
    }


    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        doFilter((HttpServletRequest) req, (HttpServletResponse) resp, chain);
    }

    public abstract void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException;


    public void destroy() {
        //NOP
    }
}
