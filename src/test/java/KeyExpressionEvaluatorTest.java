import com.tabariyya.synCache.annotations.KeyExpressionEvaluator;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class KeyExpressionEvaluatorTest {

    // helper simulating aspect invocation
    private Object eval(String expr, Object result, Object... args) throws Exception {
        Method m = TestMethods.class.getDeclaredMethod(
                "method",
                Object.class, Object.class, Object.class, Object.class
        );
        return KeyExpressionEvaluator.eval(expr, m, args, result);
    }

    // parameter names must match expressions
    static class TestMethods {
        public void method(Object user_id, Object user, Object profile, Object data) {}
    }

    // ---------- tests ----------

    @Test
    void testEvalExprPlainString() throws Exception {
        Object result = eval("plain_string", null);
        assertEquals("plain_string", result);
    }

    @Test
    void testEvalExprKwarg() throws Exception {
        Object result = eval("#user_id", null, 123);
        assertEquals("123", result);
    }

    static class User {
        public int id = 456;
        public String name = "Jane";
    }

    @Test
    void testEvalExprObjectAttribute() throws Exception {
        User user = new User();

        Object r1 = eval("#user.id", null, null, user);
        assertEquals("456", r1);

        Object r2 = eval("#user.name", null, null, user);
        assertEquals("Jane", r2);

        Object r3 = eval("#user.name-#user.id", null, null, user);
        assertEquals("Jane-456", r3);
    }

    static class ResultObj {
        public int id = 789;
        public String data = "result_data";
    }

    @Test
    void testEvalExprResultAttribute() throws Exception {
        ResultObj resultObj = new ResultObj();

        Object r = eval("#result.id", resultObj);
        assertEquals("789", r);
    }

    static class Profile {
        public User user = new User();
    }

    @Test
    void testEvalExprNestedAttribute() throws Exception {
        Profile profile = new Profile();
        profile.user.id = 999;

        Object r = eval("#profile.user.id", null, null, null, profile);
        assertEquals("999", r);
    }

    @Test
    void testEvalExprDictAccess() throws Exception {
        Map<String, Object> meta = new HashMap<>();
        meta.put("role", "admin");

        Map<String, Object> data = new HashMap<>();
        data.put("id", 111);
        data.put("name", "DictUser");
        data.put("meta", meta);

        Object r1 = eval("#data.id", null, null, null, null, data);
        assertEquals("111", r1);

        Object r2 = eval("#data.meta.role", null, null, null, null, data);
        assertEquals("admin", r2);
    }
}
