package com.naukma.cauliflower.controllers;

import com.naukma.cauliflower.dao.DAO;
import com.naukma.cauliflower.dao.TaskName;
import com.naukma.cauliflower.dao.UserRole;
import com.naukma.cauliflower.entities.Task;
import com.naukma.cauliflower.entities.User;
import com.naukma.cauliflower.info.CauliflowerInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.easymock.EasyMock.*;
import static org.powermock.api.support.membermodification.MemberMatcher.constructor;
import static org.powermock.api.support.membermodification.MemberModifier.suppress;


/**
 * Created by Алексей on 11.12.2014.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest( DAO.class )
public class InstallationTasksTest {
    private HttpServletRequest request = createNiceMock(HttpServletRequest.class);
    private HttpServletResponse response = createStrictMock(HttpServletResponse.class);
    private HttpSession session = createNiceMock(HttpSession.class);
    private InstallationTasksController controller = null;
    DAO dao = null;
    {
        suppress(constructor(DAO.class));
        dao = PowerMock.createNiceMock(DAO.class);
        controller = new InstallationTasksController();
        controller.dao = dao;
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
        expect(request.getSession()).andReturn(session);
        expect(session.getAttribute(CauliflowerInfo.USER_ATTRIBUTE)).andReturn(createMock(User.class));
        expect(request.getParameter(CauliflowerInfo.TASK_ID_PARAM)).andReturn("0");
        expect(request.getParameter(CauliflowerInfo.SERVICE_ORDER_ID)).andReturn("0");
        expect(dao.getTaskById(anyInt())).andReturn(new Task(0, 0, 0, 0, "", null));
        response.sendRedirect(CauliflowerInfo.HOME_LINK);
        expectLastCall().times(1);

        replay(request, response, session, dao);

        controller.doPost(request, response);

        verify(request, response, session, dao);
        reset(request, response, session, dao);
    }

    @Test
    public void badUserTest() throws Exception {
        int userRoleId = 1;
        int userRoleExpected = 0;

        expect(request.getSession()).andReturn(session);
        expect(session.getAttribute(CauliflowerInfo.USER_ATTRIBUTE)).andReturn(new User(0, userRoleId, "", "", "", "", "", false));
        expect(request.getParameter(CauliflowerInfo.TASK_ID_PARAM)).andReturn("0");
        expect(request.getParameter(CauliflowerInfo.SERVICE_ORDER_ID)).andReturn("0");
        expect(dao.getTaskById(anyInt())).andReturn(new Task(0, 0, 0, 0, "PROCESSING", null));
        expect(dao.getUserRoleIdFor(UserRole.INSTALLATION_ENG)).andReturn(userRoleExpected);
        response.sendRedirect(CauliflowerInfo.HOME_LINK);
        expectLastCall().times(1);

        replay(request, response, session, dao);

        controller.doPost(request, response);

        verify(request, response, session, dao);
        reset(request, response, session, dao);
    }

    @Test
    public void noFreePortsAttributeTest() throws Exception {
        int userRoleId = 0;

        expect(request.getSession()).andReturn(session).times(2);
        expect(session.getAttribute(CauliflowerInfo.USER_ATTRIBUTE)).andReturn(new User(0, userRoleId, "", "", "", "", "", false));
        expect(request.getParameter(isA(String.class))).andStubReturn("0");
        expect(dao.getTaskById(anyInt())).andReturn(new Task(0, 0, 0, 0, "PROCESSING", TaskName.CREATE_CIRCUIT));
        expect(dao.getUserRoleIdFor(UserRole.INSTALLATION_ENG)).andReturn(userRoleId);
        expect(dao.freePortExists()).andReturn(false);
        session.setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.NO_PORTS_ERROR_MESSAGE);
        expectLastCall();


        response.sendRedirect(CauliflowerInfo.INSTALL_ENGINEER_DASHBOARD_LINK);
        expectLastCall();

        replay(request, response, session, dao);

        controller.doPost(request, response);

        verify(request, response, session, dao);
        reset(request, response, session, dao);
    }
}



