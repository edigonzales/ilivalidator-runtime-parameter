package ch.so.agi.ilivalidator.ext;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;

import ch.ehi.basics.settings.Settings;
import ch.interlis.ili2c.metamodel.RuntimeParameters;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.iom.IomObject;
import ch.interlis.iox.IoxValidationConfig;
import ch.interlis.iox_j.logging.LogEventFactory;
import ch.interlis.iox_j.validator.InterlisFunction;
import ch.interlis.iox_j.validator.ObjectPool;
import ch.interlis.iox_j.validator.Validator;
import ch.interlis.iox_j.validator.Value;

public class RuntimeDummyIoxPlugin implements InterlisFunction {

    private LogEventFactory logger = null;
    private TransferDescription td = null;
    private Validator validator = null;

    @Override
    public Value evaluate(String validationKind, String usageScope, IomObject mainObj, Value[] actualArguments) {
        if (actualArguments[0].skipEvaluation()) {
            return actualArguments[0];
        }
        if (actualArguments[0].isUndefined()) {
            return Value.createSkipEvaluation();
        }

        String value = actualArguments[0].getValue();
        
//        RuntimeParameters runtimeParams = td.getActualRuntimeParameters(); 
//        Set<String> names = runtimeParams.getNames();
//        for (String name : names) {
//            System.out.println(name + ": " + runtimeParams.getValue(name));
//        }
        
        String runtimeFileName = (String) td.getActualRuntimeParameter(ch.interlis.ili2c.metamodel.RuntimeParameters.MINIMAL_RUNTIME_SYSTEM01_CURRENT_TRANSFERFILE);
        if (value.equals(runtimeFileName)) {
            return new Value(true);
        } else {
            return new Value(false);
        }
    }

    @Override
    public String getQualifiedIliName() {
        return "SO_FunctionsExt.RuntimeDummy";
    }

    @Override
    public void init(TransferDescription td, Settings settings, 
            IoxValidationConfig validationConfig, ObjectPool objectPool, 
            LogEventFactory logEventFactory) {
        this.logger = logEventFactory;
        this.logger.setValidationConfig(validationConfig);
        this.td = td;
        this.validator = (Validator) settings.getTransientObject(this.IOX_VALIDATOR);
    }
}
