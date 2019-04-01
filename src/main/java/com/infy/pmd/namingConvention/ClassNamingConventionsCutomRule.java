/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package com.infy.pmd.namingConvention;

import com.infy.bpe.core.CodeAnalyser;
import com.infy.bpe.core.ToolingOperations;
import com.infy.utility.BPEnforcerConstants;
import com.infy.utility.CategoryConstants;

import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTUserInterface;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

public class ClassNamingConventionsCutomRule extends AbstractApexRule {

    public ClassNamingConventionsCutomRule() {
        setProperty(CODECLIMATE_CATEGORIES, new String[] { "Style" });
        // Note: x10 as Apex has not automatic refactoring
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 5);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
    }

    public Object visit(ASTUserClass node, Object data) {
        if (Character.isLowerCase(node.getImage().charAt(0))) {
              
        	CodeAnalyser.addBestPracticesRule(ToolingOperations.setClassReportDtls(node.getNode().getDefiningType().getApexName(), CategoryConstants.CLASS_NAMING_CONVENTION, "PMD:Lowercase start of class Naming is not a good practice:"+node.getImage(),node.getBeginLine(),
					BPEnforcerConstants.LOW, BPEnforcerConstants.CLASS_NAMING_CONV,"MEDIUM",BPEnforcerConstants.CODESTYLE));
        	//(ApexClass apexComponent, Integer category, String issueDesc,String issueType, String sheetName)
            //addViolation(data, node);
        }
        return data;
    }

    public Object visit(ASTUserInterface node, Object data) {
        if (Character.isLowerCase(node.getImage().charAt(0))) {
        	
        	
        	CodeAnalyser.addBestPracticesRule(ToolingOperations.setClassReportDtls(node.getNode().getDefiningType().getApexName(), CategoryConstants.CLASS_NAMING_CONVENTION, "PMD:Lowercase start of class Naming is not a good practice:"+node.getImage(),node.getBeginLine(),
					BPEnforcerConstants.LOW, BPEnforcerConstants.CLASS_NAMING_CONV,"MEDIUM",BPEnforcerConstants.CODESTYLE));
            //addViolation(data, node);
        }
        return data;
    }
}
