<%@ include file="/html/common/init.jsp" %>

<%@page import="com.inikah.slayer.model.Plan"%>
<%@page import="com.inikah.slayer.service.PlanLocalServiceUtil"%>

<%
	List<Plan> plans = PlanLocalServiceUtil.getPlans(company.getCompanyId());
%>

<liferay-ui:header title="<%= profile.getTitle() %>"/>

<aui:layout cssClass="plan-box">
	<%
		for (Plan plan: plans) {
			%>
				<portlet:actionURL var="showPaymentOptionsURL" name="showPaymentOptions">
					<portlet:param name="planId" value="<%= String.valueOf(plan.getPlanId()) %>"/>
				</portlet:actionURL>
				<aui:column columnWidth="25">
					<div><%= plan.getPlanName() %></div>
					<div>Validity: <%= plan.getValidity() %> Month(s)</div>
					<div>(<%= plan.getValidity() * 30 %> Days)</div>
					<div><%= profile.getPrice(plan.getPlanId()) %></div>
					<div>Discount <%= plan.getDiscount() %>%</div>
					<div>What you pay: </div>
					<aui:button value="pick" onClick="<%= showPaymentOptionsURL %>" disabled="<%= (profile.getCurrentPlan() > plan.getPlanId()) %>"/>
				</aui:column>			
			<% 
		}
	%>
</aui:layout>