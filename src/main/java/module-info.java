module com.aesthetic.wrangler {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.aesthetic.wrangler to javafx.fxml;
    exports com.aesthetic.wrangler;
    exports com.aesthetic.wrangler.controllers;
    opens com.aesthetic.wrangler.controllers to javafx.fxml;
    exports com.aesthetic.wrangler.controllers.helpers;
    opens com.aesthetic.wrangler.controllers.helpers to javafx.fxml;
}