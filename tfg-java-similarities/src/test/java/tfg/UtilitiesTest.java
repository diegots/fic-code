package tfg;

import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class UtilitiesTest {

    @Test
    public void processByLineTest() {

        LinesHandler linesHandlerMock = mock(LinesHandler.class);

        String path = "src/test/java/resources/processByLineTestFile.txt";
        Utilities.processByLine(new File(path), linesHandlerMock);

        verify(linesHandlerMock, Mockito.times(1)).handle("1,31,2.5,1260759144");
        verify(linesHandlerMock, Mockito.times(1)).handle("100,1029,3.0,1260759179");
        verify(linesHandlerMock, Mockito.times(1)).handle("20,1061,3.0,1260759182");
        verify(linesHandlerMock, Mockito.times(1)).handle("2,1129,2.0,1260759185");
        verify(linesHandlerMock, Mockito.times(1)).handle("9,1172,4.0,1260759205");

        verify(linesHandlerMock, Mockito.times(0)).getResults();
    }

}
