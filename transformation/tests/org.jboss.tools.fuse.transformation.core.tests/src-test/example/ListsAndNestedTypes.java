package example;

import java.util.List;

public class ListsAndNestedTypes {

    private Nested1 nested1;
    private Nested2 nested2;
    private List<AClass> listOfAs;
}

class Nested1 {
    private Object field1;
    private Object field2;
    private BClass classB;
}

class Nested2 {
    private Object field1;
    private Object field2;
    private BClass classB;
}
