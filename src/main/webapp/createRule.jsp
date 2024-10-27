<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<!DOCTYPE html>
<html>
<head>
    <title>Create Rule</title>
</head>
<body>
    <h2>Create a New Rule</h2>
    <form action="/createRule" method="post">
        Rule: <input type="text" name="ruleString" required/><br/>
        <input type="submit" value="Create Rule"/>
    </form>
    
    <a href="/evaluate">Evaluate Rules</a>
</body>
</html>
