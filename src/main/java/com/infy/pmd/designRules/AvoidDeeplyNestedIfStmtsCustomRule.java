package com.infy.pmd.designRules;

import com.infy.bpe.core.CodeAnalyser;
import com.infy.bpe.core.ToolingOperations;
import com.infy.utility.BPEnforcerConstants;
import com.infy.utility.CategoryConstants;


//package net.sourceforge.pmd.lang.apex.rule.design;

import net.sourceforge.pmd.lang.apex.ast.ASTIfBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.properties.IntegerProperty;

public class AvoidDeeplyNestedIfStmtsCustomRule extends AbstractApexRule {

    private int depth;
    private int depthLimit;

    private static final IntegerProperty PROBLEM_DEPTH_DESCRIPTOR
            = IntegerProperty.named("problemDepth")
                             .desc("The if statement depth reporting threshold")
                             .range(1, 25).defaultValue(3).uiOrder(1.0f).build();

    public AvoidDeeplyNestedIfStmtsCustomRule() {
        definePropertyDescriptor(PROBLEM_DEPTH_DESCRIPTOR);

        setProperty(CODECLIMATE_CATEGORIES, "Complexity");
        // Note: Remedy needs better OO design and therefore high effort
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 200);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
    }

    @Override
    public Object visit(ASTUserClass node, Object data) {
        depth = 0;
        depthLimit = getProperty(PROBLEM_DEPTH_DESCRIPTOR);

        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTIfBlockStatement node, Object data) {
        depth++;

        super.visit(node, data);
        if (depth == depthLimit) {
           
            System.out.println(node.getNode().getDefiningType().getApexName());
            System.out.println(depthLimit+"   "+depth+"  "+node.getBeginLine());
            
        	CodeAnalyser.addBestPracticesRule(ToolingOperations.setClassReportDtls(node.getNode().getDefiningType().getApexName(), CategoryConstants.APEX_ASSERTS, "PMD: Avoid Deeply nested If statements : ",node.getBeginLine(),BPEnforcerConstants.LOW, BPEnforcerConstants.AVOID_NESTED_IF,"MEDIUM",BPEnforcerConstants.DESIGN));

            //addViolation(data, node);

        } 
        depth--;

        return data;
    }
}
