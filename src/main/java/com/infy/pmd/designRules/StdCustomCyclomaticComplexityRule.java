/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package com.infy.pmd.designRules;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;

import com.infy.bpe.core.CodeAnalyser;
import com.infy.bpe.core.ToolingOperations;
import com.infy.services.model.CyclomaticRuleBean;
import com.infy.services.model.MethodDetailsBean;
import com.infy.utility.BPEnforcerConstants;

import net.sourceforge.pmd.lang.apex.ast.ASTBooleanExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTDoLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTForEachStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTForLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTIfBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTTernaryExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTTryCatchFinallyBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTUserEnum;
import net.sourceforge.pmd.lang.apex.ast.ASTUserInterface;
import net.sourceforge.pmd.lang.apex.ast.ASTUserTrigger;
import net.sourceforge.pmd.lang.apex.ast.ASTWhileLoopStatement;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.properties.BooleanProperty;
import net.sourceforge.pmd.properties.IntegerProperty;
import net.sourceforge.pmd.util.StringUtil;

/**
 * Implements the standard cyclomatic complexity rule
 * <p>
 * Standard rules: +1 for each decision point, but not including boolean
 * operators unlike CyclomaticComplexityRule.
 * 
 * @author ported on Java version of Alan Hohn, based on work by Donald A.
 *         Leckie
 * 
 * @since June 18, 2014
 */
public class StdCustomCyclomaticComplexityRule extends AbstractApexRule {

    public static final IntegerProperty REPORT_LEVEL_DESCRIPTOR = new IntegerProperty("reportLevel",
            "Cyclomatic Complexity reporting threshold", 1, 30, 10, 1.0f);

    public static final BooleanProperty SHOW_CLASSES_COMPLEXITY_DESCRIPTOR = new BooleanProperty(
            "showClassesComplexity", "Add class average violations to the report", true, 2.0f);

    public static final BooleanProperty SHOW_METHODS_COMPLEXITY_DESCRIPTOR = new BooleanProperty(
            "showMethodsComplexity", "Add method average violations to the report", true, 3.0f);

    private int reportLevel;
    private boolean showClassesComplexity = true;
    private boolean showMethodsComplexity = true;

    protected static class Entry {
        private int decisionPoints = 1;
        public int highestDecisionPoints;
        public int methodCount;

        private Entry(Node node) {
        }

        public void bumpDecisionPoints() {
            decisionPoints++;
        }

        public void bumpDecisionPoints(int size) {
            decisionPoints += size;
        }

        public int getComplexityAverage() {
            return (double) methodCount == 0 ? 1 : (int) Math.rint((double) decisionPoints / (double) methodCount);
        }
    }

    protected Stack<Entry> entryStack = new Stack<>();

