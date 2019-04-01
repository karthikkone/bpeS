package com.infy.bpe.core;

import java.util.ArrayList;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import com.infy.pmd.bestpractices.ApexUnitTestClassShouldHaveAssertsCustomRule;
import com.infy.pmd.bestpractices.ApexUnitTestShouldNotUseSeeAllDataTrueCustomRule;
import com.infy.pmd.bestpractices.AvoidGlobalModifierCustomRule;
import com.infy.pmd.bestpractices.AvoidLogicInTriggerCustomRule;
import com.infy.pmd.designRules.AvoidDeeplyNestedIfStmtsCustomRule;
import com.infy.pmd.designRules.CyclomaticComplexityCustomRule;
import com.infy.pmd.designRules.ExcessiveClassLengthRule;
import com.infy.pmd.designRules.ExcessiveParameterListRule;
import com.infy.pmd.designRules.ExcessivePublicCountRule;
import com.infy.pmd.designRules.StdCustomCyclomaticComplexityRule;
import com.infy.pmd.designRules.TooManyFieldsCustomRule;
import com.infy.pmd.errorprone.AvoidHardcodingIdCustomRule;
import com.infy.pmd.errorprone.MethodWithSameNameAsEnclosingClassCustomRule;
import com.infy.pmd.namingConvention.ClassNamingConventionsCutomRule;
import com.infy.pmd.namingConvention.MethodNamingConventionsCustomRule;
import com.infy.pmd.namingConvention.VariableNamingConventionsCustomRule;
import com.infy.pmd.performance.AvoidDmlStatementsInLoopsCustomRule;
import com.infy.pmd.performance.AvoidSoqlInLoopsCustomRule;
import com.infy.pmd.performance.AvoidSoslInLoopsCustomRule;
import com.infy.pmd.security.ApexBadCryptoCustomRule;
import com.infy.pmd.security.ApexCRUDViolationCustomRule;
import com.infy.pmd.security.ApexCSRFCustomRule;
import com.infy.pmd.security.ApexDangerousMethodsCustomRule;
import com.infy.pmd.security.ApexInsecureEndpointCustomRule;
import com.infy.pmd.security.ApexOpenRedirectCustomRule;
import com.infy.pmd.security.ApexSOQLInjectionCustomRule;
import com.infy.pmd.security.ApexSharingViolationsCustomRule;
import com.infy.pmd.security.ApexSuggestUsingNamedCredCustomRule;
import com.infy.pmd.security.ApexXSSFromEscapeFalseCustomRule;
import com.infy.pmd.security.ApexXSSFromURLParamCustomRule;
import com.infy.report.model.ReportType;
import com.infy.services.BPEnforcerContext;
import com.infy.services.impl.SFObjectWriter;
import com.infy.services.model.CyclomaticRuleBean;
import com.infy.services.model.HealthCheckParameter;
import com.infy.services.model.MethodDetailsBean;
import com.infy.utility.BPEnforcerConstants;
import com.sforce.soap.metadata.MetadataConnection;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.tooling.ToolingConnection;
import com.sforce.soap.tooling.sobject.ApexClass;
import com.sforce.soap.tooling.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;/*
										import com.sun.grizzly.http.SelectorThread;
										import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;
										*/
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.ParseException;

/*import com.infy.bpe.utility.DataStore;
*/
import com.infy.bpe.core.DataStore;
import com.infy.bpe.models.MetaData;
import com.infy.bpe.utility.Helper;
import com.sforce.soap.tooling.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

public class CodeAnalyser {

	static HashMap<String, ArrayList<String>> custPermProMap = new HashMap<String, ArrayList<String>>();

	static final int DEFAULT_PORT = 80;

	static final String PROXY_REQUIRED = "YES";

	static String proxyReqSetting;

	private static BPEnforcerContext bpEnorcerContext = new BPEnforcerContext();

	private static List<ReportType> reportList;

	private static Map<String, ReportType> apexClassModifiedDetails;

	private static List<CyclomaticRuleBean> cycloMaticRuleList;

