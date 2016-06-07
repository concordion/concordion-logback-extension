# Customisation

## [Exception Logging](-)

Don't bother catching exceptions and logging them - this will do it for you...


    extension.setLogExceptions(LogLevel.EXCEPTION);

Options are:

* NONE: Do not log exceptions
* EXCEPTION: Log exception message
* EXCEPTION_CAUSES: Log exception message of the exception and all its causes (Default)

Note: see documentation on the two log styles for handling of [stack trace](- "printlog()").


## [Log Splitting](-)

By default a new log file will be created for each example and a link to the example's log file will be placed on the [top right of the example](- "loginexample()").  Any log statements made outside of an example will be placed in a log file for the specification and linked to from the footer of the specification.

It is possible to have a single log file for the specification.

    extension.setSplitBy(Split.SPECIFICATION);


_TODO: Seems that is adding logs to both example and specification logs :-(

    
## [Logging Start and End of Examples](-)

Turned off by [default](- "printlog()").

    extension.setLogExampleStartAndEnd(true);

