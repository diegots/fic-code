package tfg;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ContextTest {

    @Test
    public void getDefaultValuesTest() {
        Context context = new Context();

        int intResult = context.getInteger("IntVar", 123);
        assertEquals(123, intResult);

        String stringResult = context.getString("StringVar", "Foo");
        assertEquals("Foo", stringResult);
    }


    @Test
    public void getStringValueTest() {
        Context context = new Context();

        String name = "value";
        String stringValue = "Lorem ipsum dolor sit amet";
        context.putString(name, stringValue);

        String stringResult = context.getString(name, "Foo");
        assertEquals(stringValue, stringResult);
    }


    @Test
    public void getIntegerValueTest() {
        Context context = new Context();

        String name = "value";
        Integer integerValue = 718281828;
        context.putInteger(name, integerValue);

        Integer integerResult = context.getInteger(name, -1);
        assertEquals(integerValue, integerResult);
    }
}