	private static Map<String, List<MethodDetailsBean>> mapMethodDetails;
	

	public static String ValidateCredsViaLogin() throws ConnectionException {
		
		DataStore.API_VERSION=40.0;

		System.out.println("Establishing creds by connecting to salesforce org.");
		ConnectorConfig eConfig = getConfig();
		eConfig.setManualLogin(false);
		eConfig.setAuthEndpoint(DataStore.AUTHENDPOINT + "/services/Soap/c/" + DataStore.API_VERSION);
		System.out.println(DataStore.AUTHENDPOINT + "/services/Soap/c/" + DataStore.API_VERSION);
		DataStore.ent = com.sforce.soap.enterprise.Connector.newConnection(eConfig);
		
		return "success";
		
		
	}
	

	public static void startSelectiveScanSalesOrg(List<MetaData> components) throws ConnectionException, IOException {
	Properties props = readSettings();
	try {


		DataStore.OUTPUT_FILENAME = props.getProperty("pathToOutputFile");
		DataStore.GENERIC_USERID = props.getProperty("genericUserId");
		DataStore.BATCH_SIZE = Integer.parseInt(props.getProperty("batchSize"));
		DataStore.API_VERSION = Double.parseDouble(props.getProperty("apiVersion"));
		DataStore.AGILEPRO_USERNAME = props.getProperty("agileProUser");
		DataStore.AGILEPRO_PASSWORD = props.getProperty("agileProPwd");
		DataStore.AGILEPRO_AUTHENDPOINT = props.getProperty("agileProURL");
		DataStore.WAIT_TIME_MILLIS = Long.parseLong(props.getProperty("waitTimeMillis"));
		DataStore.MAX_NUM_POLL_REQUESTS = Integer.parseInt(props.getProperty("maxPolls"));
		DataStore.DASHBOARDCOMPONENTS = Integer.parseInt(props.getProperty("dashboardComponents"));
		DataStore.MAXADMINCOUNT = Integer.parseInt(props.getProperty("maxadmincount"));
		DataStore.MINUSERCOUNT = Integer.parseInt(props.getProperty("minusercount"));
	} catch (Exception e) {
		System.out.println("Exception in reading settings file or insufficient data in settings file");
	}
	ArrayList<ReportType> reportlist = new ArrayList<ReportType>();

	System.out.println("Establishing connection to salesforce org.");
	ConnectorConfig eConfig = getConfig();
	eConfig.setManualLogin(false);
	eConfig.setAuthEndpoint(DataStore.AUTHENDPOINT + "/services/Soap/c/" + DataStore.API_VERSION);
	System.out.println(DataStore.AUTHENDPOINT + "/services/Soap/c/" + DataStore.API_VERSION);
	DataStore.ent = com.sforce.soap.enterprise.Connector.newConnection(eConfig);
	
	
	

	
	

	// Partner and Tooling Connection
	ConnectorConfig pConfig = getConfig();
	pConfig.setManualLogin(false);
	pConfig.setAuthEndpoint(DataStore.AUTHENDPOINT + "/services/Soap/u/" + DataStore.API_VERSION);
	pConfig.setServiceEndpoint(eConfig.getServiceEndpoint().replaceAll("/c/", "/u/"));
	DataStore.pCon = com.sforce.soap.partner.Connector.newConnection(pConfig);
	
	
	

	com.sforce.soap.partner.LoginResult lrp = DataStore.pCon.login(DataStore.USERNAME, DataStore.PASSWORD);
	pConfig.setSessionId(lrp.getSessionId());
	pConfig.setServiceEndpoint(lrp.getServerUrl().replace("/u/", "/T/"));
	pConfig.setAuthEndpoint(pConfig.getAuthEndpoint().replace("/u/", "/T/"));

	ToolingConnection con = com.sforce.soap.tooling.Connector.newConnection(pConfig);
	
	
	DataStore.ORGANISATIONID = con.getUserInfo().getOrganizationId();
	DataStore.ORGANISATIONNAME = con.getUserInfo().getOrganizationName();


	System.out.println("Connected Successfully :::::::: ORG ID: " + DataStore.ORGANISATIONID + " :::: ORG NAME: "
			+ DataStore.ORGANISATIONNAME);

	
	
	//group components by type
	ArrayList<String> classComponents = new ArrayList<String>();
	ArrayList<String> pageComponents = new ArrayList<String>();
	ArrayList<String> triggerComponents = new ArrayList<String>();
	ArrayList<String> validationComponents = new ArrayList<String>();
	
	for (MetaData cmp : components) {
		if (cmp != null) {
			
			if (cmp.isApexClass()) {
				//classComponents = classComponents.add(cmp.getName());
				classComponents.add(cmp.getName());
			}
			
			if (cmp.isApexPage()) {
				//pageComponents= pageComponents+ cmp.getName();
				pageComponents.add(cmp.getName());
			}
			
			if (cmp.isApexTrigger()) {
				//triggerComponents= triggerComponents+ cmp.getName();
				triggerComponents.add(cmp.getName());
			}
			
			if (cmp.isValidationRule()) {
				//triggerComponents= validationComponents+ cmp.getName();
				triggerComponents.add(cmp.getName());
			}
			
		}
	}
	System.out.println("After testing values :" +classComponents);
	
	ArrayList<SObject> classes = ToolingOperations.retrieveIncrementalToolingComponents(con,"ApexClass",classComponents);

	System.out.println("Total number of classes - CodeAnalyser = "+classes.size());
		
	ArrayList<SObject> triggers = ToolingOperations.retrieveIncrementalToolingComponents(con,"ApexTrigger",triggerComponents );
	
	System.out.println("Total number of Triggers - CodeAnalyser = "+triggers.size());
	
	ArrayList<SObject> pages = ToolingOperations.retrieveIncrementalToolingComponents(con,"ApexPage",pageComponents );
	System.out.println("Total number of VFPages - CodeAnalyser = "+pages);
	
	
	//ArrayList<SObject> pages = ToolingOperations.retrieveToolingComponents(con, "ApexPage");
	// ArrayList<SObject> apexComponents =
	// ToolingOperations.retrieveToolingComponents(con, "ApexComponent");
	ArrayList<SObject> validationRules = ToolingOperations.retrieveIncrementalToolingComponents(con,"ValidationRule",validationComponents );
	System.out.println("Total number of VDrules - CodeAnalyser = "+validationRules.size());
	// ArrayList<SObject> workflowFieldUpdate =
	// ToolingOperations.retrieveToolingComponents(con, "WorkflowFieldUpdate");
	 //ArrayList<SObject> flowDtls =ToolingOperations.retrieveToolingComponents(con, "Flow");
	// ToolingOperations.multipleFieldUpdates(workflowFieldUpdate, triggers,
	// flowDtls); // commented by Anvesh
	ArrayList<SObject> totallist = new ArrayList<SObject>();
	totallist.addAll(classes);
	totallist.addAll(triggers);
	totallist.addAll(pages);
	// inactive workflow rules
	//ArrayList<SObject> workFlowRules = ToolingOperations.retrieveToolingComponents(con, "Workflowrule");

	// ############################## NEED TO CHECK -- NEVER DELETE
	// ##########################

	// reportlist.addAll(ToolingOperations.checkFutureTag(classes));
	// reportlist.addAll(ToolingOperations.methodDecl(classes));
	// ############################## NEED TO CHECK##########################

	// ORG health parameters
	HealthCheckParameter healthCheckParameter = new HealthCheckParameter();
	// if (!isHerokuInvoke) {

	pConfig.setServiceEndpoint(lrp.getServerUrl().replace("/u/", "/m/"));
	/*
	 * MetadataConnection mcon =
	 * com.sforce.soap.metadata.Connector.newConnection(pConfig); DataStore.mCon =
	 * mcon;
	 */
		

	 //reportlist.addAll(EnterpriseOperations.inactiveuser());
//
	//	reportlist.addAll(EnterpriseOperations.scheduledJobs());
//		reportlist.addAll(EnterpriseOperations.dashboard());
			
			
		//reportlist.addAll(ToolingOperations.inactiveWorkFlowRule(workFlowRules));
	//if(DATA)
			int count = ToolingOperations.totalNumOfLines(classes,triggers);
			System.out.println("This is the final count:" +count);
			DataStore.TOTALLINESOFORG= count;
	
			if (DataStore.BESTPRACTICES.equalsIgnoreCase("true")) {
				// reportlist.addAll(EnterpriseOperations.inactiveuser());

				// reportlist.addAll(EnterpriseOperations.scheduledJobs());
				// reportlist.addAll(EnterpriseOperations.dashboard());
				// reportlist.addAll(ToolingOperations.inactiveWorkFlowRule(workFlowRules));
				reportlist.addAll(ToolingOperations.triggerNameCheck(triggers));
				reportlist.addAll(ToolingOperations.findSystemDebugCount(classes));
				reportlist.addAll(ToolingOperations.inactiveValidationRule(validationRules));
				reportlist.addAll(ToolingOperations.findTest(classes));
				reportlist.addAll(ToolingOperations.errorhandling(classes));
				reportlist.addAll(ToolingOperations.annotationbetweencomments(classes));
				reportlist.addAll(ToolingOperations.Presence_Of_Doctype_VF(pages));
				reportlist.addAll(ToolingOperations.checkinlinejs(pages));
				reportlist.addAll(ToolingOperations.checkinlinecss(pages));
				reportlist.addAll(ToolingOperations.checkSelectStatement(classes));
				reportlist.addAll(ToolingOperations.multipletriggers(triggers));
				reportlist.addAll(ToolingOperations.findID(classes));
				startBestPracticesScan(classes, triggers); // all d 4 rules PMD BP

			}

			if (DataStore.DESIGN.equalsIgnoreCase("true")) {

				startCyclomaticScan(classes);

			}

			if (DataStore.CODESTYLE.equalsIgnoreCase("true")) {

				reportlist.addAll(ToolingOperations.checkNoOfLines(classes));

				startNamingConventionRuleScan(classes); // 3 rules from

			}
			if (DataStore.ERRORPRONE.equalsIgnoreCase("true")) {

				reportlist.addAll(ToolingOperations.checkDmlInTry(triggers));

				reportlist.addAll(ToolingOperations.checkInActiveTrigger(triggers));
				reportlist.addAll(ToolingOperations.findSystemRunas(classes));
				reportlist.addAll(ToolingOperations.findSystemAssert(classes));

				startErrorProneCheckScan(classes); // 2 rules from PMD ERRprone 6 PENDING

			}

			if (DataStore.PERFORMANCE.equalsIgnoreCase("true")) {

				reportlist.addAll(ToolingOperations.findSOQL(classes));

				reportlist.addAll(ToolingOperations.checkRecordTypeInfo(classes));

				startPerformanceRuleScan(classes); // all 3 rules 4m PMD PERFORMANCE

				reportlist.addAll(ToolingOperations.sharingclasses(classes));

			}

			if (DataStore.SECURITY.equalsIgnoreCase("true")) {

				startSecurityRulesScan(classes); // all 10 rules PMD SECURITY

			}

	

	// PMDCODESTYLE -4p
	if (null!=reportList)
	reportlist.addAll(reportList);
	System.out.println("############## " + reportlist.size());
	

	// bpEnorcerContext.setBpOutputGenerator(isHerokuInvoke?new
	// SFObjectWriter(DataStore.ORGANISATIONID,DataStore.ORGANISATIONNAME,DataStore.REMOTEAUTHNAME):new
	// ExcelOutputGenerator());
	bpEnorcerContext.setBpOutputGenerator(
			new SFObjectWriter(DataStore.ORGANISATIONID, DataStore.ORGANISATIONNAME, DataStore.REMOTEAUTHNAME));
	bpEnorcerContext.setHealthCheckParameter(healthCheckParameter);
	bpEnorcerContext.createOutput(reportlist);

	// return reportlist;
}
	
