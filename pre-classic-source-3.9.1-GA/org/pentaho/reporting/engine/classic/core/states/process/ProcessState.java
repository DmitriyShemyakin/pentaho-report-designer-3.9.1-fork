/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2001 - 2009 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.states.process;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.CrosstabGroup;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.ReportParameterValidationException;
import org.pentaho.reporting.engine.classic.core.ReportPreProcessor;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.cache.CachingDataFactory;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.filter.types.ExternalElementType;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.function.StructureFunction;
import org.pentaho.reporting.engine.classic.core.function.sys.AttributeExpressionsEvaluator;
import org.pentaho.reporting.engine.classic.core.function.sys.CellFormatFunction;
import org.pentaho.reporting.engine.classic.core.function.sys.MetaDataStyleEvaluator;
import org.pentaho.reporting.engine.classic.core.function.sys.SheetNameFunction;
import org.pentaho.reporting.engine.classic.core.function.sys.StyleExpressionsEvaluator;
import org.pentaho.reporting.engine.classic.core.function.sys.WizardItemHideFunction;
import org.pentaho.reporting.engine.classic.core.layout.InlineSubreportMarker;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.parameters.DefaultParameterContext;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterDefinition;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterValidator;
import org.pentaho.reporting.engine.classic.core.parameters.ValidationResult;
import org.pentaho.reporting.engine.classic.core.states.DataFactoryManager;
import org.pentaho.reporting.engine.classic.core.states.DefaultGroupingState;
import org.pentaho.reporting.engine.classic.core.states.FunctionStorage;
import org.pentaho.reporting.engine.classic.core.states.FunctionStorageKey;
import org.pentaho.reporting.engine.classic.core.states.GroupStartRecord;
import org.pentaho.reporting.engine.classic.core.states.GroupingState;
import org.pentaho.reporting.engine.classic.core.states.IgnoreEverythingReportErrorHandler;
import org.pentaho.reporting.engine.classic.core.states.InitialLayoutProcess;
import org.pentaho.reporting.engine.classic.core.states.LayoutProcess;
import org.pentaho.reporting.engine.classic.core.states.ProcessStateHandle;
import org.pentaho.reporting.engine.classic.core.states.ReportDefinitionImpl;
import org.pentaho.reporting.engine.classic.core.states.ReportProcessingErrorHandler;
import org.pentaho.reporting.engine.classic.core.states.ReportState;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.states.StateUtilities;
import org.pentaho.reporting.engine.classic.core.states.StructureFunctionComparator;
import org.pentaho.reporting.engine.classic.core.states.SubLayoutProcess;
import org.pentaho.reporting.engine.classic.core.states.SubReportStorage;
import org.pentaho.reporting.engine.classic.core.states.crosstab.CrosstabProcessorFunction;
import org.pentaho.reporting.engine.classic.core.states.datarow.DefaultFlowController;
import org.pentaho.reporting.engine.classic.core.states.datarow.InlineDataRowRuntime;
import org.pentaho.reporting.engine.classic.core.states.datarow.MasterDataRow;
import org.pentaho.reporting.engine.classic.core.states.datarow.ReportDataRow;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.util.IntegerCache;
import org.pentaho.reporting.engine.classic.core.util.ReportParameterValues;
import org.pentaho.reporting.engine.classic.core.util.beans.ConverterRegistry;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchemaDefinition;
import org.pentaho.reporting.engine.classic.core.wizard.ProxyDataSchemaDefinition;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.FastStack;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

public class ProcessState implements ReportState
{
  private static class InternalProcessHandle implements ProcessStateHandle
  {
    private DataFactoryManager manager;

    private InternalProcessHandle(final DataFactoryManager manager)
    {
      this.manager = manager;
    }

    public void close()
    {
      // close the data-factory manager ...
      manager.close();
    }
  }


  public static final int ARTIFICIAL_EVENT_CODE = ReportEvent.ARTIFICIAL_EVENT_CODE;
  private static final Log logger = LogFactory.getLog(ProcessState.class);

  private int currentGroupIndex;
  private int currentPresentationGroupIndex;
  private ReportDefinitionImpl report;

  private int currentSubReport;
  private InlineSubreportMarker[] subReports;
  private ProcessState parentState;
  private ProcessState parentSubReportState;
  private FunctionStorage functionStorage;
  private FunctionStorage structureFunctionStorage;
  private DataFactoryManager dataFactoryManager;
  private InternalProcessHandle processHandle;
  private DefaultFlowController flowController;
  private LayoutProcess layoutProcess;
  private ReportStateKey processKey;
  private AdvanceHandler advanceHandler;
  private ReportProcessingErrorHandler errorHandler;
  private int sequenceCounter;
  private boolean inItemGroup;
  private InlineSubreportMarker currentSubReportMarker;
  private boolean inlineProcess;
  private FastStack groupStarts;
  //  private InstanceID reportInstanceID;
  private boolean structuralPreprocessingNeeded;
  private HashSet processLevels;
  private SubReportStorage subReportStorage;
  private String query;
  private Integer queryLimit;
  private Integer queryTimeout;
  private boolean reportInstancesShareConnection;

