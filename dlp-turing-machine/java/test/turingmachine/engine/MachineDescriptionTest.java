/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package turingmachine.engine;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author diego
 */
public class MachineDescriptionTest {
    
    public MachineDescriptionTest() {}

    @Test
    public void testNext() throws Exception {
        
        // testin AB11L
        List l = new ArrayList();
        l.add("ABB1R");
        l.add("AB11L");
        l.add("BAB1L");
        l.add("BH11R");
        
        MachineDescription md = new MachineDescription(l);
        assertEquals("B1R", md.next("A", "B"));
        assertEquals("B1L", md.next("A", "1"));
        assertEquals("A1L", md.next("B", "B"));
        assertEquals("H1R", md.next("B", "1"));
    }
}