	private static ConnectorConfig getConfig() {
		ConnectorConfig config = new ConnectorConfig();
		config.setUsername(DataStore.USERNAME);
		config.setPassword(DataStore.PASSWORD);

		/*
		 * if (PROXY_REQUIRED.equalsIgnoreCase(proxyReqSetting)) {
		 * config.setProxy(DataStore.HOST, DataStore.PORT);
		 * config.setProxyPassword(DataStore.PROXY_PASSWORD);
		 * config.setProxyUsername(DataStore.PROXY_USERNAME); }
		 */
		return config;
	}

	private static Properties readSettings() throws IOException {
		Properties props = new Properties();
		InputStream is = null;

		try {
			File f = new File("settings.properties");
			is = new FileInputStream(f);

		} catch (Exception e) {
			is = null;
			System.out.println("Error while retrieving Settings: " + e.getMessage());
			e.printStackTrace();
		}

		try {

			props.load(is);
		} catch (Exception e) {
			System.out.println("Error while retrieving Settings: " + e.getMessage());
			e.printStackTrace();
		}
		System.out.println("Retrieving configuration from settings.properties...");
		is.close();
		return props;
	}

	/**
	 * check if the string is numeric
	 * 
	 * @param str
	 * @return boolean
	 */
	public static boolean isNumeric(String str) {
		if (str == null || str.isEmpty()) {
			return false;
		}
		return str.matches("\\d+");
	}

