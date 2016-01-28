package example;

import java.util.List;

public class DeepList {

    private String fieldL1;
    private List<Level2> listL1;
    
    
    class Level2 {
        private String fieldL2;
        private List<Level3> listL2;
    }
    
    class Level3 {
        private String fieldL3;
    }
}
