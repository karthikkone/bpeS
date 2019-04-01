/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package com.infy.pmd.errorprone;

import java.util.List;

import com.infy.bpe.core.CodeAnalyser;
import com.infy.bpe.core.ToolingOperations;
import com.infy.utility.BPEnforcerConstants;
import com.infy.utility.CategoryConstants;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

public class MethodWithSameNameAsEnclosingClassCustomRule extends AbstractApexRule {

    public MethodWithSameNameAsEnclosingClassCustomRule() {
        setProperty(CODECLIMATE_CATEGORIES, "Style");
        // Note: x10 as Apex has not automatic refactoring
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 50);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
    }

    @Override
    public Object visit(ASTUserClass node, Object data) {
        String className = node.getImage();

        List<ASTMethod> methods = node.findDescendantsOfType(ASTMethod.class);

        for (ASTMethod m : methods) {
            String methodName = m.getImage();

            if (!m.getNode().getMethodInfo().isConstructor() && methodName.equalsIgnoreCase(className)) {
               
            	
            	//System.out.println(node.getNode().getDefiningType().getApexName());
               //System.out.println(node.getBeginLine());
               	//System.out.println (node.getEndLine());
               	CodeAnalyser.addBestPracticesRule(ToolingOperations.setClassReportDtls(node.getNode().getDefiningType().getApexName(), CategoryConstants.METHODAME_MATCHES_ENCLOSING_CLSNAME, "PMD:Non-constructor methods should not have the same name as the enclosing class",node.getBeginLine(),
       					BPEnforcerConstants.MEDIUM, BPEnforcerConstants.METHOD_SAMEAS_CONSTRUCTOR_NAME,"MEDIUM",BPEnforcerConstants.ERROR_PRONE));
            	// addViolation(data, m);
            }
        }

        return super.visit(node, data);
    }
}
