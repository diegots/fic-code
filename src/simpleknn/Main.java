package simpleknn;

import simpleknn.config.ToolConfig;

import java.util.List;

public class Main {



    public static void main(String[] args) {

        System.out.println(Controller.TAG + "[Starting]");
        new Controller(args).run();

    }
}
