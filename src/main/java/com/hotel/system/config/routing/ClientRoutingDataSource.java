package com.hotel.system.config.routing;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.lang.Nullable;

public class ClientRoutingDataSource extends AbstractRoutingDataSource {

    @Override
    @Nullable
    protected Object determineCurrentLookupKey() {
        return DbContextHolder.getCurrentRole();
    }
}