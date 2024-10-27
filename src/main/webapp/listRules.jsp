<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<!DOCTYPE html>
<html>
<head>
    <title>List of Rules</title>
</head>
<body>
    <h2>List of Created Rules</h2>
    <table border="1">
        <tr>
            <th>Rule ID</th>
            <th>Rule String</th>
            <th>Actions</th>
        </tr>
        <c:forEach var="rule" items="${rules}">
            <tr>
                <td>${rule.id}</td>
                <td>${rule.ruleString}</td>
                <td>
                    <form action="evaluateRule" method="get">
                        <input type="hidden" name="ruleId" value="${rule.id}"/>
                        <input type="submit" value="Evaluate"/>
                    </form>
                </td>
            </tr>
        </c:forEach>
    </table>
</body>
</html>
