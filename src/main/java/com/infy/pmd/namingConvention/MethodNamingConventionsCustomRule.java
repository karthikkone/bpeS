/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package com.infy.pmd.namingConvention;

import static apex.jorje.semantic.symbol.type.ModifierTypeInfos.OVERRIDE;

import java.util.ArrayList;

import com.infy.bpe.core.CodeAnalyser;
import com.infy.bpe.core.ToolingOperations;
import com.infy.report.model.ReportType;
import com.infy.services.model.CyclomaticRuleBean;
import com.infy.utility.BPEnforcerConstants;
import com.infy.utility.CategoryConstants;
import com.sforce.soap.tooling.sobject.ApexClass;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTProperty;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

public class MethodNamingConventionsCustomRule extends AbstractApexRule {

	public MethodNamingConventionsCustomRule() {

		setProperty(CODECLIMATE_CATEGORIES, new String[] { "Style" });
		// Note: x10 as Apex has not automatic refactoring
		setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 1);
		setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
	}

	public Object visit(ASTUserClass node, Object data) {
		return super.visit(node, data);
	}

	public Object visit(ASTMethod node, Object data) {

	
		if (isOverriddenMethod(node) || isPropertyAccessor(node) || isConstructor(node)) {
			return data;
		}

		String methodName = node.getImage();

		if (Character.isUpperCase(methodName.charAt(0))) {
			//System.out.println(node.getNode().getDefiningType().getApexName());
			//System.out.println(methodName);
		
			
			CodeAnalyser.addBestPracticesRule(ToolingOperations.setClassReportDtls(node.getNode().getDefiningType().getApexName(),CategoryConstants.METHOD_NAMING_CONVENTION,"PMD:Method names should not start with capital letters:"+methodName,node.getBeginLine(), 
					BPEnforcerConstants.LOW, BPEnforcerConstants.METHOD_NAMING_CONV,"MEDIUM",BPEnforcerConstants.CODESTYLE));
			//addViolationWithMessage(data, node, "Method names should not start with capital letters");
		}
		if (methodName.indexOf('_') >= 0) {
			//System.out.println(node.getNode().getDefiningType().getApexName());
			//System.out.println(methodName);
			CodeAnalyser.addBestPracticesRule(ToolingOperations.setClassReportDtls(node.getNode().getDefiningType().getApexName(),CategoryConstants.METHOD_NAMING_CONVENTION,"PMD:Method names should not start with capital letters:"+methodName,node.getBeginLine(), 
					BPEnforcerConstants.LOW, BPEnforcerConstants.METHOD_NAMING_CONV,"MEDIUM",BPEnforcerConstants.CODESTYLE));
			
			
			//addViolationWithMessage(data, node, "Method names should not contain underscores");
		}
		
		return data;
	}

	

	private boolean isOverriddenMethod(ASTMethod node) {
		return node.getNode().getModifiers().has(OVERRIDE);
	}

	private boolean isPropertyAccessor(ASTMethod node) {
		return (node.getParentsOfType(ASTProperty.class).size() > 0);
	}

	private boolean isConstructor(ASTMethod node) {
		return (node.getNode().getMethodInfo().isConstructor());
	}
}
