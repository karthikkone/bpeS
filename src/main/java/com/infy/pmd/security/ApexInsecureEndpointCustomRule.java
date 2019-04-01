/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package com.infy.pmd.security;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import com.infy.bpe.core.CodeAnalyser;
import com.infy.bpe.core.ToolingOperations;
import com.infy.utility.BPEnforcerConstants;
import com.infy.utility.CategoryConstants;

import net.sourceforge.pmd.lang.apex.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTBinaryExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTLiteralExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableExpression;
import net.sourceforge.pmd.lang.apex.ast.AbstractApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

/**
 * Insecure HTTP endpoints passed to (req.setEndpoint)
 * req.setHeader('Authorization') should use named credentials
 * 
 * @author sergey.gorbaty
 *
 */
public class ApexInsecureEndpointCustomRule extends AbstractApexRule {
    private static final String SET_ENDPOINT = "setEndpoint";
    private static final Pattern PATTERN = Pattern.compile("^http://.+?$", Pattern.CASE_INSENSITIVE);

    private final Set<String> httpEndpointStrings = new HashSet<>();

    public ApexInsecureEndpointCustomRule() {
        setProperty(CODECLIMATE_CATEGORIES, "Security");
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 100);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
    }

    @Override
    public Object visit(ASTAssignmentExpression node, Object data) {
        findInsecureEndpoints(node);
        return data;
    }

    @Override
    public Object visit(ASTVariableDeclaration node, Object data) {
        findInsecureEndpoints(node);
        return data;
    }

    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        findInsecureEndpoints(node);
        return data;
    }

    private void findInsecureEndpoints(AbstractApexNode<?> node) {
        ASTVariableExpression variableNode = node.getFirstChildOfType(ASTVariableExpression.class);
        findInnerInsecureEndpoints(node, variableNode);

        ASTBinaryExpression binaryNode = node.getFirstChildOfType(ASTBinaryExpression.class);
        if (binaryNode != null) {
            findInnerInsecureEndpoints(binaryNode, variableNode);
        }

    }

    private void findInnerInsecureEndpoints(AbstractApexNode<?> node, ASTVariableExpression variableNode) {
        ASTLiteralExpression literalNode = node.getFirstChildOfType(ASTLiteralExpression.class);

        if (literalNode != null && variableNode != null) {
            Object o = literalNode.getNode().getLiteral();
            if (o instanceof String) {
                String literal = (String) o;
                if (PATTERN.matcher(literal).matches()) {
                    httpEndpointStrings.add(Helper.getFQVariableName(variableNode));
                }
            }
        }
    }

    @Override
    public Object visit(ASTMethodCallExpression node, Object data) {
        processInsecureEndpoint(node, data);
        return data;
    }

    private void processInsecureEndpoint(ASTMethodCallExpression node, Object data) {
        if (!Helper.isMethodName(node, SET_ENDPOINT)) {
            return;
        }

        ASTBinaryExpression binaryNode = node.getFirstChildOfType(ASTBinaryExpression.class);
        if (binaryNode != null) {
            runChecks(binaryNode, data);
        }

        runChecks(node, data);

    }

    private void runChecks(AbstractApexNode<?> node, Object data) {
        ASTLiteralExpression literalNode = node.getFirstChildOfType(ASTLiteralExpression.class);
        if (literalNode != null) {
            Object o = literalNode.getNode().getLiteral();
            if (o instanceof String) {
                String literal = (String) o;
                if (PATTERN.matcher(literal).matches()) {
                	//System.out.println(node.getNode().getDefiningType().getApexName());
                	//System.out.println(node.getBeginLine());
                	//System.out.println (node.getEndLine());
                	CodeAnalyser.addBestPracticesRule(ToolingOperations.setClassReportDtls(node.getNode().getDefiningType().getApexName(), CategoryConstants.APEX_INSECURE_ENDPOINT, "You should always use https for security ",node.getBeginLine(),BPEnforcerConstants.HIGH, BPEnforcerConstants.INSECURE_ENDPOINT,"MEDIUM",BPEnforcerConstants.SECURITY));
                	//addViolation(data, literalNode);
                }
            }
        }

        ASTVariableExpression variableNode = node.getFirstChildOfType(ASTVariableExpression.class);
        if (variableNode != null) {
            if (httpEndpointStrings.contains(Helper.getFQVariableName(variableNode))) {
            	//System.out.println(node.getNode().getDefiningType().getApexName());
            	//System.out.println(node.getBeginLine());
            	//System.out.println (node.getEndLine());
            	CodeAnalyser.addBestPracticesRule(ToolingOperations.setClassReportDtls(node.getNode().getDefiningType().getApexName(), CategoryConstants.APEX_INSECURE_ENDPOINT, "You should always use https for security ",node.getBeginLine(),BPEnforcerConstants.HIGH, BPEnforcerConstants.INSECURE_ENDPOINT,"MEDIUM",BPEnforcerConstants.SECURITY));
            	//  addViolation(data, variableNode);
            }

        }
    }
}
