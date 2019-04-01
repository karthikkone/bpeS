package com.infy.pmd.designRules;/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

//package net.sourceforge.pmd.lang.apex.rule.design;

import static apex.jorje.semantic.symbol.type.ModifierTypeInfos.FINAL;
import static apex.jorje.semantic.symbol.type.ModifierTypeInfos.STATIC;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.infy.bpe.core.CodeAnalyser;
import com.infy.bpe.core.ToolingOperations;
import com.infy.utility.BPEnforcerConstants;
import com.infy.utility.CategoryConstants;

import net.sourceforge.pmd.lang.apex.ast.ASTField;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.properties.IntegerProperty;
import net.sourceforge.pmd.util.NumericConstants;

public class TooManyFieldsCustomRule extends AbstractApexRule {

    private static final int DEFAULT_MAXFIELDS = 5;

    private Map<String, Integer> stats;
    private Map<String, ASTUserClass> nodes;

    private static final IntegerProperty MAX_FIELDS_DESCRIPTOR = new IntegerProperty("maxfields",
            "Max allowable fields", 1, 300, DEFAULT_MAXFIELDS, 1.0f);

    public TooManyFieldsCustomRule() {
        definePropertyDescriptor(MAX_FIELDS_DESCRIPTOR);

        setProperty(CODECLIMATE_CATEGORIES, "Complexity");
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 200);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
    }

    @Override
    public Object visit(ASTUserClass node, Object data) {

        int maxFields = getProperty(MAX_FIELDS_DESCRIPTOR);

        stats = new HashMap<>(5);
        nodes = new HashMap<>(5);

        List<ASTField> l = node.findDescendantsOfType(ASTField.class);

        for (ASTField fd : l) {
            if (fd.getNode().getModifierInfo().all(FINAL, STATIC)) {
                continue;
            }
            ASTUserClass clazz = fd.getFirstParentOfType(ASTUserClass.class);
            if (clazz != null) {
                bumpCounterFor(clazz);
            }
        }
        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
            int val = entry.getValue();
        	System.out.println(val);

            Node n = nodes.get(entry.getKey());
            if (val > maxFields) {
               // addViolation(data, n);
//            	System.out.println(val);
//                System.out.println(node.getNode().getDefiningType().getApexName());
//                System.out.println(node.getBeginLine());
//                
            	CodeAnalyser.addBestPracticesRule(ToolingOperations.setClassReportDtls(node.getNode().getDefiningType().getApexName(), CategoryConstants.APEX_ASSERTS, "PMD: Classes that have too many fields can become unwieldy and could be redesigned to have fewer fields, possibly through grouping related fields in new objects.",node.getBeginLine(),BPEnforcerConstants.LOW, BPEnforcerConstants.TOO_MANY_FIELDS,"LOW",BPEnforcerConstants.DESIGN));

                
            }
        }
        return data;
    }

    private void bumpCounterFor(ASTUserClass clazz) {
        String key = clazz.getImage();
        if (!stats.containsKey(key)) {
            stats.put(key, NumericConstants.ZERO);
            nodes.put(key, clazz);
        }
        Integer i = Integer.valueOf(stats.get(key) + 1);
        stats.put(key, i);
    }
}