  public ProcessState()
  {
  }

  public void initializeForMasterReport(final MasterReport report,
                                        final ProcessingContext processingContext,
                                        final InitialLayoutProcess layoutProcess)
      throws ReportProcessingException
  {
    if (layoutProcess == null)
    {
      throw new NullPointerException("LayoutProcess must not be null.");
    }
    if (report == null)
    {
      throw new NullPointerException("Report must not be null");
    }
    if (processingContext == null)
    {
      throw new NullPointerException("ProcessingContext must not be null.");
    }

    final ReportParameterDefinition parameters = report.getParameterDefinition();
    final DefaultParameterContext parameterContext = new DefaultParameterContext(report);
    parameterContext.open();
    final ReportParameterValues parameterValues;
    try
    {
      final ReportParameterValidator reportParameterValidator = parameters.getValidator();
      final ValidationResult validationResult =
          reportParameterValidator.validate(new ValidationResult(), parameters, parameterContext);
      if (validationResult.isEmpty() == false)
      {
        throw new ReportParameterValidationException
            ("The parameters provided for this report are not valid.", validationResult);
      }
      parameterValues = validationResult.getParameterValues();
    }
    finally
    {
      parameterContext.close();
    }

    this.reportInstancesShareConnection = "true".equals(processingContext.getConfiguration().getConfigProperty
        ("org.pentaho.reporting.engine.classic.core.ReportInstancesShareConnections"));
    this.processLevels = new HashSet();
    this.groupStarts = new FastStack();
    this.errorHandler = IgnoreEverythingReportErrorHandler.INSTANCE;
    this.advanceHandler = BeginReportHandler.HANDLER;
    this.parentState = null;
    this.currentSubReport = -1;
    this.currentGroupIndex = ReportState.BEFORE_FIRST_GROUP;
    this.currentPresentationGroupIndex = ReportState.BEFORE_FIRST_GROUP;
    this.functionStorage = new FunctionStorage();
    this.structureFunctionStorage = new FunctionStorage();
    this.sequenceCounter = 0;
    this.processKey = new ReportStateKey
        (null, ReportState.BEFORE_FIRST_ROW, 0, 0, ReportState.BEFORE_FIRST_GROUP, -1, sequenceCounter, false);
    this.dataFactoryManager = new DataFactoryManager();
    this.subReportStorage = new SubReportStorage();
    this.processHandle = new InternalProcessHandle(dataFactoryManager);


    if (isStructureRunNeeded(report) == false)
    {
      // Perform a static analysis on whether there is an External-element or Inline-Subreports or Crosstabs
      // if none, return unchanged
      this.structuralPreprocessingNeeded = false;
    }
    else
    {
      // otherwise process the report one time to walk through all eligible states. Record all subreports,
      // and then compute the runlevels based on what we have in the caches.
      this.structuralPreprocessingNeeded = true;
    }

    final DataSchemaDefinition definition = report.getDataSchemaDefinition();
    final DefaultFlowController flowController = new DefaultFlowController(processingContext,
        definition, StateUtilities.computeParameterValueSet(report, parameterValues),
        report.getParameterDefinition().getParameterDefinitions(), structuralPreprocessingNeeded);

    final Object dataCacheEnabledRaw =
        report.getAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.DATA_CACHE);
    final boolean dataCacheEnabled = Boolean.FALSE.equals(dataCacheEnabledRaw) == false;
    final CachingDataFactory dataFactory = new CachingDataFactory(report.getDataFactory(), dataCacheEnabled);
    dataFactory.initialize(processingContext.getConfiguration(), processingContext.getResourceManager(),
        processingContext.getContentBase(), processingContext.getResourceBundleFactory());
    dataFactory.open();

    final FunctionStorageKey functionStorageKey = FunctionStorageKey.createKey(null, report);
    this.dataFactoryManager.store(functionStorageKey, dataFactory, true);
    // eval query, query-limit and query-timeout
    this.flowController = flowController;
    final Integer queryLimitDefault = IntegerCache.getInteger(report.getQueryLimit());
    final Integer queryTimeoutDefault = IntegerCache.getInteger(report.getQueryTimeout());

