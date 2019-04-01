/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package com.infy.pmd.performance;

import com.infy.bpe.core.CodeAnalyser;
import com.infy.bpe.core.ToolingOperations;
import com.infy.utility.BPEnforcerConstants;
import com.infy.utility.CategoryConstants;

import net.sourceforge.pmd.lang.apex.ast.ASTDmlDeleteStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlInsertStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlMergeStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlUndeleteStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlUpdateStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlUpsertStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDoLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTForEachStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTForLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTWhileLoopStatement;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.ast.AbstractNode;
import net.sourceforge.pmd.lang.ast.Node;

public class AvoidDmlStatementsInLoopsCustomRule extends AbstractApexRule {

    public AvoidDmlStatementsInLoopsCustomRule() {
        setProperty(CODECLIMATE_CATEGORIES, "Performance");
        // Note: Often more complicated as just moving the SOQL a few lines.
        // Involves Maps...
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 150);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
    }

    @Override
    public Object visit(ASTDmlDeleteStatement node, Object data) {
        if (insideLoop(node)) {
        	System.out.println("******AvoidDmlStatementsInLoopsCustomRule******");
        	//System.out.println(node.getNode().getDefiningType().getApexName());
        	//System.out.println(node.getBeginLine());
        	//System.out.println (node.getEndLine());
          	CodeAnalyser.addBestPracticesRule(ToolingOperations.setClassReportDtls(node.getNode().getDefiningType().getApexName(), CategoryConstants.AVOID_DML_STATEMENTS_IN_LOOPS, "DML DELETE statement in Loop ",node.getBeginLine(),
					BPEnforcerConstants.HIGH, BPEnforcerConstants.DML_IN_LOOP,"MEDIUM",BPEnforcerConstants.PERFORMANCE));
            //addViolation(data, node);
            
        }
        return data;
    }

    @Override
    public Object visit(ASTDmlInsertStatement node, Object data) {
        if (insideLoop(node)) {
        	System.out.println("******AvoidDmlStatementsInLoopsCustomRule******");

        	//System.out.println(node.getNode().getDefiningType().getApexName());
        	//System.out.println(node.getBeginLine());
        	//System.out.println (node.getEndLine());
          	CodeAnalyser.addBestPracticesRule(ToolingOperations.setClassReportDtls(node.getNode().getDefiningType().getApexName(), CategoryConstants.AVOID_DML_STATEMENTS_IN_LOOPS, "DML INSERT statement in Loop ",node.getBeginLine(),
					BPEnforcerConstants.HIGH, BPEnforcerConstants.DML_IN_LOOP,"MEDIUM",BPEnforcerConstants.PERFORMANCE));
            //addViolation(data, node);
        }
        return data;
    }

    @Override
    public Object visit(ASTDmlMergeStatement node, Object data) {
        if (insideLoop(node)) {
        	System.out.println("******AvoidDmlStatementsInLoopsCustomRule******");

        	//System.out.println(node.getNode().getDefiningType().getApexName());
        	//System.out.println(node.getBeginLine());
        	//System.out.println (node.getEndLine());
          	CodeAnalyser.addBestPracticesRule(ToolingOperations.setClassReportDtls(node.getNode().getDefiningType().getApexName(), CategoryConstants.AVOID_DML_STATEMENTS_IN_LOOPS, "DML MERGE statement in Loop ",node.getBeginLine(),
					BPEnforcerConstants.HIGH, BPEnforcerConstants.DML_IN_LOOP,"MEDIUM",BPEnforcerConstants.PERFORMANCE));
           // addViolation(data, node);
        }
        return data;
    }

    @Override
    public Object visit(ASTDmlUndeleteStatement node, Object data) {
        if (insideLoop(node)) {
        	System.out.println("******AvoidDmlStatementsInLoopsCustomRule******");

        	//System.out.println(node.getNode().getDefiningType().getApexName());
        	//System.out.println(node.getBeginLine());
        	//System.out.println (node.getEndLine());
          	CodeAnalyser.addBestPracticesRule(ToolingOperations.setClassReportDtls(node.getNode().getDefiningType().getApexName(), 59, "DML DELETE statement in Loop ",node.getBeginLine(),
					BPEnforcerConstants.HIGH, BPEnforcerConstants.DML_IN_LOOP,"MEDIUM",BPEnforcerConstants.PERFORMANCE));
           // addViolation(data, node);
        }
        return data;
    }

    @Override
    public Object visit(ASTDmlUpdateStatement node, Object data) {
        if (insideLoop(node)) {
        	System.out.println("******AvoidDmlStatementsInLoopsCustomRule******");

        	//System.out.println(node.getNode().getDefiningType().getApexName());
        	//System.out.println(node.getBeginLine());
        	//System.out.println (node.getEndLine());
       	CodeAnalyser.addBestPracticesRule(ToolingOperations.setClassReportDtls(node.getNode().getDefiningType().getApexName(), CategoryConstants.AVOID_DML_STATEMENTS_IN_LOOPS, "DML UPDATE statement in Loop ",node.getBeginLine(),
					BPEnforcerConstants.HIGH, BPEnforcerConstants.DML_IN_LOOP,"MEDIUM",BPEnforcerConstants.PERFORMANCE));
        
        	//addViolation(data, node);
        }
        return data;
    }

    @Override
    public Object visit(ASTDmlUpsertStatement node, Object data) {
        if (insideLoop(node)) {
        	System.out.println("******AvoidDmlStatementsInLoopsCustomRule******");

        	//System.out.println(node.getNode().getDefiningType().getApexName());
        	//System.out.println(node.getBeginLine());
        	//System.out.println (node.getEndLine());
        	
          	CodeAnalyser.addBestPracticesRule(ToolingOperations.setClassReportDtls(node.getNode().getDefiningType().getApexName(), CategoryConstants.AVOID_DML_STATEMENTS_IN_LOOPS, "DML update statement in Loop ",node.getBeginLine(),
					BPEnforcerConstants.HIGH, BPEnforcerConstants.DML_IN_LOOP,"MEDIUM",BPEnforcerConstants.PERFORMANCE));
            //addViolation(data, node);
        }
        return data;
    }

    private boolean insideLoop(AbstractNode node) {
        Node n = node.jjtGetParent();
      

        while (n != null) {
            if (n instanceof ASTDoLoopStatement || n instanceof ASTWhileLoopStatement
                    || n instanceof ASTForLoopStatement || n instanceof ASTForEachStatement) {
                return true;
            }
            n = n.jjtGetParent();
        }

        return false;
    }
}