	/**
	 * check if string is null or empty
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		if (str == null || str.isEmpty()) {
			return true;
		}
		return false;
	}

	/**
	 * @return the mapMethodDetails
	 */
	public static Map<String, List<MethodDetailsBean>> getMapMethodDetails() {
		if (null == mapMethodDetails) {
			mapMethodDetails = new HashMap<String, List<MethodDetailsBean>>();
		}
		return mapMethodDetails;
	}

	/**
	 * @param mapMethodDetails
	 *            the mapMethodDetails to set
	 */
	public static void setMapMethodDetails(String className, List<MethodDetailsBean> methodDetailsBeans) {
		if (null == mapMethodDetails) {
			mapMethodDetails = new HashMap<String, List<MethodDetailsBean>>();
		}
		mapMethodDetails.put(className, methodDetailsBeans);
	}

	public static void addBestPracticesRule(ReportType reportType) {
		if (null == reportList) {
			reportList = new ArrayList<>();
		}
		reportList.add(reportType);

	}

	/**
	 * @param cycloMaticRuleList
	 *            the cycloMaticRuleList to set
	 */
	public static void setCycloMaticRuleList(List<CyclomaticRuleBean> cycloMaticRuleList) {
		CodeAnalyser.cycloMaticRuleList = cycloMaticRuleList;
	}

