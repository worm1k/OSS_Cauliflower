package com.naukma.cauliflower.controllers;

import com.naukma.cauliflower.dao.DAO;
import com.naukma.cauliflower.dao.TaskName;
import com.naukma.cauliflower.dao.UserRole;
import com.naukma.cauliflower.entities.Task;
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

    @Test
    public void badTaskTest() throws Exception {
        InstallationTasksController inst = new InstallationTasksController();

        inst.dao = createNiceMock(DAO.class);
        DAO dao = inst.dao;
        expect(request.getSession()).andReturn(session).times(1);
        expect(session.getAttribute(CauliflowerInfo.USER_ATTRIBUTE)).andReturn(createMock(User.class));
        expect(request.getParameter(CauliflowerInfo.TASK_ID_PARAM)).andReturn("0");
        expect(request.getParameter(CauliflowerInfo.SERVICE_ORDER_ID)).andReturn("0");
        expect(dao.getTaskById(anyInt())).andReturn(new Task(0, 0, 0, 0, "", null));
        response.sendRedirect(CauliflowerInfo.HOME_LINK);
        expectLastCall().times(1);

        replay(request, response, session, dao);

        inst.doPost(request, response);

        verify(request, response, session, dao);
        reset(request, response, session, dao);
    }

    @Test
    public void badUserTest() throws Exception {
        int userRoleId = 1;
        int userRoleExpected = 0;
        InstallationTasksController inst = new InstallationTasksController();

        inst.dao = createNiceMock(DAO.class);
        DAO dao = inst.dao;
        expect(request.getSession()).andReturn(session).times(1);
        expect(session.getAttribute(CauliflowerInfo.USER_ATTRIBUTE)).andReturn(new User(0, userRoleId, "", "", "", "", "", false));
        expect(request.getParameter(CauliflowerInfo.TASK_ID_PARAM)).andReturn("0");
        expect(request.getParameter(CauliflowerInfo.SERVICE_ORDER_ID)).andReturn("0");
        expect(dao.getTaskById(anyInt())).andReturn(new Task(0, 0, 0, 0, "PROCESSING", null));
        expect(dao.getUserRoleIdFor(UserRole.INSTALLATION_ENG)).andReturn(userRoleExpected);
        response.sendRedirect(CauliflowerInfo.HOME_LINK);
        expectLastCall().times(1);

        replay(request, response, session, dao);

        inst.doPost(request, response);

        verify(request, response, session, dao);
        reset(request, response, session, dao);
    }
}