    public StdCustomCyclomaticComplexityRule() {
        definePropertyDescriptor(REPORT_LEVEL_DESCRIPTOR);
        definePropertyDescriptor(SHOW_CLASSES_COMPLEXITY_DESCRIPTOR);
        definePropertyDescriptor(SHOW_METHODS_COMPLEXITY_DESCRIPTOR);

        setProperty(CODECLIMATE_CATEGORIES, new String[] { "Complexity" });
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 250);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
    }

    @Override
    public Object visit(ASTUserClass node, Object data) {
    	
    	
    
		
        reportLevel = getProperty(REPORT_LEVEL_DESCRIPTOR);
        showClassesComplexity = getProperty(SHOW_CLASSES_COMPLEXITY_DESCRIPTOR);
        showMethodsComplexity = getProperty(SHOW_METHODS_COMPLEXITY_DESCRIPTOR);
        entryStack.push(new Entry(node));
        super.visit(node, data);
        Entry classEntry = entryStack.pop();
        
        if (showClassesComplexity) {
        	
        
            if (classEntry.getComplexityAverage() >= reportLevel || classEntry.highestDecisionPoints >= reportLevel) {
            	//CodeAnalyser.addCycloMaticRule(new CyclomaticRuleBean(node.getNode().getDefiningType().getApexName(), classEntry.highestDecisionPoints,CodeAnalyser.getMapMethodDetails().get(node.getNode().getDefiningType().getApexName())));
            	String methodNames="";
            	List<MethodDetailsBean> methodDtlsLst = CodeAnalyser.getMapMethodDetails().get(node.getNode().getDefiningType().getApexName());
				if(null!=methodDtlsLst && methodDtlsLst.size()>0){
            		for (MethodDetailsBean  methodDetailsBean : methodDtlsLst) {
            			methodNames += methodDetailsBean.getMethodName()+",";
					}
            		methodNames = methodNames.substring(0,methodNames.length()-1);
            	}
               	CodeAnalyser.addBestPracticesRule(ToolingOperations.setCycloBeanDtls(node.getNode().getDefiningType().getApexName(), classEntry.highestDecisionPoints, "standard cyclomatic complexity index of apex class with method : "+methodNames+" : "+classEntry.highestDecisionPoints,classEntry.highestDecisionPoints,BPEnforcerConstants.MEDIUM, BPEnforcerConstants.CYCLO_COMPLEXITY,BPEnforcerConstants.DESIGN));

            }
        }
        return data;
    }

    @Override
    public Object visit(ASTUserTrigger node, Object data) {
        reportLevel = getProperty(REPORT_LEVEL_DESCRIPTOR);
        showClassesComplexity = getProperty(SHOW_CLASSES_COMPLEXITY_DESCRIPTOR);
        showMethodsComplexity = getProperty(SHOW_METHODS_COMPLEXITY_DESCRIPTOR);
        entryStack.push(new Entry(node));
        super.visit(node, data);
        Entry classEntry = entryStack.pop();
        if (showClassesComplexity) {
            if (classEntry.getComplexityAverage() >= reportLevel || classEntry.highestDecisionPoints >= reportLevel) {
            	System.out.println("The Trigger "+
                        classEntry.getComplexityAverage() + " (Highest = " + classEntry.highestDecisionPoints + ')'  );
           
            }
        }
        return data;
    }

    @Override
    public Object visit(ASTUserInterface node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTUserEnum node, Object data) {
        entryStack.push(new Entry(node));
        super.visit(node, data);
        Entry classEntry = entryStack.pop();
        if (classEntry.getComplexityAverage() >= reportLevel || classEntry.highestDecisionPoints >= reportLevel) {
        	 System.out.println("The class "+node.getNode().getDefiningType()+" has a Cyclomatic Complexity "+classEntry.getComplexityAverage()+" (Highest = " + classEntry.highestDecisionPoints + ')');
        	 
        }
        return data;
    }

    @Override
    public Object visit(ASTMethod node, Object data) {
        if (!node.getImage().matches("<clinit>|<init>|clone")) {
            entryStack.push(new Entry(node));
            super.visit(node, data);
            Entry methodEntry = entryStack.pop();
            int methodDecisionPoints = methodEntry.decisionPoints;
            Entry classEntry = entryStack.peek();
            classEntry.methodCount++;
            classEntry.bumpDecisionPoints(methodDecisionPoints);

            if (methodDecisionPoints > classEntry.highestDecisionPoints) {
                classEntry.highestDecisionPoints = methodDecisionPoints;
            }
            //System.out.println(node.getNode().getMethodInfo().getName());
            if (showMethodsComplexity && methodEntry.decisionPoints >= reportLevel) {
                String methodType = (node.getNode().getMethodInfo().isConstructor()) ? "constructor" : "method";
                String methodName =  (node.getNode().getMethodInfo().isConstructor()) ?node.getNode().getDefiningType().getApexName():node.getNode().getMethodInfo().getName();
                //System.out.println(" The "+methodType +" "+methodName+" has a Cyclomatic Complexity " +String.valueOf(methodEntry.decisionPoints));
                if(null!=node.getNode() && null!=node.getNode().getDefiningType() && null!=node.getNode().getDefiningType().getApexName()){
                List<MethodDetailsBean> methodDtlsLst = CodeAnalyser.getMapMethodDetails().get(node.getNode().getDefiningType().getApexName());
                if(null==methodDtlsLst){
                	methodDtlsLst = new ArrayList<MethodDetailsBean>();
                }
                methodDtlsLst.add(new MethodDetailsBean(methodName,methodEntry.decisionPoints));
                CodeAnalyser.getMapMethodDetails().put(node.getNode().getDefiningType().getApexName(),methodDtlsLst);
                }
            }
        }
        return data;
    }

    @Override
    public Object visit(ASTIfBlockStatement node, Object data) {
        entryStack.peek().bumpDecisionPoints();
        super.visit(node, data);
        return data;
    }

    @Override
    public Object visit(ASTTryCatchFinallyBlockStatement node, Object data) {
        entryStack.peek().bumpDecisionPoints();
        super.visit(node, data);
        return data;
    }

    @Override
    public Object visit(ASTForLoopStatement node, Object data) {
        entryStack.peek().bumpDecisionPoints();
        super.visit(node, data);
        return data;
    }

    @Override
    public Object visit(ASTForEachStatement node, Object data) {
        entryStack.peek().bumpDecisionPoints();
        super.visit(node, data);
        return data;
    }

    @Override
    public Object visit(ASTWhileLoopStatement node, Object data) {
        entryStack.peek().bumpDecisionPoints();
        super.visit(node, data);
        return data;
    }

    @Override
    public Object visit(ASTDoLoopStatement node, Object data) {
        entryStack.peek().bumpDecisionPoints();
        super.visit(node, data);
        return data;
    }

    @Override
    public Object visit(ASTTernaryExpression node, Object data) {
        entryStack.peek().bumpDecisionPoints();
        super.visit(node, data);
        return data;
    }

    @Override
    public Object visit(ASTBooleanExpression node, Object data) {
        return data;
    }
}