    final Object queryRaw = evaluateExpression(report.getAttributeExpression(AttributeNames.Internal.NAMESPACE,
        AttributeNames.Internal.QUERY), report.getQuery());
    final Object queryLimitRaw = evaluateExpression(report.getAttributeExpression(AttributeNames.Internal.NAMESPACE,
        AttributeNames.Internal.QUERY_LIMIT), queryLimitDefault);
    final Object queryTimeoutRaw = evaluateExpression(report.getAttributeExpression(AttributeNames.Internal.NAMESPACE,
        AttributeNames.Internal.QUERY_TIMEOUT), queryTimeoutDefault);
    this.query = (String) ConverterRegistry.convert(queryRaw, String.class, report.getQuery());
    this.queryLimit = (Integer) ConverterRegistry.convert(queryLimitRaw, Integer.class, queryLimitDefault);
    this.queryTimeout = (Integer) ConverterRegistry.convert(queryTimeoutRaw, Integer.class, queryTimeoutDefault);

    DefaultFlowController postQueryFlowController = flowController.performQuery
        (dataFactory, query, queryLimit.intValue(), queryTimeout.intValue(),
            processingContext.getResourceBundleFactory());
    final ReportPreProcessor[] processors = getAllPreProcessors(report);
    MasterReport fullReport = report;
    DataSchemaDefinition fullDefinition = definition;
    for (int i = 0; i < processors.length; i++)
    {
      final ReportPreProcessor processor = processors[i];
      fullReport = processor.performPreProcessing(fullReport, postQueryFlowController);
      if (fullReport.getDataSchemaDefinition() != fullDefinition)
      {
        fullDefinition = fullReport.getDataSchemaDefinition();
        postQueryFlowController = postQueryFlowController.updateDataSchema(fullDefinition);
      }
    }

    this.flowController =
        postQueryFlowController.activateExpressions(fullReport.getExpressions().getExpressions(), false);
    this.report = new ReportDefinitionImpl(fullReport, fullReport.getPageDefinition());
    this.layoutProcess = new SubLayoutProcess(layoutProcess,
        computeStructureFunctions(fullReport.getStructureFunctions(),
            getFlowController().getReportContext().getOutputProcessorMetaData()));

