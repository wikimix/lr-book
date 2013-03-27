package com.mpower.job;

import java.util.Iterator;

import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.GroupConstants;
import com.liferay.portal.service.CompanyLocalServiceUtil;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.mpower.util.InvitationUtil;

public class ReminderInvitationJob extends BaseMessageListener {

	protected void doReceive(Message message) throws Exception {

		long userId = 10195l;
		
		long companyId = CompanyLocalServiceUtil.getCompanyIdByUserId(userId);
		
		Group group = GroupLocalServiceUtil.fetchGroup(companyId, GroupConstants.GUEST);
		
		String portalURL = ""; // ???????
		System.out.println("createAccountURL ==> " + 
				InvitationUtil.getCreateAccountURL(group.getGroupId(), portalURL, userId));
		
		Iterator<String> itr = message.getValues().keySet().iterator();
		
		while (itr.hasNext()) {
			System.out.println(itr.next());
		}
	}
}