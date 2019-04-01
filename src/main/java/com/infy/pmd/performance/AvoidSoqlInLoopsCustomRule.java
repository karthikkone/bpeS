/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package com.infy.pmd.performance;

import com.infy.bpe.core.CodeAnalyser;
import com.infy.bpe.core.ToolingOperations;
import com.infy.utility.BPEnforcerConstants;
import com.infy.utility.CategoryConstants;

import net.sourceforge.pmd.lang.apex.ast.ASTDoLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTForEachStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTForLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTSoqlExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTWhileLoopStatement;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.ast.Node;

public class AvoidSoqlInLoopsCustomRule extends AbstractApexRule {

    public AvoidSoqlInLoopsCustomRule() {
        setProperty(CODECLIMATE_CATEGORIES, "Performance");
        // Note: Often more complicated as just moving the SOQL a few lines.
        // Involves Maps...
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 150);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
    }

    @Override
    public Object visit(ASTSoqlExpression node, Object data) {
        if (insideLoop(node) && parentNotReturn(node) && parentNotForEach(node)) {

        	System.out.println("******AvoidSoqlInLoopsCustomRule******");

        	//System.out.println(node.getNode().getDefiningType().getApexName());
        	//System.out.println(node.getBeginLine());
        	//System.out.println (node.getEndLine());
           // addViolation(data, node);
        	CodeAnalyser.addBestPracticesRule(ToolingOperations.setClassReportDtls(node.getNode().getDefiningType().getApexName(), CategoryConstants.AVOID_SOQL_STATEMENTS_IN_LOOPS, "SOQL statement in Loop ",node.getBeginLine(),
					BPEnforcerConstants.HIGH, BPEnforcerConstants.SOQL_IN_LOOP,"MEDIUM",BPEnforcerConstants.PERFORMANCE));
        }
        return data;
    }

    private boolean parentNotReturn(ASTSoqlExpression node) {
        return !(node.jjtGetParent() instanceof ASTReturnStatement);
    }

    private boolean parentNotForEach(ASTSoqlExpression node) {
        return !(node.jjtGetParent() instanceof ASTForEachStatement);
    }

    private boolean insideLoop(ASTSoqlExpression node) {
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
