<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Merge Rules</title>
</head>
<body>

<h2>Merge Rules</h2>

<form action="/mergeRules" method="POST">
    <label for="ruleId1">Rule ID 1:</label>
    <input type="text" id="ruleId1" name="ruleId1" required><br><br>

    <label for="ruleId2">Rule ID 2:</label>
    <input type="text" id="ruleId2" name="ruleId2" required><br><br>

    <label for="operator">Logical Operator:</label>
    <select id="operator" name="logicalOperator" required>
        <option value="AND">AND</option>
        <option value="OR">OR</option>
        <option value="NOT">NOT</option>
    </select><br><br>

    <input type="submit" value="Merge Rules">
</form>

<c:if test="${not empty mergedRule}">
    <h3>Merged Rule</h3>
    <p><strong>Rule String:</strong> ${mergedRule.ruleString}</p>
    <p><strong>Merged AST:</strong> ${mergedRule.ast}</p>
</c:if>

</body>
</html>
