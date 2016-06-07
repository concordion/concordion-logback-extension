# Customisation

## [Exception Logging](-)

Don't bother catching exceptions and logging them - this will do it for you...


    extension.setLogExceptions(LogLevel.EXCEPTION);

Options are:

* NONE: Do not log exceptions
* EXCEPTION: Log exception message
* EXCEPTION_CAUSES: Log exception message of the exception and all its causes (Default)

Note: see documentation on the two log styles for handling of [stack trace](- "printlog()").


## [Splitting Log Files](-)

Log files may be split either by specification or by example.

    @Extension
    private final LoggingFormatterExtension loggingExtension = new LoggingFormatterExtension(false).setSplitBy(Split.SPECIFICATION);


### By Specification
A link to the specifications log file is placed in the footer.


### By Example
A link to the log file is placed at the [top right](- "assertTrue=splitexample()") of each example - this is the default setting.  If there is no example, or log statements are made outside of an example then a link will also be placed in the footer.  

    
## [Logging Start and End of Examples](-)

Turned off by [default](- "printlog()").

    extension.setLogExampleStartAndEnd(true);

