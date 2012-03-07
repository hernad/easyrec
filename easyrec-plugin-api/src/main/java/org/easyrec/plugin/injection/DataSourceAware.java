package org.easyrec.plugin.injection;

import javax.sql.DataSource;

public interface DataSourceAware {
    public void setDataSource(DataSource datasource);
}
