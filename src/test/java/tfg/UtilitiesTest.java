package tfg;

import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class UtilitiesTest {

    @Test
    public void processByLineTest() {

        ProcessFile processFileMock = mock(ProcessFile.class);

        String path = "src/test/java/resources/processByLineTestFile.txt";
        Utilities.processByLine(new File(path), processFileMock);

        verify(processFileMock, Mockito.times(1)).treatLine("1,31,2.5,1260759144");
        verify(processFileMock, Mockito.times(1)).treatLine("100,1029,3.0,1260759179");
        verify(processFileMock, Mockito.times(1)).treatLine("20,1061,3.0,1260759182");
        verify(processFileMock, Mockito.times(1)).treatLine("2,1129,2.0,1260759185");
        verify(processFileMock, Mockito.times(1)).treatLine("9,1172,4.0,1260759205");

        verify(processFileMock, Mockito.times(0)).getResultsAfterFile();
    }

}
