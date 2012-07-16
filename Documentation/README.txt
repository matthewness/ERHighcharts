###################
#
# Overview
#
###################

ERHighcharts is a Wonder adapter framework for using the Highcharts
JavaScript libraries to create charts as SVG in Wonder applications.

The Highcharts library is a JavaScript charting library by HighSoft.

It is free for non-profit projects, but it is essential that commercial
and governmental projects purchase a license.

The full license details can be found here:
http://shop.highsoft.com/highcharts.html



The Highcharts JavaScript framework builds and renders an SVG object to
a container, which is defined by a JavaScript graph object. 

ERHighcharts defines an extensible graph using the KVCAExtensionGraph
class, which eventuates as the marshaled JSON/JavaScript object.

ERHighcharts comes with its own WORequestHandler for SVG transcoding,
which allows you to keep all report/export generation of PNG, JPEG, PDF
and SVG in-house, within the environment of your application. If you do
not use the custom request handler, the default Highcharts behaviour
sends your SVG to the Highsoft server for transcoding.


The ERHighcharts framework contains a base component - ERHighchart - and
convenience chart components:

ERAreaChart
ERBarChart
ERColumnChart
ERLineChart
ERPieChart
ERScatterChart
ERSplineChart

These are convenience components which can take bindings for
EOGenericRecord/EOCustomObject objects and collections, allowing you to
easily create graphs and charts.

The developer may also build their own graph in a more hands on approach
by using WOString or WOJavaScript components directly, or by using only
the core ERHighchart component.


###################
#
# Usage
#
###################

For example, you may have a Company EO, with a to-many relationship to
Department EO: A Company has many Departments. A Department has a
to-many relationship to FleetVehicle EO, "fleet".

You can create a simple pie chart:

<wo:ERPieChart series="$company.departments" mappings="$mappings" />

Where mappings is a simple NSDictionary mapping a keypath to a
(Highcharts) JSON key:

	public NSDictionary<String, String> mappings() {
		return new NSDictionary<String, String>("fleet.count", "y");
	}
	
This will generate a pie chart where each pie wedge represents a
department, and each wedge value is the number of FleetVehicles in that
department.

The "y" key/value here represents part of the "series data" which
Highcharts expects. In highcharts parlance, the "y" represents the value
of the point configuration object(s) (see #Options)

In this example, ERPieChart takes the bindings for series and mappings,
and transfers the values into a KVCExtensionsGraph object. This object
is used to build the final chart definition document in JSON, which
Highcharts uses to do its job.

The above example is in ERHighchartsDemo Wonder application.



If you want to override and extend any of the options in the chart, you
bind a KVCAExtensionGraph object to the binding 'graph'.

<wo:ERPieChart 
	graph="$graph" 
	series="$company.departments"
	mappings="$mappings" />

A KVCAExtensionGraph object allows you to build an object key value
graph of arbitrary path depth. It's a useful class outside of
ERHighcharts, but for now it is bundled inside the ERHighcharts
framework.

Just as you may use key value coding to retrieve values down a EO graph,
perhaps like this:

NSArray<FleetVehicle> totalFleet = ERXArrayUtilities.flatten(
company().valueForKeyPath("departments.fleet") );

KVCAExtensionGraph allows you to set values on it against a key path, 
one that may not necessarily already exist:

KVCAExtensionGraph graph = new KVCAExtensionGraph();
graph.takeValueForKeyPath("string value 1", "stack.color");
graph.takeValueForKeyPath("Aaron", "person.firstName");
graph.takeValueForKeyPath("Aardvark", "person.lastName");
graph.takeValueForKeyPath(company().departments(),
"some.arbitrary.path.can.be.added");
graph.takeValueForKeyPath(234.56, "timeentry.hours");
graph.takeValueForKeyPath(new Foo("my Foo", -1234), "person.pet");
graph.takeValueForKeyPath(true, "toggle.on");
graph.takeValueForKeyPath(someOtherGraph, "timeentry.history");


This allows you to fine-tune each options graph if you wish. 
For example, the default value for the option "allow decimals" on the
"xAxis" in the Highcharts options is <true>.

You can override this in ERHighcharts by adding this to your graph:

graph.takeValueForKeyPath(false, "xAxis.allowDecimals");

or perhaps:

graph.takeValueForKeyPath(shouldAllowDecimals(), "xAxis.allowDecimals");


Further examples: You can set the title and subtitle of your chart:

graph.takeValueForKeyPath("Department Fleet Numbers" , "title.text");
graph.takeValueForKeyPath("2012 - 2013" , "subtitle.text");


You may also set a JavaScript function object (ERHighchartsFunction) on
a key path, which will render as a true JavaScript function when the
JSON is built:

graph.takeValueForKeyPath(
	new ERHighchartsFunction(
		"function() { return 'The value for <b>'+ this.x + "+
		"'</b> is <b>'+ this.y +'</b>';}"
	), 
	"tooltip.formatter");

(broken out for README page wrapping)

Why in that example do you have to punch in "function() { .. }" ?
Because you can also specify the function name instead:

graph.takeValueForKeyPath(
	new ERHighchartsFunction("myTooltipFunction"),
	"tooltip.formatter");

And complete your function separately in JavaScript elsewhere.

However, you must use the ERHighchartsFunction object for
functions/closures, otherwise the JSON output will quote the value you
set, rendering it pointless/a JSON string.


###################
#
# Options
#
###################


There are many (many!) options in the JavaScript Highcharts library
which may be used and overridden, a full list is here:

http://www.highcharts.com/ref/


Some important options are:

Series data:

http://www.highcharts.com/ref/#series--data

Point configuration object:

http://www.highcharts.com/ref/#point



###################
#
# Wonder Dependencies
#
###################


o  Ajax.framework


###################
#
# Notes
#
###################

o  This framework uses WOOgnl bindings only. Sorry if that's upsetting.


###################
#
# TO DO
#
###################

o  Expand the convenience chart bindings reference to allow for non-kvc
   series values. This can be used now by setting the values in the
   bound graph rather than a key path mapping.
o  Expand the series data collection to implement all point
   configuration object values.
o  er... more junit tests.


###################
#
# Acknowlegdements
#
###################


o  Highcharts

http://www.highcharts.com/
http://shop.highsoft.com/highcharts.html

Free for non profit: http://creativecommons.org/licenses/by-nc/3.0/
Commercial and Government: http://shop.highsoft.com/highcharts.html


o  Jackson

Apache License 2.0 (AL 2.0)
Lesser/Library General Public License (LGPL 2.1)


o  Batik

Apache License 2.0
http://www.apache.org/licenses/
                        