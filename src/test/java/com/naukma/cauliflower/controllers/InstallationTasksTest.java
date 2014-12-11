package com.naukma.cauliflower.controllers;

import com.naukma.cauliflower.entities.User;
import com.naukma.cauliflower.info.CauliflowerInfo;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.easymock.EasyMock.*;


/**
 * Created by Алексей on 11.12.2014.
 */
public class InstallationTasksTest {
    private HttpServletRequest request = createNiceMock(HttpServletRequest.class);
    private HttpServletResponse response = createStrictMock(HttpServletResponse.class);
    private HttpSession session = createNiceMock(HttpSession.class);

    @Test
    public void testUserNull() throws Exception {

        expect(request.getSession()).andReturn(session);
        expect(session.getAttribute(CauliflowerInfo.USER_ATTRIBUTE)).andReturn(null);

        response.sendRedirect(CauliflowerInfo.AUTH_LINK);
        expectLastCall().times(1);

        replay(request, response, session);

        new InstallationTasksController().doPost(request, response);

        verify(request, response, session);
        reset(request, response, session);
    }


    @Test
    public void testTaskAndServiceNull() throws Exception {
        expect(request.getSession()).andReturn(session).times(2);
        expect(session.getAttribute(CauliflowerInfo.USER_ATTRIBUTE)).andReturn(createMock(User.class));
        expect(request.getParameter(CauliflowerInfo.TASK_ID_PARAM)).andReturn(null);
        expect(request.getParameter(CauliflowerInfo.SERVICE_ORDER_ID)).andReturn(null);
        response.sendRedirect(CauliflowerInfo.DASHBOARD_LINK);
        expectLastCall().times(1);

        replay(request, response, session);

        new InstallationTasksController().doPost(request, response);

        verify(request, response, session);
        reset(request, response, session);
    }
}



