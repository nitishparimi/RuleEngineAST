package com.example.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Stack;

@Service
public class RuleService {

    @Autowired
    private RuleRepository ruleRepository;

    private final ObjectMapper objectMapper;

    // Constructor
    public RuleService(RuleRepository ruleRepository, ObjectMapper objectMapper) {
        this.ruleRepository = ruleRepository;
        this.objectMapper = objectMapper;
    }
    
    public void insertInitialRules() {
    	if (ruleRepository.count() == 0) { 
    	    Rule rule1 = new Rule();
    	    rule1.setAst("{\"type\":\"operator\",\"value\":\"AND\",\"left\":{\"type\":\"operator\",\"value\":\"OR\",\"left\":{\"type\":\"operator\",\"value\":\"AND\",\"left\":{\"type\":\"operand\",\"value\":\"age > 30\",\"left\":null,\"right\":null},\"right\":{\"type\":\"operand\",\"value\":\"department = 'Sales'\",\"left\":null,\"right\":null}},\"right\":{\"type\":\"operator\",\"value\":\"AND\",\"left\":{\"type\":\"operand\",\"value\":\"age < 25\",\"left\":null,\"right\":null},\"right\":{\"type\":\"operand\",\"value\":\"department = 'Marketing'\",\"left\":null,\"right\":null}}},\"right\":{\"type\":\"operator\",\"value\":\"OR\",\"left\":{\"type\":\"operand\",\"value\":\"salary > 50000\",\"left\":null,\"right\":null},\"right\":{\"type\":\"operand\",\"value\":\"experience > 5\",\"left\":null,\"right\":null}}}");
    	    rule1.setRuleString("((age > 30 AND department = 'Sales') OR (age < 25 AND department = 'Marketing')) AND (salary > 50000 OR experience > 5)");

    	    Rule rule2 = new Rule();
    	    rule2.setAst("{\"type\":\"operator\",\"value\":\"AND\",\"left\":{\"type\":\"operator\",\"value\":\"AND\",\"left\":{\"type\":\"operand\",\"value\":\"age > 30\",\"left\":null,\"right\":null},\"right\":{\"type\":\"operand\",\"value\":\"department = 'Marketing'\",\"left\":null,\"right\":null}},\"right\":{\"type\":\"operator\",\"value\":\"OR\",\"left\":{\"type\":\"operand\",\"value\":\"salary > 20000\",\"left\":null,\"right\":null},\"right\":{\"type\":\"operand\",\"value\":\"experience > 5\",\"left\":null,\"right\":null}}}");
    	    rule2.setRuleString("((age > 30 AND department = 'Marketing')) AND (salary > 20000 OR experience > 5)");

    	    ruleRepository.save(rule1);
    	    ruleRepository.save(rule2);
    	}

    }
    
    public Rule mergeRules(Long ruleId1, Long ruleId2, String logicalOperator) throws JsonProcessingException {
        // Fetch the two rules from the repository
        Rule rule1 = ruleRepository.findById(ruleId1)
                .orElseThrow(() -> new RuntimeException("Rule not found: " + ruleId1));
        Rule rule2 = ruleRepository.findById(ruleId2)
                .orElseThrow(() -> new RuntimeException("Rule not found: " + ruleId2));

        // Parse the ASTs of both rules
        Node ast1 = jsonToAst(rule1.getAst());
        Node ast2 = jsonToAst(rule2.getAst());

        // Create a new AST node for the logical operator
        Node mergedAst = new Node("operator", logicalOperator);
        mergedAst.setLeft(ast1); 
        mergedAst.setRight(ast2); 

        // Convert the merged AST back to JSON
        String mergedAstJson = astToJson(mergedAst);

        // Create a new merged rule
        Rule mergedRule = new Rule();
        mergedRule.setAst(mergedAstJson);
        mergedRule.setRuleString("(" + rule1.getRuleString() + ") " + logicalOperator + " (" + rule2.getRuleString() + ")");

        // Save and return the merged rule
        return ruleRepository.save(mergedRule);
    }


    // Create a new rule from a rule string
    public Rule createRule(String ruleString) {
        Node ast = parseRuleString(ruleString);
        Rule rule = new Rule();
        rule.setRuleString(ruleString);
        
        String astJson = astToJson(ast);  // Convert AST to JSON string
        rule.setAst(astJson);

        // Log AST JSON before saving
        System.out.println("AST to be saved: " + astJson);
        
        return ruleRepository.save(rule);
    }

