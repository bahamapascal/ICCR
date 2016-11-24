package org.iotacontrolcenter.iota.agent.action;

import org.iotacontrolcenter.properties.source.PropertySource;

public abstract class AbstractAction {

    private PropertySource propSource;
    String[] propNames;

    protected AbstractAction(PropertySource propSource, String[] propNames) {
        this.propSource = propSource;
        this.propNames = propNames;
    }

    protected boolean haveRequiredProperties() {
        if(propNames != null && propNames.length > 0) {
            for(String key : propNames) {
                String val = propSource.getString(key);
                if(val == null || val.isEmpty()) {
                    throw new IllegalStateException("Missing value for property " + key);
                }
            }
        }
        return true;
    }

    protected boolean validatePreconditions();


}
