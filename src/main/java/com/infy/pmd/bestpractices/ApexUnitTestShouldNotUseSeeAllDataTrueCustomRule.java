/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package com.infy.pmd.bestpractices;
import com.infy.bpe.core.CodeAnalyser;
import com.infy.bpe.core.ToolingOperations;
import com.infy.utility.BPEnforcerConstants;
import com.infy.utility.CategoryConstants;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTModifierNode;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexUnitTestRule;

import apex.jorje.semantic.ast.modifier.Annotation;
import apex.jorje.semantic.ast.modifier.AnnotationParameter;
import apex.jorje.semantic.ast.modifier.ModifierOrAnnotation;
import apex.jorje.semantic.symbol.type.AnnotationTypeInfos;
import apex.jorje.semantic.symbol.type.ModifierOrAnnotationTypeInfo;
import apex.jorje.semantic.symbol.type.TypeInfoEquivalence;
import apex.jorje.services.Version;

/**
 * <p>
 * It's a very bad practice to use @isTest(seeAllData=true) in Apex unit tests,
 * because it opens up the existing database data for unexpected modification by
 * tests.
 * </p>
 *
 * @author a.subramanian
 */
public class ApexUnitTestShouldNotUseSeeAllDataTrueCustomRule extends AbstractApexUnitTestRule {

    @Override
    public Object visit(final ASTUserClass node, final Object data) {
        // @isTest(seeAllData) was introduced in v24, and was set to false by
        // default
        final Version classApiVersion = node.getNode().getDefiningType().getCodeUnitDetails().getVersion();

        if (!isTestMethodOrClass(node) && classApiVersion.isGreaterThan(Version.V174)) {
            return data;
        }

        checkForSeeAllData(node, data);
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTMethod node, Object data) {
        if (!isTestMethodOrClass(node)) {
            return data;
        }

        return checkForSeeAllData(node, data);
    }

    private Object checkForSeeAllData(final ApexNode<?> node, final Object data) {
        final ASTModifierNode modifierNode = node.getFirstChildOfType(ASTModifierNode.class);

        if (modifierNode != null) {
            for (final ModifierOrAnnotationTypeInfo modifierOrAnnotationTypeInfo : modifierNode.getNode().getModifiers().all()) {
                ModifierOrAnnotation modifierOrAnnotation = modifierNode.getNode().getModifiers().get(modifierOrAnnotationTypeInfo);
             if (modifierOrAnnotation instanceof Annotation && TypeInfoEquivalence
                        .isEquivalent(modifierOrAnnotationTypeInfo, AnnotationTypeInfos.IS_TEST)) {
                    final Annotation annotation = (Annotation) modifierOrAnnotation;
                    final AnnotationParameter parameter = annotation.getParameter("seeAllData");
                    
                    if (parameter != null && parameter.getBooleanValue() == true) {
                    	System.out.println("**********ApexUnitTestShouldNotUseSeeAllDataTrueCustomRule**********");
                                     	
                    	CodeAnalyser.addBestPracticesRule(ToolingOperations.setClassReportDtls(node.getNode().getDefiningType().getApexName(), CategoryConstants.SEEALL_DATA_TRUE, "PMD:Apex Unit test should not use SeeAllDataTrue ",node.getBeginLine(),BPEnforcerConstants.MEDIUM, BPEnforcerConstants.SEE_ALL_DATA,"MEDIUM",BPEnforcerConstants.BEST_PRACTICES));
                    	 System.out.println("vem");
                    	//addViolation(data, node);
                        return data;
                    }
                }
            }
        }

        return data;
    }
}
