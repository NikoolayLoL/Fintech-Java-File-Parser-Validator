package test;

public class RunAllTests {
    
    public static void main(String[] args) throws Exception {
        System.out.println("================================================================================");
        System.out.println("                        REFLECTIVE DATA ENGINE TEST SUITE");
        System.out.println("================================================================================\n");
        
        GenericFileParserTest.main(args);
        System.out.println();
        
        ValidatorTest.main(args);
        System.out.println();
        
        NotNullValidationStrategyTest.main(args);
        System.out.println();
        
        RegexValidationStrategyTest.main(args);
        System.out.println();
        
        RangeValidationStrategyTest.main(args);
        
        System.out.println("\n================================================================================");
        System.out.println("                        ALL TESTS COMPLETED");
        System.out.println("================================================================================");
    }
}
