package com.example.demo;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RuleEngineController {

    @Autowired
    private RuleService ruleService;
    
    @Autowired
    private RuleRepository rulerepo;

    @GetMapping("/create")
    public String showCreateRulePage() {
        return "createRule.jsp";  
    }

    @PostMapping("/createRule")
    @ResponseBody
    public String createRule(@RequestParam String ruleString, Model model) {
        Rule rule = ruleService.createRule(ruleString);
        model.addAttribute("rule", rule);
        return "ruleCreated";  
    }

    @GetMapping("/evaluate")
    public String showEvaluateRulePage( Model model) {
    	List<Rule> rules = rulerepo.findAll();
        model.addAttribute("rules", rules);
        return "evaluateRule.jsp";  
    }

    @PostMapping("/evaluate")
    @ResponseBody
    public String evaluateRule(@RequestParam Long ruleId, @RequestParam int age,
                               @RequestParam double income, @RequestParam String department,
                               @RequestParam int experience, Model model) {
        UserAttributes userAttributes = new UserAttributes();
        userAttributes.setAge(age);
        userAttributes.setIncome(income);
        userAttributes.setDepartment(department);
        userAttributes.setExperience(experience);  

        boolean result = ruleService.evaluateRule(ruleId, userAttributes);
        model.addAttribute("result", result);
        
        return result ? "Evaluation Success" : "Evaluation Not Success";
    }
    
    @GetMapping("/mergeRules")
    public String mergeRules() {
    	return "mergeRules.jsp";
    }
    
    @PostMapping("/mergeRules")
    @ResponseBody
    public String mergeRules(
            @RequestParam("ruleId1") Long ruleId1,
            @RequestParam("ruleId2") Long ruleId2,
            @RequestParam("logicalOperator") String logicalOperator,
            Model model) {

        try {
            // Call the mergeRules method in the service layer
            Rule mergedRule = ruleService.mergeRules(ruleId1, ruleId2, logicalOperator);

            // Add the merged rule to the model to display it in the JSP
            model.addAttribute("mergedRule", mergedRule);

        } catch (Exception e) {
            // Handle exceptions and show an error message
            model.addAttribute("error", "Error merging rules: " + e.getMessage());
        }

        // Redirect back to the same JSP page to display the merged rule
        return "Process Success";
    }

}
