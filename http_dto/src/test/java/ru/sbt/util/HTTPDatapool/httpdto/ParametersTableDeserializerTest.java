package ru.sbt.util.HTTPDatapool.httpdto;

import com.fasterxml.jackson.databind.cfg.DeserializerFactoryConfig;
import com.fasterxml.jackson.databind.deser.BeanDeserializerFactory;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashSet;

public class ParametersTableDeserializerTest {

    private ParametersTable parametersTable;
    private ParametersTableDeserializer parametersTableDeserializer;

    @BeforeMethod
    public void setUp() throws Exception {
        HashSet<String> strings = new HashSet<>();
        strings.add("ID");
        strings.add("JNBACDK_klsdjvn1");
        strings.add("LJNDS_KJN1NLN");


        parametersTable = ParametersTable.builder()
                .type(RequestType.RANDOM)
                .tableName("Script1_26")
                .scriptName("Script1_27")
                .columnsName(strings)
                .build();
        parametersTableDeserializer = new ParametersTableDeserializer();
    }

    @Test
    public void testDeserializeKey() throws Exception {
        DefaultDeserializationContext.Impl impl = new DefaultDeserializationContext.Impl(new BeanDeserializerFactory(new DeserializerFactoryConfig()));

        ParametersTable parametersTableAfter = parametersTableDeserializer.deserializeKey(this.parametersTable.toString(), impl);

        Assert.assertEquals(parametersTableAfter, this.parametersTable);
    }
}