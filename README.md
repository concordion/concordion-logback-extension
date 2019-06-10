[![Build Status](https://travis-ci.org/concordion/concordion-logback-extension.svg?branch=master)](https://travis-ci.org/concordion/concordion-logback-extension)

This [Concordion](http://www.concordion.org) extension provides support for Logback logging.

The [demo project](http://github.com/concordion/concordion-logback-extension-demo) demonstrates this extension.

# Introduction

Provides [Logback](http://logback.qos.ch) logging support for concordion.

When using this extension I recommend not catching and logging exceptions yourself (unless you need to perform a specific action or stop the exception filtering back up to the specification) as this extension will automatically capture and log the exception.

This extension was originally developed once we started running our tests in parallel (using the latest update to cordion to allow this) and discovered that one interleaved log file was not particularly useful.  Even if you are not running tests in parallel the ability to click on a link in the specification to access the log file is very nice :-)

# Tooltip
---------
Adds the LogbackLogMessenger class to support the [tooltip extension](http://github.com/concordion/concordion-tooltip-extension)

# Unique Log Per Test
---------------------
The LoggingFormatterExtension places a link at the bottom right of each specification to the specifications log file
 
The log is presented wrapped by a log viewer page, this was an attempt to make the logging less scary for non developers and came before I thought of the [storyboard extension](http://github.com/concordion/concordion-storyboard-extension).  I'm a little unsure whether its an improvement or not.  There log viewer provides access to the raw log file as well. 

Your Logback configuration must have an active SiftingAppender with a discriminator key of "testname".  If found the extension will add a link at the bottom right of your specification that links to the log file.  This log file is assumed to be in the same location as the specification, and with the same base name, but ending with either '.log', or 'Log.html'.  See the demo project for an example.

# Further info

* [API](http://concordion.github.io/concordion-logback-extension/api/index.html)
* [Demo project](http://github.com/concordion/concordion-logback-extension-demo)
