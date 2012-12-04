Validate
=========

Playframework 1.2 module to handle annotation based validation everywhere, not only in controllers.

If you would like to have support for data validation in method parameters using Play annotations
or your custom validators already used in models and controllers this is a plugin for you.

Usage
-----
To use validation plugin simply add it to your application (see [Installation](#Installation))
and use validation annotations for method parameters wherever you like (not only in controllers), ex:
```@Required @Min @Date @Email @InPast``` etc. all will work just fine.
Also custom validation with ```@CheckWith(YourCheck.class)``` or custom annotation works with no custom code required.

(You can find more details about them [here](http://www.playframework.org/documentation/1.2.5/validation#annotations))

By default plugin will throw ```ValidationException``` (a runtime exception so you don't
need to handle it on all code levels) that contain stock Play ```Error``` objects that you can later handle
it however you like (even add them to stock Play validation errors using one exception method).

If what you need is exactly that you can configure plugin so it does fill current validation object
with errors instead of throwing exceptions. There are 2 ways to do it:

* Set ```validation.throwException``` property in ```application.conf```<br>
This will make it default for every validated method in your app

* Use ```@Validate``` annotation on validated method with ```throwException``` parameter<br>
This will override default or property configured behavior for this single method. Just in case you need it.

How does it work
----------------
Validate module connects to Play compilation process and checks for all methods that have validation annotations
on parameters. In every such method a new line of code is injected in method that will process
all these parameters on runtime and check if they conform validation requirements.

Installation
------------
Add repository and dependency to your dependencies.yml:
  
    require:
		- play
		- pl.com.tt.play.modules -> validate 1.0
	repositories:
		- pl.com.tt.play.modules:
			type:       http
			artifact:   http://cloud.github.com/downloads/transition-technologies/[module]/[module]-[revision].zip
			descriptor: http://cloud.github.com/downloads/transition-technologies/[module]/[module]-[revision].yml
			contains:
				- pl.com.tt.play.modules -> *