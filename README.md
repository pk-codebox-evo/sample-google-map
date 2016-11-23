# Google Maps

This is an example of working with Google Maps using the CUBA platform [Charts and Maps premium add-on](https://www.cuba-platform.com/add-ons).

The application has three entities with corresponding screens:

* `Territory` - defines a territory as a polygon on the map.
* `Salesperson` - a salesperson assigned to a territory. Use its editor screen to upload a picture.
* `Order` - contains date, amount and the reference to a salesperson.

The *Map* screen displays the map with the center defined in `web-app.properties` by the `charts.map.defaultLatitude` and `charts.map.defaultLongitude` application properties. If you click to a salesperson picture, the map will display the corresponding territory polygon. If you click to a polygon, a modal window will be shown. The window contains salesperson details and a chart with data about related orders.

Based on CUBA Platform 6.3.3