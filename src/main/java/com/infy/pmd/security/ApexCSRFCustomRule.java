/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package com.infy.pmd.security;
import com.infy.bpe.core.CodeAnalyser;
import com.infy.bpe.core.ToolingOperations;
import com.infy.utility.BPEnforcerConstants;
import com.infy.utility.CategoryConstants;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

/**
 * Constructor and init method might contain DML, which constitutes a CSRF
 * vulnerability
 * 
 * @author sergey.gorbaty
 *
 */
public class ApexCSRFCustomRule extends AbstractApexRule {
    public static final String INIT = "init";

    public ApexCSRFCustomRule() {
        setProperty(CODECLIMATE_CATEGORIES, "Security");
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 100);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
    }

    @Override
    public Object visit(ASTUserClass node, Object data) {
        if (Helper.isTestMethodOrClass(node) || Helper.isSystemLevelClass(node)) {
            return data; // stops all the rules
        }

        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTMethod node, Object data) {
        if (!Helper.isTestMethodOrClass(node)) {
            checkForCSRF(node, data);
        }
        return data;
    }

    /**
     * @param node
     * @param data
     */
    private void checkForCSRF(ASTMethod node, Object data) {
        if (node.getNode().getMethodInfo().isConstructor()) {
            if (Helper.foundAnyDML(node)) {
            	
            	//System.out.println(node.getNode().getDefiningType().getApexName());
            	//System.out.println(node.getBeginLine());
            	//System.out.println (node.getEndLine());
            	CodeAnalyser.addBestPracticesRule(ToolingOperations.setClassReportDtls(node.getNode().getDefiningType().getApexName(), CategoryConstants.APEXCSRF, "avoid making DML operations in Apex class constructor/init method ",node.getBeginLine(),BPEnforcerConstants.HIGH, BPEnforcerConstants.APEX_CSRF,"MEDIUM",BPEnforcerConstants.SECURITY));
            	
                //addViolation(data, node);
            }

        }

        String name = node.getNode().getMethodInfo().getName();
        if (name.equalsIgnoreCase(INIT)) {
            if (Helper.foundAnyDML(node)) {
            	//System.out.println(node.getNode().getDefiningType().getApexName());
            	//System.out.println(node.getBeginLine());
            	//System.out.println (node.getEndLine());
            	CodeAnalyser.addBestPracticesRule(ToolingOperations.setClassReportDtls(node.getNode().getDefiningType().getApexName(), CategoryConstants.APEXCSRF, "avoid making DML operations in Apex class constructor/init method",node.getBeginLine(),BPEnforcerConstants.HIGH, BPEnforcerConstants.APEX_CSRF,"MEDIUM",BPEnforcerConstants.SECURITY));
            	
                //addViolation(data, node);
            }
        }

    }
}
