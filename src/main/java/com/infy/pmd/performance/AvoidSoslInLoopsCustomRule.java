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
import net.sourceforge.pmd.lang.apex.ast.ASTSoslExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTWhileLoopStatement;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.ast.Node;

public class AvoidSoslInLoopsCustomRule extends AbstractApexRule {

    public AvoidSoslInLoopsCustomRule() {
        setProperty(CODECLIMATE_CATEGORIES, "Performance");
        // Note: Often more complicated as just moving the SOSL a few lines.
        // Involves Maps...
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 150);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
    }

    @Override
    public Object visit(ASTSoslExpression node, Object data) {
        if (insideLoop(node) && parentNotReturn(node) && parentNotForEach(node)) {
        	System.out.println("******AvoidSoslInLoopsCustomRule******");

        	//System.out.println(node.getNode().getDefiningType().getApexName());
        	//System.out.println(node.getBeginLine());
        	//System.out.println (node.getEndLine());
        	CodeAnalyser.addBestPracticesRule(ToolingOperations.setClassReportDtls(node.getNode().getDefiningType().getApexName(), CategoryConstants.AVOID_SOSL_STATEMENTS_IN_LOOPS, "SOSL statement in Loop ",node.getBeginLine(),
					BPEnforcerConstants.HIGH, BPEnforcerConstants.SOSL_IN_LOOP,"MEDIUM",BPEnforcerConstants.PERFORMANCE));
        	
        	//addViolation(data, node);
        }
        return data;
    }

    private boolean parentNotReturn(ASTSoslExpression node) {
        return !(node.jjtGetParent() instanceof ASTReturnStatement);
    }

    private boolean parentNotForEach(ASTSoslExpression node) {
        return !(node.jjtGetParent() instanceof ASTForEachStatement);
    }

    private boolean insideLoop(ASTSoslExpression node) {
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
