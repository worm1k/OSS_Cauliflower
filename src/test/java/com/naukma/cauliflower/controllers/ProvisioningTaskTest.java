package com.naukma.cauliflower.controllers;

import com.naukma.cauliflower.dao.DAO;
import com.naukma.cauliflower.dao.TaskName;
import com.naukma.cauliflower.dao.UserRole;
import com.naukma.cauliflower.entities.ServiceOrder;
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
 * Created by Алексей on 15.12.2014.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest( DAO.class )
public class ProvisioningTaskTest {
    private HttpServletRequest request = createNiceMock(HttpServletRequest.class);
    private HttpServletResponse response = createStrictMock(HttpServletResponse.class);
    private HttpSession session = createNiceMock(HttpSession.class);
    private ProvisioningTaskController controller = null;
    DAO dao = null;
    {
        suppress(constructor(DAO.class));
        dao = PowerMock.createNiceMock(DAO.class);
        controller = new ProvisioningTaskController();
        controller.dao = dao;
    }

    @Test
    public void taskNullTest() throws Exception {
        User userStub = new User(0, 0, "", "", "", "", "", false);

        expect(request.getSession()).andReturn(session).times(2);
        expect(session.getAttribute(CauliflowerInfo.USER_ATTRIBUTE)).andReturn(userStub);
        expect(request.getParameter(isA(String.class))).andReturn(null);
        session.setAttribute(CauliflowerInfo.ERROR_ATTRIBUTE, CauliflowerInfo.SYSTEM_ERROR_MESSAGE);
        expectLastCall();
        response.sendRedirect(CauliflowerInfo.DASHBOARD_LINK);
        expectLastCall();

        replay(request, response, session);

        new InstallationTasksController().doPost(request, response);

        verify(request, response, session);
        reset(request, response, session);
    }

    @Test
    /**
     * When task doesn't have status PROCESSING
     */
    public void badTaskTest() throws Exception {
        Task taskStub = new Task(0, 0, 0, 0, "NOT PROCESSING", null);

        expect(request.getSession()).andReturn(session);
        expect(session.getAttribute(CauliflowerInfo.USER_ATTRIBUTE)).andReturn(createMock(User.class));
        expect(request.getParameter(CauliflowerInfo.TASK_ID_PARAM)).andReturn("0");
        expect(dao.getTaskById(anyInt())).andReturn(taskStub);
        response.sendRedirect(CauliflowerInfo.HOME_LINK);
        expectLastCall();

        replay(request, response, session, dao);

        controller.doPost(request, response);

        verify(request, response, session, dao);
        reset(request, response, session, dao);
    }

    @Test
    /**
     * When user role id is not equal to expected user role id
     */
    public void badUserTest() throws Exception {
        int userRoleId = 1;
        int userRoleExpected = 0;
        User userStub = new User(0, userRoleId, "", "", "", "", "", false);
        Task taskStub = new Task(0, 0, 0, 0, "PROCESSING", null);

        expect(request.getSession()).andReturn(session);
        expect(session.getAttribute(CauliflowerInfo.USER_ATTRIBUTE)).andReturn(userStub);
        expect(request.getParameter(isA(String.class))).andStubReturn("0");
        expect(dao.getTaskById(anyInt())).andReturn(taskStub);
        expect(dao.getUserRoleIdFor(UserRole.PROVISIONING_ENG)).andReturn(userRoleExpected);
        response.sendRedirect(CauliflowerInfo.HOME_LINK);
        expectLastCall();

        replay(request, response, session, dao);

        controller.doPost(request, response);

        verify(request, response, session, dao);
        reset(request, response, session, dao);
    }

    @Test
    /**
     * When all attributes and their status are initialised as expected, servlet should finally redirect
     * to provisioning engineer dashboard.
     */
    public void normalWorkFlowTest() throws Exception {
        int userRoleId = 0;
        User userStub = new User(0, userRoleId, "", "", "", "", "", false);
        //Connect instance (for example, because nothing will be changed)
        Task taskStub = new Task(0, 0, 0, 0, "PROCESSING", TaskName.CONNECT_INSTANCE);
        ServiceOrder serviceOrderStub = new ServiceOrder(0, 0, "", 0, 0, "", null, 0);

        expect(request.getSession()).andReturn(session);
        expect(session.getAttribute(CauliflowerInfo.USER_ATTRIBUTE)).andReturn(userStub);
        expect(request.getParameter(isA(String.class))).andStubReturn("0");
        expect(dao.getTaskById(anyInt())).andReturn(taskStub);
        expect(dao.getUserRoleIdFor(UserRole.PROVISIONING_ENG)).andReturn(userRoleId);
        expect(dao.getServiceOrder(anyInt())).andReturn(serviceOrderStub);


        response.sendRedirect(CauliflowerInfo.PROVIS_ENGINEER_DASHBOARD_LINK);
        expectLastCall();

        replay(request, response, session, dao);

        controller.doPost(request, response);

        verify(request, response, session, dao);
        reset(request, response, session, dao);
    }
}