    StateUtilities.computeLevels(this.flowController, this.layoutProcess, processLevels);
    this.processKey = createKey();
  }

  private boolean isReportsShareConnections(final ReportDefinition report)
  {
    final Object attribute = report.getAttribute
        (AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.SHARED_CONNECTIONS);
    if (Boolean.TRUE.equals(attribute))
    {
      return true;
    }
    if (Boolean.FALSE.equals(attribute))
    {
      return false;
    }
    return reportInstancesShareConnection;
  }

  public void initializeForSubreport(final InlineSubreportMarker[] subReports,
                                     final int subReportIndex,
                                     final ProcessState parentState) throws ReportProcessingException
  {
    if (parentState == null)
    {
      throw new NullPointerException();
    }

    this.reportInstancesShareConnection = parentState.reportInstancesShareConnection;
    this.groupStarts = new FastStack();
    this.parentState = parentState;
    this.parentSubReportState = parentState;
    this.advanceHandler = BeginReportHandler.HANDLER;
    this.errorHandler = parentState.errorHandler;
    this.functionStorage = parentState.functionStorage;
    this.structureFunctionStorage = parentState.structureFunctionStorage;
    this.currentGroupIndex = ReportState.BEFORE_FIRST_GROUP;
    this.currentPresentationGroupIndex = ReportState.BEFORE_FIRST_GROUP;
    this.currentSubReport = subReportIndex;
    this.flowController = parentState.flowController;
    this.subReports = subReports.clone();
    this.dataFactoryManager = parentState.dataFactoryManager;
    this.subReportStorage = parentState.subReportStorage;
    this.structuralPreprocessingNeeded = parentState.structuralPreprocessingNeeded;
    this.processLevels = parentState.processLevels;
    this.sequenceCounter = parentState.getSequenceCounter() + 1;

    this.currentSubReportMarker = subReports[subReportIndex];
    this.inlineProcess =
        parentState.isInlineProcess() || currentSubReportMarker.getProcessType() == SubReportProcessType.INLINE;

    final SubReport subreportFromMarker = currentSubReportMarker.getSubreport();
    final FunctionStorageKey functionStorageKey = FunctionStorageKey.createKey
        (parentSubReportState.getProcessKey(), subreportFromMarker);
    final boolean needPreProcessing;
    final SubReport report;
    if (subReportStorage.contains(functionStorageKey))
    {
      report = subReportStorage.restore(functionStorageKey);
      final ElementStyleSheet subreportStyle = subreportFromMarker.getStyle();
      final Map styleExpressions = subreportFromMarker.getStyleExpressions();
      final StyleKey[] definedStyle =
          (StyleKey[]) styleExpressions.keySet().toArray(new StyleKey[styleExpressions.size()]);
      for (int i = 0; i < definedStyle.length; i++)
      {
        final StyleKey styleKey = definedStyle[i];
        report.getStyle().setStyleProperty(styleKey, subreportStyle.getStyleProperty(styleKey));
      }
      needPreProcessing = false;
    }
    else
    {
      report = subreportFromMarker;
      needPreProcessing = true;
    }

    final ResourceBundleFactory resourceBundleFactory;
    if (report.getResourceBundleFactory() != null)
    {
      resourceBundleFactory = MasterReport.computeAndInitResourceBundleFactory
          (report.getResourceBundleFactory(), parentState.getFlowController().getReportContext().getEnvironment());

    }
    else
    {
      resourceBundleFactory = parentState.getResourceBundleFactory();
    }

    final int processingLevel = flowController.getReportContext().getProcessingLevel();
    if (processingLevel == LayoutProcess.LEVEL_PAGINATE &&
        report.isVisible() == false)
    {
      // make it a minimum effort report, but still enter the loop.
      final ReportDefinition parentReport = parentState.getReport();
      final SubReport dummyReport = new SubReport(functionStorageKey.getReportId());
      this.report = new ReportDefinitionImpl(dummyReport, parentReport.getPageDefinition());
      this.flowController = parentState.flowController.derive();
      this.advanceHandler = EndSubReportHandler.HANDLER;
      this.layoutProcess = new SubLayoutProcess
          (parentState.layoutProcess, computeStructureFunctions(report.getStructureFunctions(),
              flowController.getReportContext().getOutputProcessorMetaData()));
    }
    else
    {
      CachingDataFactory dataFactory = dataFactoryManager.restore(functionStorageKey, isReportsShareConnections(report));

      if (dataFactory == null)
      {
        final DataFactory subreportDf = report.getDataFactory();
        final Object dataCacheEnabledRaw =
            report.getAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.DATA_CACHE);
        final boolean dataCacheEnabled = Boolean.FALSE.equals(dataCacheEnabledRaw) == false;
        if (subreportDf == null)
        {
          // subreport does not define a own factory, so reuse the parent's data-factory.
          dataFactory = new CachingDataFactory(parentState.getFlowController().getDataFactory(), true, dataCacheEnabled);
        }
        else
        {
          // subreport comes with an own factory, so open the gates ..
          dataFactory = new CachingDataFactory(subreportDf, dataCacheEnabled);
          final ProcessingContext context = parentState.getFlowController().getReportContext();
          dataFactory.initialize(context.getConfiguration(), context.getResourceManager(),
              context.getContentBase(), resourceBundleFactory);
          dataFactory.open();
          dataFactoryManager.store(functionStorageKey, dataFactory, isReportsShareConnections(report));
        }
      }

      // And now initialize the sub-report.
      final ParameterMapping[] inputMappings = report.getInputMappings();
      final ParameterMapping[] exportMappings = report.getExportMappings();

      // eval query, query-limit and query-timeout
      this.flowController = parentState.flowController.performInitSubreport
          (dataFactory, inputMappings, resourceBundleFactory);
      final Integer queryLimitDefault = IntegerCache.getInteger(report.getQueryLimit());
      final Integer queryTimeoutDefault = IntegerCache.getInteger(report.getQueryTimeout());

      final Object queryRaw = evaluateExpression(report.getAttributeExpression(AttributeNames.Internal.NAMESPACE,
          AttributeNames.Internal.QUERY), report.getQuery());
      final Object queryLimitRaw = evaluateExpression(report.getAttributeExpression(AttributeNames.Internal.NAMESPACE,
          AttributeNames.Internal.QUERY_LIMIT), queryLimitDefault);
      final Object queryTimeoutRaw = evaluateExpression(report.getAttributeExpression(AttributeNames.Internal.NAMESPACE,
          AttributeNames.Internal.QUERY_TIMEOUT), queryTimeoutDefault);
      this.query = (String) ConverterRegistry.convert(queryRaw, String.class, report.getQuery());
      this.queryLimit = (Integer) ConverterRegistry.convert(queryLimitRaw, Integer.class, queryLimitDefault);
      this.queryTimeout = (Integer) ConverterRegistry.convert(queryTimeoutRaw, Integer.class, queryTimeoutDefault);

      DefaultFlowController postQueryFlowController = flowController.performSubReportQuery
          (query, queryLimit.intValue(), queryTimeout.intValue(), exportMappings);
      final ProxyDataSchemaDefinition schemaDefinition =
          new ProxyDataSchemaDefinition(report.getDataSchemaDefinition(),
              postQueryFlowController.getMasterRow().getDataSchemaDefinition());
      postQueryFlowController = postQueryFlowController.updateDataSchema(schemaDefinition);

      SubReport fullReport = report;
      DataSchemaDefinition fullDefinition = schemaDefinition;

      if (needPreProcessing)
      {
        final ReportPreProcessor[] processors = getAllPreProcessors(report);
        for (int i = 0; i < processors.length; i++)
        {
          final ReportPreProcessor processor = processors[i];
          fullReport = processor.performPreProcessing(fullReport, postQueryFlowController);
          if (fullReport.getDataSchemaDefinition() != fullDefinition)
          {
            fullDefinition = fullReport.getDataSchemaDefinition();
            postQueryFlowController = postQueryFlowController.updateDataSchema(fullDefinition);
          }
        }

        subReportStorage.store(functionStorageKey, fullReport);
      }

      this.report = new ReportDefinitionImpl(fullReport, fullReport.getPageDefinition());


      final Expression[] structureFunctions = getStructureFunctionStorage().restore(functionStorageKey);
      if (structureFunctions != null)
      {
        final StructureFunction[] functions = new StructureFunction[structureFunctions.length];
        //noinspection SuspiciousSystemArraycopy
        System.arraycopy(structureFunctions, 0, functions, 0, structureFunctions.length);
        this.layoutProcess = new SubLayoutProcess(parentState.layoutProcess, functions);
      }
      else
      {
        final StructureFunction[] functions = computeStructureFunctions(fullReport.getStructureFunctions(),
            postQueryFlowController.getReportContext().getOutputProcessorMetaData());
        this.layoutProcess = new SubLayoutProcess(parentState.layoutProcess, functions);
      }

      boolean preserve = true;
      Expression[] expressions = getFunctionStorage().restore(functionStorageKey);
      if (expressions == null)
      {
        // ok, it seems we have entered a new subreport ..
        // we use the expressions from the report itself ..
        expressions = fullReport.getExpressions().getExpressions();
        preserve = false;
      }

      this.flowController = postQueryFlowController.activateExpressions(expressions, preserve);
    }

    StateUtilities.computeLevels(this.flowController, this.layoutProcess, processLevels);
    this.processKey = createKey();
  }


  private Object evaluateExpression(final Expression expression, final Object defaultValue)
  {
    if (expression == null)
    {
      return defaultValue;
    }

    final Expression evalExpression = expression.getInstance();

    final InlineDataRowRuntime runtime = new InlineDataRowRuntime();
    runtime.setState(this);
    final ExpressionRuntime oldRuntime = evalExpression.getRuntime();
    try
    {
      evalExpression.setRuntime(runtime);
      return evalExpression.getValue();
    }
    catch (Exception e)
    {
      logger.debug("Failed to evaluate expression " + expression);
      return defaultValue;
    }
    finally
    {
      evalExpression.setRuntime(oldRuntime);
    }
  }

  public int[] getRequiredRuntimeLevels()
  {
    processLevels.add(IntegerCache.getInteger(LayoutProcess.LEVEL_PAGINATE));

    final int[] retval = new int[processLevels.size()];
    final Integer[] levels = (Integer[]) processLevels.toArray(new Integer[processLevels.size()]);
    Arrays.sort(levels, new StateUtilities.DescendingComparator());
    for (int i = 0; i < levels.length; i++)
    {
      final Integer level = levels[i];
      retval[i] = level.intValue();
    }

    return retval;
  }

  private StructureFunction[] computeStructureFunctions(final StructureFunction[] fromReport,
                                                        final OutputProcessorMetaData metaData)
  {
    if (metaData.isFeatureSupported(OutputProcessorFeature.DESIGNTIME))
    {
      return new StructureFunction[]{new CrosstabProcessorFunction()};
    }

    final ArrayList e = new ArrayList(Arrays.asList(fromReport));
    if (structuralPreprocessingNeeded)
    {
      e.add(new CrosstabProcessorFunction());
    }
    e.add(new AttributeExpressionsEvaluator());
    e.add(new SheetNameFunction());
    e.add(new MetaDataStyleEvaluator());
    e.add(new StyleExpressionsEvaluator());
    e.add(new CellFormatFunction());
    e.add(new WizardItemHideFunction());
    Collections.sort(e, new StructureFunctionComparator());
    return (StructureFunction[]) e.toArray(new StructureFunction[e.size()]);
  }

  private ReportPreProcessor[] getAllPreProcessors(final AbstractReportDefinition reportDefinition)
  {
    final ReportPreProcessor[] processors = reportDefinition.getPreProcessors();
    final Configuration configuration = ClassicEngineBoot.getInstance().getGlobalConfig();
    final ArrayList preProcessors = new ArrayList();
    for (int i = 0; i < processors.length; i++)
    {
      final ReportPreProcessor o = processors[i];
      if (o != null)
      {
        preProcessors.add(o);
      }
    }

    final Iterator keys = configuration.findPropertyKeys
        ("org.pentaho.reporting.engine.classic.core.auto-report-preprocessors.");
    while (keys.hasNext())
    {
      final String key = (String) keys.next();
      final String value = configuration.getConfigProperty(key);
      final Object o = ObjectUtilities.loadAndInstantiate(value, ProcessState.class, ReportPreProcessor.class);
      if (o != null)
      {
        preProcessors.add(o);
      }
    }

    return (ReportPreProcessor[]) preProcessors.toArray(new ReportPreProcessor[preProcessors.size()]);
  }

  public boolean isSubReportExecutable()
  {
    final Expression expression =
        getReport().getAttributeExpression(AttributeNames.Core.NAMESPACE, AttributeNames.Core.SUBREPORT_ACTIVE);
    if (expression != null)
    {
      // the master-report state will only be non-null for subreports.
      final InlineDataRowRuntime dataRowRuntime = new InlineDataRowRuntime();
      dataRowRuntime.setState(this);
      expression.setRuntime(dataRowRuntime);
      try
      {
        final Object value = expression.getValue();
        // the expression has to explicitly return false as a value to disable the report processing of
        // subreports. Just returning null or a non-boolean value is not enough. This is a safety measure
        // so that if in doubt we print more data than to little.
        if (Boolean.FALSE.equals(value))
        {
          return false;
        }
        if ("false".equals(String.valueOf(value)))
        {
          return false;
        }
        return true;
      }
      finally
      {
        expression.setRuntime(null);
      }
    }
    return true;
  }

  public ProcessState returnFromSubReport(final LayoutProcess layoutProcess) throws ReportProcessingException
  {
    final ProcessState state = deriveForAdvance();
    try
    {
      state.layoutProcess = (LayoutProcess) layoutProcess.clone();
    }
    catch (final CloneNotSupportedException e)
    {
      throw new ReportProcessingException("Clone must not fail here", e);
    }
    return state;
  }

  public ProcessState restart() throws ReportProcessingException
  {
    final ProcessState state = this.deriveForStorage();
    state.currentSubReport = -1;
    state.currentGroupIndex = ReportState.BEFORE_FIRST_GROUP;
    state.currentPresentationGroupIndex = ReportState.BEFORE_FIRST_GROUP;
    if (state.groupStarts.isEmpty() == false)
    {
      throw new IllegalStateException();
    }
    state.setAdvanceHandler(BeginReportHandler.HANDLER);

    final ReportStateKey parentStateKey;
    final ReportState parentState = this.getParentSubReportState();
    if (parentState == null)
    {
      parentStateKey = null;
    }
    else
    {
      parentStateKey = parentState.getProcessKey();
    }

    final CachingDataFactory dataFactory = state.dataFactoryManager.restore
        (FunctionStorageKey.createKey(parentStateKey, state.getReport()), isReportsShareConnections(report));
    if (dataFactory == null)
    {
      throw new ReportProcessingException("No data factory on restart()? Somewhere we went wrong.");
    }

    final DefaultFlowController fc = state.getFlowController();
    final DefaultFlowController cfc = fc.restart();
    final DefaultFlowController qfc = cfc.performQuery
        (dataFactory, query, queryLimit.intValue(), queryTimeout.intValue(), fc.getMasterRow().getResourceBundleFactory());
    final Expression[] expressions = getFunctionStorage().restore
        (FunctionStorageKey.createKey(null, state.getReport()));
    final DefaultFlowController efc = qfc.activateExpressions(expressions, true);
    state.setFlowController(efc);
    state.sequenceCounter += 1;
    state.processKey = createKey();
    return state;
  }

  public ReportProcessingErrorHandler getErrorHandler()
  {
    return errorHandler;
  }

  public void setErrorHandler(final ReportProcessingErrorHandler errorHandler)
  {
    this.errorHandler = errorHandler;
  }

  public void setSequenceCounter(final int sequenceCounter)
  {
    this.sequenceCounter = sequenceCounter;
    this.processKey = this.createKey();
  }

  public int getSequenceCounter()
  {
    return sequenceCounter;
  }

  public InlineSubreportMarker getCurrentSubReportMarker()
  {
    return currentSubReportMarker;
  }

  public boolean isInlineProcess()
  {
    return inlineProcess;
  }

  public SubReportProcessType getSubreportProcessingType()
  {
    if (inlineProcess)
    {
      return SubReportProcessType.INLINE;
    }
    return SubReportProcessType.BANDED;
  }

  /**
   * This is a more expensive version of the ordinary derive. This method creates a separate copy of the layout-process
   * so that this operation is expensive in memory and CPU usage.
   *
   * @return the derived state.
   */
  public ProcessState deriveForPagebreak()
  {
    try
    {
      final ProcessState processState = (ProcessState) clone();
      processState.sequenceCounter += 1;
      processState.flowController = flowController.derive();
      processState.report = (ReportDefinitionImpl) report.clone();
      processState.layoutProcess = layoutProcess.deriveForPagebreak();
      processState.processKey = processState.createKey();
      return processState;
    }
    catch (CloneNotSupportedException e)
    {
      throw new IllegalStateException("Clone failed but I dont know why ..");
    }
  }

  public ProcessState deriveForAdvance()
  {
    try
    {
      final ProcessState processState = (ProcessState) clone();
      processState.sequenceCounter += 1;
      processState.processKey = processState.createKey();
      return processState;
    }
    catch (CloneNotSupportedException e)
    {
      throw new IllegalStateException("Clone failed but I dont know why ..");
    }
  }

  public ProcessState deriveForStorage()
  {
    try
    {
      final ProcessState result = (ProcessState) clone();
      result.flowController = flowController.derive();
      result.report = (ReportDefinitionImpl) report.clone();
      result.layoutProcess = layoutProcess.deriveForStorage();
      return result;
    }
    catch (CloneNotSupportedException e)
    {
      throw new IllegalStateException("Clone failed but I dont know why ..");
    }
  }

  public Object clone() throws CloneNotSupportedException
  {
    final ProcessState result = (ProcessState) super.clone();
    result.groupStarts = (FastStack) groupStarts.clone();
    result.processKey = createKey();
    return result;
  }

  public AdvanceHandler getAdvanceHandler()
  {
    return advanceHandler;
  }

  private ReportStateKey createKey()
  {
    if (parentState != null)
    {
      return new ReportStateKey(parentState.createKey(),
          getCurrentDataItem(), getCurrentCrosstabPaddingItem(), getEventCode(),
          getCurrentGroupIndex(), getCurrentSubReport(), sequenceCounter, advanceHandler.isRestoreHandler());
    }

    return new ReportStateKey(null, getCurrentDataItem(), getCurrentCrosstabPaddingItem(),
        getEventCode(), getCurrentGroupIndex(), getCurrentSubReport(),
        sequenceCounter, advanceHandler.isRestoreHandler());
  }

  public void setAdvanceHandler(final AdvanceHandler advanceHandler)
  {
    if (advanceHandler == null)
    {
      throw new NullPointerException();
    }
    this.advanceHandler = advanceHandler;
    this.processKey = null;
  }

  public final ProcessState advance() throws ReportProcessingException
  {
    return advanceHandler.advance(this);
  }

  public final ProcessState commit() throws ReportProcessingException
  {
    return advanceHandler.commit(this);
  }

  public int getCurrentDataItem()
  {
    return this.flowController.getCursor();
  }

  public int getCurrentCrosstabPaddingItem()
  {
    return this.flowController.getCurrentCrosstabPaddingItem();
  }

  public int getProgressLevel()
  {
    return flowController.getReportContext().getProgressLevel();
  }

  public int getProgressLevelCount()
  {
    return flowController.getReportContext().getProgressLevelCount();
  }

  public boolean isPrepareRun()
  {
    return flowController.getReportContext().isPrepareRun();
  }

  public int getLevel()
  {
    return flowController.getReportContext().getProcessingLevel();
  }

  public boolean isFinish()
  {
    return advanceHandler.isFinish();
  }

  public int getEventCode()
  {
    return advanceHandler.getEventCode();
  }


  public int getCurrentGroupIndex()
  {
    return currentGroupIndex;
  }

  public void enterGroup()
  {
    currentGroupIndex += 1;
    final Group group = report.getGroup(currentGroupIndex);
    groupStarts.push(new GroupStartRecord(getCurrentDataItem(), group.getName()));

    if (groupStarts.size() != currentGroupIndex + 1)
    {
      throw new IllegalStateException();
    }
  }

  public void leaveGroup()
  {
    if (groupStarts.size() != currentGroupIndex + 1)
    {
      throw new IllegalStateException();
    }

    currentGroupIndex -= 1;
    groupStarts.pop();
  }

  public int getPresentationGroupIndex()
  {
    return currentPresentationGroupIndex;
  }

  public void enterPresentationGroup()
  {
    currentPresentationGroupIndex += 1;
  }

  public void leavePresentationGroup()
  {
    currentPresentationGroupIndex -= 1;
  }

  public ReportDefinition getReport()
  {
    return report;
  }

  public int getCurrentSubReport()
  {
    return currentSubReport;
  }

  public void setCurrentSubReport(final int currentSubReport)
  {
    this.currentSubReport = currentSubReport;
  }

  public ReportState getParentState()
  {
    return parentState;
  }

  public ReportState getParentSubReportState()
  {
    return parentSubReportState;
  }

  public FunctionStorage getStructureFunctionStorage()
  {
    return structureFunctionStorage;
  }

  public FunctionStorage getFunctionStorage()
  {
    return functionStorage;
  }

  public DefaultFlowController getFlowController()
  {
    return flowController;
  }

  public void setFlowController(final DefaultFlowController flowController)
  {
    if (flowController == null)
    {
      throw new NullPointerException();
    }
    this.flowController = flowController;
    this.processKey = null;
  }

  public LayoutProcess getLayoutProcess()
  {
    return layoutProcess;
  }

  public ReportStateKey getProcessKey()
  {
    if (processKey == null)
    {
      processKey = createKey();
    }
    return processKey;
  }

  public DataRow getDataRow()
  {
    return flowController.getMasterRow().getGlobalView();
  }

  public int getNumberOfRows()
  {
    final MasterDataRow masterRow = flowController.getMasterRow();
    final ReportDataRow reportDataRow = masterRow.getReportDataRow();
    if (reportDataRow != null)
    {
      return reportDataRow.getReportData().getRowCount();
    }
    return 0;
  }

  /**
   * Fires a 'page-started' event.
   *
   * @param baseEvent the type of the base event which caused the page start to be triggered.
   */
  public void firePageStartedEvent(final int baseEvent)
  {
    final ReportEvent event = new ReportEvent(this, ReportEvent.PAGE_STARTED | baseEvent);
    flowController = flowController.fireReportEvent(event);
    layoutProcess.fireReportEvent(event);
  }

  /**
   * Fires a '<code>page-finished</code>' event.  The <code>pageFinished(...)</code> method is called for every report
   * function.
   */
  public void firePageFinishedEvent(final boolean noParentPassing)
  {
    final int eventCode = ReportEvent.PAGE_FINISHED | (noParentPassing ? ReportEvent.NO_PARENT_PASSING_EVENT : 0);
    final ReportEvent event = new ReportEvent(this, eventCode);
    flowController = flowController.fireReportEvent(event);
    layoutProcess.fireReportEvent(event);
  }

  protected void fireReportEvent()
  {
    if ((advanceHandler.getEventCode() & ProcessState.ARTIFICIAL_EVENT_CODE) == ProcessState.ARTIFICIAL_EVENT_CODE)
    {
      throw new IllegalStateException("Cannot fire artificial events.");
    }

    final ReportEvent event = new ReportEvent(this, advanceHandler.getEventCode());
    flowController = flowController.fireReportEvent(event);
    layoutProcess.fireReportEvent(event);
  }

  /**
   * Returns true if this is the last item in the group, and false otherwise. This checks the group condition and all
   * conditions of all subgroups.
   *
   * @param rootGroup      the root group that should be checked.
   * @param currentDataRow the current data row.
   * @param nextDataRow    the next data row, or null, if this is the last datarow.
   * @return A flag indicating whether or not the current item is the last in its group.
   */
  public static boolean isLastItemInGroup
      (final Group rootGroup,
       final MasterDataRow currentDataRow,
       final MasterDataRow nextDataRow)
  {
    // return true if this is the last row in the model.
    if (currentDataRow.isAdvanceable() == false || nextDataRow == null)
    {
      return true;
    }

    final DataRow nextView = nextDataRow.getGlobalView();
    Group g = rootGroup;
    while (g != null)
    {
      if (g.isGroupChange(nextView))
      {
        return true;
      }

      // groups are never directly nested into each other. They always have a group-body between each group instance. 
      final Section parentSection = g.getParentSection();
      if (parentSection == null)
      {
        return false;
      }

      final Section maybeGroup = parentSection.getParentSection();
      if (maybeGroup instanceof Group)
      {
        g = (Group) maybeGroup;
      }
      else
      {
        g = null;
      }
    }
    return false;
  }

  public boolean isSubReportEvent()
  {
    return getParentSubReportState() != null;
  }

  public InlineSubreportMarker[] getSubReports()
  {
    return subReports.clone();
  }

  public ProcessStateHandle getProcessHandle()
  {
    return processHandle;
  }

  public void setInItemGroup(final boolean inItemGroup)
  {
    this.inItemGroup = inItemGroup;
  }

  public boolean isInItemGroup()
  {
    return inItemGroup;
  }

  public ResourceBundleFactory getResourceBundleFactory()
  {
    return flowController.getMasterRow().getResourceBundleFactory();
  }

  public boolean isArtifcialState()
  {
    return (advanceHandler.getEventCode() & ReportEvent.ARTIFICIAL_EVENT_CODE) != 0;
  }

  public GroupingState createGroupingState()
  {
    return new DefaultGroupingState(currentGroupIndex, (FastStack) groupStarts.clone());
  }

  private boolean isStructureRunNeeded(final Section section)
  {
    final int count = section.getElementCount();
    for (int i = 0; i < count; i++)
    {
      final ReportElement element = section.getElement(i);
      final Object type = element.getAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.ELEMENT_TYPE);
      if (type instanceof ExternalElementType)
      {
        return true;
      }

      if (element instanceof CrosstabGroup)
      {
        return true;
      }
      else if (element instanceof SubReport)
      {
        return true;
      }
      else if (element instanceof RootLevelBand)
      {
        final RootLevelBand band = (RootLevelBand) element;
        if (band.getSubReportCount() > 0)
        {
          return true;
        }
        if (isStructureRunNeeded((Section) element))
        {
          return true;
        }
      }
      else if (element instanceof Section)
      {
        if (isStructureRunNeeded((Section) element))
        {
          return true;
        }
      }
    }
    return false;
  }

  public boolean isStructuralPreprocessingNeeded()
  {
    return structuralPreprocessingNeeded;
  }
}