	/**
	 * @return the apexClassModifiedDetails
	 */
	public static Map<String, ReportType> getApexClassModifiedDetails() {
		if (null == apexClassModifiedDetails) {
			apexClassModifiedDetails = new HashMap<String, ReportType>();
		}
		return apexClassModifiedDetails;
	}

	/**
	 * @param cycloMaticRuleList
	 *            the cycloMaticRuleList to set
	 */
	public static void addCycloMaticRule(CyclomaticRuleBean cycloMaticRule) {
		if (null == cycloMaticRuleList) {
			cycloMaticRuleList = new ArrayList<>();
		}
		cycloMaticRuleList.add(cycloMaticRule);
	}

	private static void startCyclomaticScan(List<SObject> classes) {

		Node node;
		ApexClass apexComponent;
		System.out.println("**********DESIGN - PMD**********");
		try {

			StdCustomCyclomaticComplexityRule rule1 = new StdCustomCyclomaticComplexityRule();
			//added recently to check
			/*
			AvoidDeeplyNestedIfStmtsCustomRule rule2 = new AvoidDeeplyNestedIfStmtsCustomRule();
			CyclomaticComplexityCustomRule rule3 = new CyclomaticComplexityCustomRule();
			ExcessiveClassLengthRule rule4 = new ExcessiveClassLengthRule();
			ExcessiveParameterListRule rule5 = new ExcessiveParameterListRule();
			ExcessivePublicCountRule rule6 = new ExcessivePublicCountRule();
			TooManyFieldsCustomRule rule7 = new TooManyFieldsCustomRule();
			*/
			
			
			RuleContext ruleCtx = new RuleContext();
			LanguageVersion javaLanguageVersion = LanguageRegistry.getLanguage("Apex").getDefaultVersion();
			ParserOptions parserOptions = javaLanguageVersion.getLanguageVersionHandler().getDefaultParserOptions();
			Parser parser = javaLanguageVersion.getLanguageVersionHandler().getParser(parserOptions);

			for (SObject c : classes) {
				apexComponent = null;
				if (c instanceof ApexClass) {
					apexComponent = ((ApexClass) c);
					if (!BPEnforcerConstants.BODY_CODE.equalsIgnoreCase(apexComponent.getBody())
							&& apexComponent.getBody() != null) {
						node = parser.parse(apexComponent.getName(), new StringReader(apexComponent.getBody()));
						
						rule1.apply(Arrays.asList(node), ruleCtx);
						
						//recently added to check
						/*
						rule2.apply(Arrays.asList(node), ruleCtx);
						rule3.apply(Arrays.asList(node), ruleCtx);
						rule4.apply(Arrays.asList(node), ruleCtx);
						//rule5.apply(Arrays.asList(node), ruleCtx);
						//rule6.apply(Arrays.asList(node), ruleCtx);
						rule7.apply(Arrays.asList(node), ruleCtx);
						*/
					}
				}

			}
		} catch (ParseException e) {
			System.err.println("Exception caused due to" + e.getLocalizedMessage());
			e.printStackTrace();
		}

	}

