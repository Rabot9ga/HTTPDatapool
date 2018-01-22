//import okhttp3.Request;
//import org.mockito.Mockito;
//import org.testng.annotations.BeforeMethod;
//import org.testng.annotations.Test;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import ru.sbt.util.HTTPDatapool.httpapi.*;
//import ru.sbt.util.HTTPDatapool.httpapi.Status;
//
//import java.io.IOException;
//import java.util.*;
//import java.util.stream.Collectors;
//
//import static org.testng.Assert.*;
//
//public class RequestAdderTest {
//
//    private MainController mainController;
//    private DatapoolRequest datapoolRequest;
//
//    @BeforeMethod
//    public void setUp() throws Exception {
//        Set<ParametersTable> set = new HashSet<>();
//        set.add(createParametersTable("tableName", RequestType.RANDOM, "AM_Create", "ID", "IND_NAME", "BIRTH_DATE"));
//        set.add(createParametersTable("tableName1", RequestType.RANDOM, "AM_Create", "ID", "IND_NAME", "BIRTH_DATE");
//        set.add(createParametersTable("tableName2", RequestType.RANDOM, "AM_Create", "ID", "IND_NAME", "BIRTH_DATE"));
//        set.add(createParametersTable("tableName3", RequestType.RANDOM, "AM_Create", "ID", "IND_NAME", "BIRTH_DATE"));
//
//        mainController = Mockito.mock(MainController.class);
//        datapoolRequest = DatapoolRequest.builder().parametersTables(set).build();
//
//        Map<String, String> map = new HashMap<>();
//        map.put("ID", "12341234512345");
//        map.put("IND_NAME", "Dfcz");
//        map.put("BIRTH_DATE", "321547-651321");
//
//        Map<ParametersTable, ResponseTables> collect = datapoolRequest.getParametersTablesStream().collect(Collectors.toMap(o -> o, o -> ResponseTables.builder().mapParameters(map).status(Status.SUCCESS).build()));
//
//        Response.success(DatapoolResponse.builder().responseTables(collect).build());
//
//        Call<DatapoolResponse> call = new Call<DatapoolResponse>() {
//            @Override
//            public Response<DatapoolResponse> execute() throws IOException {
//                return Response.success(DatapoolResponse.builder().responseTables(collect).build());
//            }
//
//            @Override
//            public void enqueue(Callback<DatapoolResponse> callback) {
//
//            }
//
//            @Override
//            public boolean isExecuted() {
//                return false;
//            }
//
//            @Override
//            public void cancel() {
//
//            }
//
//            @Override
//            public boolean isCanceled() {
//                return false;
//            }
//
//            @Override
//            public Call<DatapoolResponse> clone() {
//                return null;
//            }
//
//            @Override
//            public Request request() {
//                return null;
//            }
//        }
//
//        Mockito.when(mainController.getParameter(datapoolRequest)).thenReturn(call);
//
//
//    }
//
//    @Test
//    public void testGetParameters() throws Exception {
//        RequestAdder requestAdder = new RequestAdder(mainController);
//        ParameterList parameterList = requestAdder
//                .addRequest("tableName", RequestType.RANDOM, "AM_Create", "ID", "IND_NAME", "BIRTH_DATE")
//                .addRequest("tableName1", RequestType.RANDOM, "AM_Create", "ID", "IND_NAME", "BIRTH_DATE")
//                .addRequest("tableName2", RequestType.RANDOM, "AM_Create", "ID", "IND_NAME", "BIRTH_DATE")
//                .addRequest("tableName3", RequestType.RANDOM, "AM_Create", "ID", "IND_NAME", "BIRTH_DATE")
//                .getParameters();
//
//
//    }
//
//
//    private ParametersTable createParametersTable(String tableName, RequestType requestType, String scriptName, String... columnNames) {
//        Set<String> columnsName = Arrays.stream(columnNames).collect(Collectors.toSet());
//        return ParametersTable.builder()
//                .columnsName(columnsName)
//                .scriptName(scriptName)
//                .tableName(tableName)
//                .type(requestType)
//                .build();
//    }
//
//}