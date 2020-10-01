package de.metanome.algorithms.myoddetector;

import java.util.ArrayList;
import static java.util.Arrays.asList;
import de.metanome.algorithm_integration.AlgorithmConfigurationException;
import de.metanome.algorithm_integration.AlgorithmExecutionException;
import de.metanome.algorithm_integration.algorithm_types.*;
import de.metanome.algorithm_integration.configuration.*;
import de.metanome.algorithm_integration.input.RelationalInputGenerator;
import de.metanome.algorithm_integration.result_receiver.InclusionDependencyResultReceiver;
import de.metanome.algorithm_integration.result_receiver.OrderDependencyResultReceiver;

public class MyOdDetector extends MyOdDetectorAlgorithm 				// Separating the algorithm implementation and the Metanome interface implementation is good practice
						  implements InclusionDependencyAlgorithm, 			// Defines the type of the algorithm, i.e., the result type, for instance, FunctionalDependencyAlgorithm or InclusionDependencyAlgorithm; implementing multiple types is possible
						  			 RelationalInputParameterAlgorithm,	// Defines the input type of the algorithm; relational input is any relational input from files or databases; more specific input specifications are possible
						  			 StringParameterAlgorithm, IntegerParameterAlgorithm, BooleanParameterAlgorithm {	// Types of configuration parameters this algorithm requires; all these are optional

	private final MyOdDetectorAlgorithm impl;
	private final Configuration defaultValues;
	private final Configuration.ConfigurationBuilder builder;

	public MyOdDetector(Configuration defaultValues) {
		this.impl = new MyOdDetectorAlgorithm();
		this.defaultValues = Configuration.withDefaults();//defaultValues;
		this.builder = Configuration.builder();
	}

	public enum Identifier {
		INPUT_GENERATOR, SOME_STRING_PARAMETER, SOME_INTEGER_PARAMETER, SOME_BOOLEAN_PARAMETER
	};

	@Override
	public String getAuthors() {
		return "Tianfeng Li"; // A string listing the author(s) of this algorithm
	}

	@Override
	public String getDescription() {
		return "An independency discovery algorithm"; // A string briefly describing what this algorithm does
	}
	
	@Override
	public ArrayList<ConfigurationRequirement<?>> getConfigurationRequirements() { // Tells Metanome which and how many parameters the algorithm needs
		ArrayList<ConfigurationRequirement<?>> conf = new ArrayList<>();
		/*conf.add(new ConfigurationRequirementRelationalInput(MyOdDetector.Identifier.INPUT_GENERATOR.name()));
		//conf.add(new ConfigurationRequirementRelationalInput(MyIndDetector.Identifier.INPUT_GENERATOR.name(), ConfigurationRequirement.ARBITRARY_NUMBER_OF_VALUES)); // An algorithm can ask for more than one input; this is typical for IND detection algorithms

		ConfigurationRequirementString stringParameter = new ConfigurationRequirementString(MyOdDetector.Identifier.SOME_STRING_PARAMETER.name());
		String[] defaultStringParameter = new String[1];
		defaultStringParameter[0] = "default value";
		stringParameter.setDefaultValues(defaultStringParameter);
		stringParameter.setRequired(true);
		conf.add(stringParameter);
		
		ConfigurationRequirementInteger integerParameter = new ConfigurationRequirementInteger(MyOdDetector.Identifier.SOME_INTEGER_PARAMETER.name());
		Integer[] defaultIntegerParameter = new Integer[1];
		defaultIntegerParameter[0] = Integer.valueOf(42);
		integerParameter.setDefaultValues(defaultIntegerParameter);
		integerParameter.setRequired(true);
		conf.add(integerParameter);

		ConfigurationRequirementBoolean booleanParameter = new ConfigurationRequirementBoolean(MyOdDetector.Identifier.SOME_BOOLEAN_PARAMETER.name());
		Boolean[] defaultBooleanParameter = new Boolean[1];
		defaultBooleanParameter[0] = Boolean.valueOf(true);
		booleanParameter.setDefaultValues(defaultBooleanParameter);
		booleanParameter.setRequired(true);
		conf.add(booleanParameter);
		*/
		conf.add(relationalInput());
		conf.add(processEmptyColumns());
		conf.add(rowCount());

		return conf;
	}

	public enum ConfigurationKey {
		TABLE,
		PROCESS_EMPTY_COLUMNS,
		INPUT_ROW_LIMIT,
	}

	private ConfigurationRequirement<?> relationalInput() {
		return new ConfigurationRequirementRelationalInput(ConfigurationKey.TABLE.name(),
				ConfigurationRequirement.ARBITRARY_NUMBER_OF_VALUES);
	}

	private ConfigurationRequirement<?> processEmptyColumns() {
		final ConfigurationRequirementBoolean requirement = new ConfigurationRequirementBoolean(
				ConfigurationKey.PROCESS_EMPTY_COLUMNS.name());
		requirement.setDefaultValues(new Boolean[]{defaultValues.isProcessEmptyColumns()});
		return requirement;
	}

	private ConfigurationRequirement<?> rowCount() {
		final ConfigurationRequirementInteger requirement = new ConfigurationRequirementInteger(ConfigurationKey.INPUT_ROW_LIMIT.name());
		requirement.setDefaultValues(new Integer[] { defaultValues.getInputRowLimit()});
		return requirement;
	}

	private ConfigurationRequirement<?> tableInput() {
		return new ConfigurationRequirementTableInput(
				ConfigurationKey.TABLE.name(),
				ConfigurationRequirement.ARBITRARY_NUMBER_OF_VALUES);
	}


	@Override
	public void setStringConfigurationValue(String identifier, String... values) throws AlgorithmConfigurationException {
		if (MyOdDetector.Identifier.SOME_STRING_PARAMETER.name().equals(identifier))
			this.someStringParameter = values[0];
		else
			this.handleUnknownConfiguration(identifier, values);
	}

	@Override
	public void setIntegerConfigurationValue(String identifier, Integer... values) throws AlgorithmConfigurationException {
		if (ConfigurationKey.INPUT_ROW_LIMIT.name().equals(identifier))
			//this.someIntegerParameter = values[0];
			builder.inputRowLimit(values[0]);
		else
			this.handleUnknownConfiguration(identifier, values);
	}

	@Override
	public void setBooleanConfigurationValue(String identifier, Boolean... values) throws AlgorithmConfigurationException {
		if (ConfigurationKey.PROCESS_EMPTY_COLUMNS.name().equals(identifier))
			//this.someBooleanParameter = values[0];
			builder.processEmptyColumns(values[0]);
		else
			this.handleUnknownConfiguration(identifier, values);
	}

	@Override
	public void setRelationalInputConfigurationValue(String identifier, RelationalInputGenerator... values) throws AlgorithmConfigurationException {
		if (ConfigurationKey.TABLE.name().equals(identifier)) {
			builder.relationalInputGenerators(asList(values));
		} else {
			this.handleUnknownConfiguration(identifier, values);
		}
	}

	@Override
	public void setResultReceiver(InclusionDependencyResultReceiver resultReceiver) {
		builder.resultReceiver(resultReceiver);
		//this.resultReceiver = resultReceiver;
	}

	@Override
	public void execute() throws AlgorithmExecutionException {
		final Configuration configuration = builder.build();
		impl.execute(configuration);
		//super.execute();
	}

	private void handleUnknownConfiguration(String identifier, Object[] values) throws AlgorithmConfigurationException {
		throw new AlgorithmConfigurationException("Unknown configuration: " + identifier + " -> [" + concat(values, ",") + "]");
	}
	
	private static String concat(Object[] objects, String separator) {
		if (objects == null)
			return "";
		
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < objects.length; i++) {
			buffer.append(objects[i].toString());
			if ((i + 1) < objects.length)
				buffer.append(separator);
		}
		return buffer.toString();
	}
}
