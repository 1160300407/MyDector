package de.metanome.algorithms.myoddetector;

import de.metanome.algorithm_integration.input.RelationalInputGenerator;
import de.metanome.algorithm_integration.input.TableInputGenerator;
import de.metanome.algorithm_integration.result_receiver.InclusionDependencyResultReceiver;

import java.util.Collections;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Singular;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Configuration {
  //private static Object TableInputGenerator;
    @Singular
    private List<TableInputGenerator> tableInputGenerators;
    @Singular
    private List<RelationalInputGenerator> relationalInputGenerators;
    private boolean processEmptyColumns;
    private int inputRowLimit;

    private InclusionDependencyResultReceiver resultReceiver;

    public static Configuration withDefaults() {
        return builder().tableInputGenerators(Collections.<TableInputGenerator>emptyList())
                .relationalInputGenerators(Collections.<RelationalInputGenerator>emptyList())
                .processEmptyColumns(true)
                .inputRowLimit(-1)
                .resultReceiver(null)
                .build();
    }
}
