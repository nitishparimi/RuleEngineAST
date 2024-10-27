<%@page import="com.example.demo.Rule"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<!DOCTYPE html>
<html>
<head>
    <title>Evaluate Rule</title>
    <style>
        table {
            border-collapse: collapse;
            width: 50%;
            margin-bottom: 20px;
        }

        table, th, td {
            border: 1px solid black;
        }

        th, td {
            padding: 10px;
            text-align: left;
        }
    </style>
</head>
<body>

    <h1>Current Weather Data</h1>
    <table>
        <tr>
            <th>ID</th>
            <th>Rule</th>
        </tr>
        <% 
            List<Rule> rules = (List<Rule>) request.getAttribute("rules");
            if (rules != null) {
                for (Rule s : rules) { 
        %>
            <tr>
                <td><%= s.getId() %></td>
                <td><%= s.getRuleString() %></td>
            </tr>
        <% 
                } 
            } 
        %>
    </table>

    <h2>Evaluate Rule</h2>
    <form action="/evaluate" method="post">
        Rule ID: <input type="number" name="ruleId" required/><br/>
        Age: <input type="number" name="age" required/><br/>
        Income: <input type="number" step="0.01" name="income" required/><br/>
        Department: <input type="text" name="department" required/><br/>
        Experience: <input type="number" name="experience" required/><br/>
        <input type="submit" value="Evaluate"/>
    </form>
    
    <a href="/mergeRules">Merge Any two rules</a>
    <a href="/create">Create Rule</a>
    
</body>
</html>
