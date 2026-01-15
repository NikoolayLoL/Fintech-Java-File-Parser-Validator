public class RunAllTests {
    
    public static void main(String[] args) throws Exception {
        GenericFileParserTest.main(args);
        System.out.println();
        
        ValidatorTest.main(args);
        System.out.println();
        
        NotNullValidationStrategyTest.main(args);
        System.out.println();
        
        RegexValidationStrategyTest.main(args);
        System.out.println();
        
        RangeValidationStrategyTest.main(args);
    }
}
