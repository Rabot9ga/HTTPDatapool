//import org.testng.annotations.BeforeMethod;
//import org.testng.annotations.Test;
//import retrofit2.Retrofit;
//import ru.sbt.util.HTTPDatapool.httpapi.DatapoolResponse;
//import ru.sbt.util.HTTPDatapool.httpapi.ParametersTable;
//import ru.sbt.util.HTTPDatapool.httpapi.RequestType;
//
//import java.util.HashSet;
//import java.util.Map;
//
//import static org.testng.Assert.*;
//
//public class HttpParameterTest {
//
//    @BeforeMethod
//    public void setUp() throws Exception {
//        Retrofit retrofit
//    }
//
//    @Test
//    public void getParameter() throws Exception {
//
//        ru.sbt.util.HTTPDatapool.httpparameter.ParameterList parameterList = ru.sbt.util.HTTPDatapool.httpparameter.HttpParameter.getInstance("http://localhost:8080")
//                .addRequest("tableName", RequestType.RANDOM, "AM_Create", "ID", "IND_NAME", "BIRTH_DATE")
//                .addRequest("tableName1", RequestType.RANDOM, "AM_Create", "ID", "IND_NAME", "BIRTH_DATE")
//                .addRequest("tableName2", RequestType.RANDOM, "AM_Create", "ID", "IND_NAME", "BIRTH_DATE")
//                .addRequest("tableName3", RequestType.RANDOM, "AM_Create", "ID", "IND_NAME", "BIRTH_DATE")
//                .getParameters();
//
//        String id = parameterList.getRequestValue(0).get("ID");
//
//
//    }
//}