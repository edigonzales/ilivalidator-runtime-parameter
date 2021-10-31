package ch.so.agi.ilivalidator.ext;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.ehi.basics.settings.Settings;
import ch.interlis.ili2c.config.Configuration;
import ch.interlis.ili2c.config.FileEntry;
import ch.interlis.ili2c.config.FileEntryKind;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.Iom_jObject;
import ch.interlis.iox_j.EndBasketEvent;
import ch.interlis.iox_j.EndTransferEvent;
import ch.interlis.iox_j.ObjectEvent;
import ch.interlis.iox_j.PipelinePool;
import ch.interlis.iox_j.StartBasketEvent;
import ch.interlis.iox_j.StartTransferEvent;
import ch.interlis.iox_j.logging.LogEventFactory;
import ch.interlis.iox_j.validator.ValidationConfig;
import ch.interlis.iox_j.validator.Validator;

public class RuntimeDummyIoxPluginTest {
    private TransferDescription td=null;
    private final static String OBJ_OID1 ="o1";
    private final static String ILI_TOPIC="Testmodel.Topic";
    private final static String ILI_CLASSA=ILI_TOPIC+".ClassA";
    private final static String ILI_CLASSD=ILI_TOPIC+".ClassD";
    
    private final static String BID1="b1";
    
    @BeforeEach
    public void setUp() throws Exception {
        Configuration ili2cConfig = new Configuration();
        {
            FileEntry fileEntry = new FileEntry("src/test/data/SO_FunctionsExt.ili", FileEntryKind.ILIMODELFILE);
            ili2cConfig.addFileEntry(fileEntry);
        }
        {
            FileEntry fileEntry = new FileEntry("src/test/data/Testmodel.ili", FileEntryKind.ILIMODELFILE);
            ili2cConfig.addFileEntry(fileEntry);
        }
        td = ch.interlis.ili2c.Ili2c.runCompiler(ili2cConfig);
        assertNotNull(td);
    }

    @Test
    public void filename_Ok() {
        Iom_jObject iomObjA = new Iom_jObject(ILI_CLASSA, OBJ_OID1);
        iomObjA.setattrvalue("attr2", "SO0200002601_3396.xml");
        ValidationConfig modelConfig = new ValidationConfig();
        modelConfig.mergeIliMetaAttrs(td);
        LogCollector logger = new LogCollector();
        LogEventFactory errFactory = new LogEventFactory();
        Settings settings = new Settings();
        Map<String,Class> newFunctions = new HashMap<String,Class>();
        newFunctions.put("SO_FunctionsExt.RuntimeDummy", RuntimeDummyIoxPlugin.class);
        settings.setTransientObject(Validator.CONFIG_CUSTOM_FUNCTIONS, newFunctions);
        td.setActualRuntimeParameter(ch.interlis.ili2c.metamodel.RuntimeParameters.MINIMAL_RUNTIME_SYSTEM01_CURRENT_TRANSFERFILE, "SO0200002601_3396.xml");
        Validator validator=new Validator(td, modelConfig, logger, errFactory, new PipelinePool(), settings);
        validator.validate(new StartTransferEvent());
        validator.validate(new StartBasketEvent(ILI_TOPIC,BID1));
        validator.validate(new ObjectEvent(iomObjA));
        validator.validate(new EndBasketEvent());
        validator.validate(new EndTransferEvent());
                
        assertTrue(logger.getErrs().size()==0);
    }
    
    @Test
    public void filename_Fail() {
        Iom_jObject iomObjA = new Iom_jObject(ILI_CLASSA, OBJ_OID1);
        iomObjA.setattrvalue("attr2", "fubar.xml");
        ValidationConfig modelConfig = new ValidationConfig();
        modelConfig.mergeIliMetaAttrs(td);
        LogCollector logger = new LogCollector();
        LogEventFactory errFactory = new LogEventFactory();
        Settings settings = new Settings();
        Map<String,Class> newFunctions = new HashMap<String,Class>();
        newFunctions.put("SO_FunctionsExt.RuntimeDummy", RuntimeDummyIoxPlugin.class);
        settings.setTransientObject(Validator.CONFIG_CUSTOM_FUNCTIONS, newFunctions);
        td.setActualRuntimeParameter(ch.interlis.ili2c.metamodel.RuntimeParameters.MINIMAL_RUNTIME_SYSTEM01_CURRENT_TRANSFERFILE, "SO0200002601_3396.xml");
        Validator validator=new Validator(td, modelConfig, logger, errFactory, new PipelinePool(), settings);
        validator.validate(new StartTransferEvent());
        validator.validate(new StartBasketEvent(ILI_TOPIC,BID1));
        validator.validate(new ObjectEvent(iomObjA));
        validator.validate(new EndBasketEvent());
        validator.validate(new EndTransferEvent());
        
        assertTrue(logger.getErrs().size()==1);
    }    
}