	private static void startNamingConventionRuleScan(List<SObject> classes) {

		Node node;
		ApexClass apexComponent;
		System.out.println("**********NAMING CONVENTIONS FROM CODESTYLE - PMD**********");
		try {
			ClassNamingConventionsCutomRule rule1 = new ClassNamingConventionsCutomRule();
			//VariableNamingConventionsCustomRule rule2 = new VariableNamingConventionsCustomRule();
			// rule3 = new MethodNamingConventionsCustomRule();

			RuleContext ruleCtx = new RuleContext();
			LanguageVersion javaLanguageVersion = LanguageRegistry.getLanguage("Apex").getDefaultVersion();
			ParserOptions parserOptions = javaLanguageVersion.getLanguageVersionHandler().getDefaultParserOptions();
			Parser parser = javaLanguageVersion.getLanguageVersionHandler().getParser(parserOptions);

			for (SObject c : classes) {
				apexComponent = null;
				if (c instanceof ApexClass) {
					apexComponent = ((ApexClass) c);
					if (!BPEnforcerConstants.BODY_CODE.equalsIgnoreCase(apexComponent.getBody())) {

						node = parser.parse(apexComponent.getName(), new StringReader(apexComponent.getBody()));
						rule1.apply(Arrays.asList(node), ruleCtx);
						//rule2.apply(Arrays.asList(node), ruleCtx);
						//rule3.apply(Arrays.asList(node), ruleCtx);
					}
				}
			}
		} catch (ParseException e) {
			System.err.println("Exception caused due to" + e.getLocalizedMessage());
			e.printStackTrace();
		}

	}

