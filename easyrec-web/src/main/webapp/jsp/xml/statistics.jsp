<?xml version="1.0" encoding="ISO-8859-1"?>
<%@ page contentType="text/xml; charset=ISO-8859-1" %>
<%@ page language="java" session="false" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<easyrec>
    <tenants>
        <tenant id="${tenantId}">
            <numberOfTotalActions>${tenantStatistics.actions}</numberOfTotalActions>
            <numberOfTotalRules>${remoteTenant.rules}</numberOfTotalRules>
            <numberOfClicksOnRecommendations>${tenantStatistics.backtracks}</numberOfClicksOnRecommendations>
            <conversions>${conversionStatistics.recommendationToBuyCount}</conversions>
            <numberOfTotalItems>${tenantStatistics.items}</numberOfTotalItems>
            <numberOfTotalUsers>${tenantStatistics.users}</numberOfTotalUsers>
            <averageActionsPerUser>${tenantStatistics.averageActionsPerUser}</averageActionsPerUser>
            <usersWith1action>${userStatistics.users_with_1_action}</usersWith1action>
            <usersWith2actions>${userStatistics.users_with_2_actions}</usersWith2actions>
            <usersWith3to10actions>${userStatistics.users_with_3_10_actions}</usersWith3to10actions>
            <usersWith11to100actions>${userStatistics.users_with_11_100_actions}</usersWith11to100actions>
            <usersWith100andMoreActions>${userStatistics.users_with_101_and_more_actions}</usersWith100andMoreActions>
            <assocs>
                <c:forEach var="stat" items="${statAssoc}" varStatus="status">
                    <assoc type="${stat.key}">
                        <totalActions>${stat.value.actions}</totalActions>
                        <rulesCreated>${stat.value.rules}</rulesCreated>
                        <numberOfItemsWithRules>${stat.value.itemsWithRules}</numberOfItemsWithRules>
                        <coverage>${fn:substringBefore((stat.value.itemsWithRules/tenantStatistics.items)*100, '.')}</coverage>
                        <averageNumberOfRulesPerItem>${stat.value.averageNumberOfRulesPerItem}</averageNumberOfRulesPerItem>
                        <standardDeviation>${stat.value.stdNumberOfRulesPerItem}</standardDeviation>
                    </assoc>
                </c:forEach>
            </assocs>
            <confidenceLessThen20>${ruleMinerStatistics.group1}</confidenceLessThen20>
            <confidenceBetween20and40>${ruleMinerStatistics.group2}</confidenceBetween20and40>
            <confidenceBetween40and60>${ruleMinerStatistics.group3}</confidenceBetween40and60>
            <confidenceBetween60and80>${ruleMinerStatistics.group4}</confidenceBetween60and80>
            <confidenceGreaterThen80>${ruleMinerStatistics.group5}</confidenceGreaterThen80>
        </tenant>
    </tenants>
</easyrec>