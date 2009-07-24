package chat.common;

/**
 *
 * @author eTeR
 */
public class ExceptionHandler {

    public static void HandleException(Exception ex)
    {
        System.out.println(String.format("/t%s",ex.toString()));
    }
}