	private static void startPerformanceRuleScan(List<SObject> classes) {
		Node node;
		ApexClass apexComponent;
		System.out.println("**********PERFORMANCE SCAN - PMD**********");
		try {
			AvoidDmlStatementsInLoopsCustomRule rule1 = new AvoidDmlStatementsInLoopsCustomRule();
			AvoidSoqlInLoopsCustomRule rule2 = new AvoidSoqlInLoopsCustomRule();
			AvoidSoslInLoopsCustomRule rule3 = new AvoidSoslInLoopsCustomRule();

			RuleContext ruleCtx = new RuleContext();
			LanguageVersion javaLanguageVersion = LanguageRegistry.getLanguage("Apex").getDefaultVersion();
			ParserOptions parserOptions = javaLanguageVersion.getLanguageVersionHandler().getDefaultParserOptions();
			Parser parser = javaLanguageVersion.getLanguageVersionHandler().getParser(parserOptions);

			for (SObject c : classes) {
				apexComponent = null;
				if (c instanceof ApexClass) {
					apexComponent = ((ApexClass) c);
					if (!BPEnforcerConstants.BODY_CODE.equalsIgnoreCase(apexComponent.getBody())) {

						node = parser.parse(apexComponent.getName(), new StringReader(apexComponent.getBody()));
						rule1.apply(Arrays.asList(node), ruleCtx);
						rule2.apply(Arrays.asList(node), ruleCtx);
						rule3.apply(Arrays.asList(node), ruleCtx);

					}
				}

			}
		} catch (ParseException e) {
			System.err.println("Exception caused due to" + e.getLocalizedMessage());
			e.printStackTrace();
		}

	}

	private static void startErrorProneCheckScan(List<SObject> classes) {

		Node node;
		ApexClass apexComponent;
		System.out.println("**********ERROR PRONE SCAN - PMD**********");
		try {
			AvoidHardcodingIdCustomRule rule = new AvoidHardcodingIdCustomRule();
			MethodWithSameNameAsEnclosingClassCustomRule rule2 = new MethodWithSameNameAsEnclosingClassCustomRule();

			RuleContext ruleCtx = new RuleContext();
			LanguageVersion javaLanguageVersion = LanguageRegistry.getLanguage("Apex").getDefaultVersion();
			ParserOptions parserOptions = javaLanguageVersion.getLanguageVersionHandler().getDefaultParserOptions();
			Parser parser = javaLanguageVersion.getLanguageVersionHandler().getParser(parserOptions);

			for (SObject c : classes) {
				apexComponent = null;
				if (c instanceof ApexClass) {
					apexComponent = ((ApexClass) c);

					if (!BPEnforcerConstants.BODY_CODE.equalsIgnoreCase(apexComponent.getBody())) {
						node = parser.parse(apexComponent.getName(), new StringReader(apexComponent.getBody()));
						rule.apply(Arrays.asList(node), ruleCtx);
						rule2.apply(Arrays.asList(node), ruleCtx);

					}
				}

			}

		} catch (ParseException e) {
			System.err.println("Exception caused due to" + e.getLocalizedMessage());
			e.printStackTrace();
		}

	}

