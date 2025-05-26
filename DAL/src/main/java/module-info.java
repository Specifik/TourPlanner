module medialibdal {
    requires java.desktop;
    requires java.sql;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.core;
    requires spring.beans;
    requires spring.data.jpa;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires spring.data.commons;
    requires org.apache.logging.log4j;
    requires com.fasterxml.jackson.annotation;

    exports at.fhtw.tourplanner.dal;
    exports at.fhtw.tourplanner.model;
    exports at.fhtw.tourplanner.dal.config;
    exports at.fhtw.tourplanner.dal.repository;

    opens at.fhtw.tourplanner.model;
    opens at.fhtw.tourplanner.dal.config;
    opens at.fhtw.tourplanner.dal.repository;
}
