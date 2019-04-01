/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package com.infy.pmd.security;

import java.util.List;

import com.infy.bpe.core.CodeAnalyser;
import com.infy.bpe.core.ToolingOperations;
import com.infy.utility.BPEnforcerConstants;
import com.infy.utility.CategoryConstants;

import net.sourceforge.pmd.lang.apex.ast.ASTLiteralExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableExpression;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

/**
 * Finds all .addError method calls that are not HTML escaped on purpose
 * 
 * @author sergey.gorbaty
 *
 */
public class ApexXSSFromEscapeFalseCustomRule extends AbstractApexRule {
    private static final String ADD_ERROR = "addError";

    public ApexXSSFromEscapeFalseCustomRule() {
        setProperty(CODECLIMATE_CATEGORIES, "Security");
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 100);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
    }

    @Override
    public Object visit(ASTUserClass node, Object data) {
        if (Helper.isTestMethodOrClass(node) || Helper.isSystemLevelClass(node)) {
            return data; // stops all the rules
        }

        List<ASTMethodCallExpression> methodCalls = node.findDescendantsOfType(ASTMethodCallExpression.class);
        for (ASTMethodCallExpression methodCall : methodCalls) {
            if (Helper.isMethodName(methodCall, ADD_ERROR)) {
                validateBooleanParameter(methodCall, data);
            }
        }
        return data;
    }

    private void validateBooleanParameter(ASTMethodCallExpression methodCall, Object data) {
        int numberOfChildren = methodCall.jjtGetNumChildren();
        if (numberOfChildren == 3) { // addError('',false)
            Object potentialLiteral = methodCall.jjtGetChild(2);
            if (potentialLiteral instanceof ASTLiteralExpression) {
                ASTLiteralExpression parameter = (ASTLiteralExpression) potentialLiteral;
                Object o = parameter.getNode().getLiteral();
                if (o instanceof Boolean) {
                    Boolean paramValue = (Boolean) o;
                    if (paramValue.equals(Boolean.FALSE)) {
                        validateLiteralPresence(methodCall, data);
                    }
                }
            }
        }
    }

    private void validateLiteralPresence(ASTMethodCallExpression methodCall, Object data) {
        List<ASTVariableExpression> variables = methodCall.findDescendantsOfType(ASTVariableExpression.class);
        for (ASTVariableExpression v : variables) {
        	//System.out.println(v);
        	//System.out.println(methodCall.getNode().getDefiningType().getApexName());
        	//System.out.println(methodCall.getBeginLine());
        	//System.out.println (methodCall.getEndLine());
        	CodeAnalyser.addBestPracticesRule(ToolingOperations.setClassReportDtls(methodCall.getNode().getDefiningType().getApexName(), CategoryConstants.APEX_XSS_FROM_ESCAPE_FALSE, "Reported call to addError with disabled escaping",methodCall.getBeginLine(),BPEnforcerConstants.HIGH, BPEnforcerConstants.APEX_XSS_ESCAPE,"MEDIUM",BPEnforcerConstants.SECURITY));
        	//addViolation(data, v);
        }
    }

}