    // Evaluate a rule based on the rule ID and user attributes
    public boolean evaluateRule(Long ruleId, UserAttributes data) {
       
        Rule rule = ruleRepository.findById(ruleId).orElseThrow(() -> new RuntimeException("Rule not found"));
        
        try {
            System.out.println("Fetched AST: " + rule.getAst());

            if (rule.getAst() == null) {
                throw new RuntimeException("AST is null for Rule ID: " + ruleId);
            }

            Node root = jsonToAst(rule.getAst());

            // Evaluate the AST
            return evaluateAst(root, data);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Parsing the rule string into an AST
    private Node parseRuleString(String ruleString) {
        String[] tokens = ruleString.replace("(", "( ").replace(")", " )").split("\\s+");
        return buildAstFromTokens(tokens);
    }

    private Node buildAstFromTokens(String[] tokens) {
        Stack<Node> nodeStack = new Stack<>();
        Stack<String> operatorStack = new Stack<>();

        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            switch (token) {
                case "(":
                    operatorStack.push(token);
                    break;
                case ")":
                    while (!operatorStack.isEmpty() && !"(".equals(operatorStack.peek())) {
                        nodeStack.push(createNodeFromStack(operatorStack, nodeStack));
                    }
                    operatorStack.pop();
                    break;
                case "AND":
                case "OR":
                    while (!operatorStack.isEmpty() && precedence(operatorStack.peek()) >= precedence(token)) {
                        nodeStack.push(createNodeFromStack(operatorStack, nodeStack));
                    }
                    operatorStack.push(token);
                    break;
                default:
                    if (i + 2 < tokens.length && isOperator(tokens[i + 1])) {
                        nodeStack.push(createOperandNode(tokens[i], tokens[i + 1], tokens[i + 2]));
                        i += 2; // 
                    } else {
                        throw new IllegalArgumentException("Invalid token sequence: " + token);
                    }
                    break;
            }
        }

        while (!operatorStack.isEmpty()) {
            nodeStack.push(createNodeFromStack(operatorStack, nodeStack));
        }

        return nodeStack.pop();
    }
    
    private int precedence(String operator) {
        switch (operator) {
            case "AND":
                return 2;
            case "OR":
                return 1;
            default:
                return 0;
        }
    }

    private Node createOperandNode(String leftOperand, String operator, String rightOperand) {
        String condition = leftOperand + " " + operator + " " + rightOperand;
        return new Node("operand", condition); 
    }

    private boolean isOperator(String token) {
        return token.equals(">") || token.equals("<") || token.equals("=") || token.equals(">=") || token.equals("<=") || token.equals("!=");
    }

    private Node createNodeFromStack(Stack<String> operatorStack, Stack<Node> nodeStack) {
        if (operatorStack.isEmpty() || nodeStack.size() < 2) {
            throw new RuntimeException("Error creating AST: operator or operand stack is invalid.");
        }

        String operator = operatorStack.pop();
        Node rightNode = nodeStack.pop();
        Node leftNode = nodeStack.pop();
        Node operatorNode = new Node("operator", operator);
        operatorNode.setLeft(leftNode);
        operatorNode.setRight(rightNode);
        return operatorNode;
    }

    private boolean evaluateAst(Node ast, UserAttributes data) {
        if (ast == null) {
            throw new RuntimeException("AST node is null"); 
        }

        if (ast.getType().equals("operand")) {
            return evaluateCondition(ast.getValue(), data);
        } else if (ast.getType().equals("operator")) {
            boolean leftResult = ast.getLeft() != null ? evaluateAst(ast.getLeft(), data) : false;
            boolean rightResult = ast.getRight() != null ? evaluateAst(ast.getRight(), data) : false;

            switch (ast.getValue()) {
                case "AND":
                    return leftResult && rightResult;
                case "OR":
                    return leftResult || rightResult;
                default:
                    throw new IllegalArgumentException("Unknown operator: " + ast.getValue());
            }
        }
        return false;
    }

    private boolean evaluateCondition(String condition, UserAttributes data) {
        String[] parts = condition.split(" ");
        
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid condition format: " + condition + ". Expected format: attribute operator value.");
        }

        String attribute = parts[0];
        String operator = parts[1];
        String value = parts[2].replace("'", "").trim();

        switch (attribute) {
            case "age":
                int age = data.getAge();
                int ageValue = Integer.parseInt(value);
                return evaluateComparison(age, operator, ageValue);
                
            case "department":
                String department = data.getDepartment();
                return department != null && department.equalsIgnoreCase(value);
                
            case "salary":
                double salary = data.getIncome();
                double salaryValue = Double.parseDouble(value);
                return evaluateComparison(salary, operator, salaryValue);
                
            case "experience": 
                int experience = data.getExperience();
                int experienceValue = Integer.parseInt(value);
                return evaluateComparison(experience, operator, experienceValue);
                
            default:
                throw new IllegalArgumentException("Unknown attribute: " + attribute);
        }
    }

    private boolean evaluateComparison(double left, String operator, double right) {
        switch (operator) {
            case ">":
                return left > right;
            case "<":
                return left < right;
            case ">=":
                return left >= right;
            case "<=":
                return left <= right;
            case "==":
                return left == right;
            case "!=":
                return left != right;
            default:
                throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }

    private boolean evaluateComparison(int left, String operator, int right) {
        switch (operator) {
            case ">":
                return left > right;
            case "<":
                return left < right;
            case ">=":
                return left >= right;
            case "<=":
                return left <= right;
            case "==":
                return left == right;
            case "!=":
                return left != right;
            default:
                throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }

    private String astToJson(Node ast) {
        try {
            return objectMapper.writeValueAsString(ast);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert AST to JSON", e);
        }
    }

    public Node jsonToAst(String jsonString) throws JsonProcessingException {
        try {
            return objectMapper.readValue(jsonString, Node.class); 
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert JSON to AST", e);
        }
    }
}