	private static void startSecurityRulesScan(List<SObject> classes) {

		Node node;
		ApexClass apexComponent;
		System.out.println("**********SECURITY SCAN - PMD**********");
		try {
			ApexBadCryptoCustomRule rule = new ApexBadCryptoCustomRule();
			ApexCRUDViolationCustomRule rule1 = new ApexCRUDViolationCustomRule();
			ApexCSRFCustomRule rule2 = new ApexCSRFCustomRule();
			ApexDangerousMethodsCustomRule rule3 = new ApexDangerousMethodsCustomRule();
			ApexInsecureEndpointCustomRule rule4 = new ApexInsecureEndpointCustomRule();
			ApexOpenRedirectCustomRule rule5 = new ApexOpenRedirectCustomRule();
			ApexSharingViolationsCustomRule rule6 = new ApexSharingViolationsCustomRule();
			ApexSOQLInjectionCustomRule rule7 = new ApexSOQLInjectionCustomRule();
			ApexSuggestUsingNamedCredCustomRule rule8 = new ApexSuggestUsingNamedCredCustomRule();
			ApexXSSFromEscapeFalseCustomRule rule9 = new ApexXSSFromEscapeFalseCustomRule();
			ApexXSSFromURLParamCustomRule rule10 = new ApexXSSFromURLParamCustomRule();

			RuleContext ruleCtx = new RuleContext();
			LanguageVersion javaLanguageVersion = LanguageRegistry.getLanguage("Apex").getDefaultVersion();
			ParserOptions parserOptions = javaLanguageVersion.getLanguageVersionHandler().getDefaultParserOptions();
			Parser parser = javaLanguageVersion.getLanguageVersionHandler().getParser(parserOptions);

			for (SObject c : classes) {
				apexComponent = null;
				if (c instanceof ApexClass) {
					apexComponent = ((ApexClass) c);
					if (!BPEnforcerConstants.BODY_CODE.equalsIgnoreCase(apexComponent.getBody())) {

						node = parser.parse(apexComponent.getName(), new StringReader(apexComponent.getBody()));

						rule.apply(Arrays.asList(node), ruleCtx);
						rule1.apply(Arrays.asList(node), ruleCtx);
						rule2.apply(Arrays.asList(node), ruleCtx);
						rule3.apply(Arrays.asList(node), ruleCtx);
						rule4.apply(Arrays.asList(node), ruleCtx);
						rule5.apply(Arrays.asList(node), ruleCtx);
						rule6.apply(Arrays.asList(node), ruleCtx);
						rule7.apply(Arrays.asList(node), ruleCtx);
						rule8.apply(Arrays.asList(node), ruleCtx);
						rule9.apply(Arrays.asList(node), ruleCtx);
						rule10.apply(Arrays.asList(node), ruleCtx);

					}
				}

			}
		} catch (ParseException e) {
			System.err.println("Exception caused due to" + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	private static void startBestPracticesScan(List<SObject> classes, List<SObject> triggers) {

		Node node;
		ApexClass apexComponent;
		System.out.println("BEST PRACTICES SCAN - PMD");
		try {

			AvoidLogicInTriggerCustomRule rule1 = new AvoidLogicInTriggerCustomRule();
			AvoidGlobalModifierCustomRule rule2 = new AvoidGlobalModifierCustomRule();
			ApexUnitTestClassShouldHaveAssertsCustomRule rule3 = new ApexUnitTestClassShouldHaveAssertsCustomRule();
			ApexUnitTestShouldNotUseSeeAllDataTrueCustomRule rule4 = new ApexUnitTestShouldNotUseSeeAllDataTrueCustomRule();

			RuleContext ruleCtx = new RuleContext();
			LanguageVersion javaLanguageVersion = LanguageRegistry.getLanguage("Apex").getDefaultVersion();
			ParserOptions parserOptions = javaLanguageVersion.getLanguageVersionHandler().getDefaultParserOptions();
			Parser parser = javaLanguageVersion.getLanguageVersionHandler().getParser(parserOptions);

			for (SObject c : classes) {
				apexComponent = null;
				if (c instanceof ApexClass) {
					apexComponent = ((ApexClass) c);

					if (!BPEnforcerConstants.BODY_CODE.equalsIgnoreCase(apexComponent.getBody())) {

						node = parser.parse(apexComponent.getName(), new StringReader(apexComponent.getBody()));

						rule2.apply(Arrays.asList(node), ruleCtx);
						rule3.apply(Arrays.asList(node), ruleCtx);
						rule4.apply(Arrays.asList(node), ruleCtx);

					}
				}

			}

			for (SObject c : triggers) {
				apexComponent = null;
				if (c instanceof ApexClass) {
					apexComponent = ((ApexClass) c);
					if (!BPEnforcerConstants.BODY_CODE.equalsIgnoreCase(apexComponent.getBody())) {

						node = parser.parse(apexComponent.getName(), new StringReader(apexComponent.getBody()));
						rule1.apply(Arrays.asList(node), ruleCtx);

					}
				}

			}

		} catch (ParseException e) {
			System.err.println("Exception caused due to" + e.getLocalizedMessage());
			e.printStackTrace();
		}

	}

	// ArrayList<SObject>
	// classes=ToolingOperations.retrieveToolingComponents(DataStore.TOOLING,
	// "ApexClass");
	// System.out.println("Number of classes retrieved: "+classes.size());

}
