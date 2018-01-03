package org.iotacontrolcenter.iccr.agent.action;

import org.iotacontrolcenter.persistence.PersistenceService;
import org.iotacontrolcenter.properties.locale.Localizer;
import org.iotacontrolcenter.properties.source.PropertySource;

public abstract class AbstractIccrAction {

    protected Localizer localizer;
    protected PersistenceService persister;
    protected String[] propNames;
    protected PropertySource propSource;

    protected AbstractIccrAction(String[] propNames) {
        this.propNames = propNames;
        propSource = PropertySource.getInstance();
        localizer = Localizer.getInstance();
        persister = PersistenceService.getInstance();
    }

    protected void preExecute() {
        checkRequiredProps();
        validatePreconditions();
    }

    protected void checkRequiredProps() {
        if(propNames != null && propNames.length > 0) {
            for(String key : propNames) {
                String val = propSource.getString(key);
                if(val == null || val.isEmpty()) {
                    throw new IllegalStateException(localizer.getLocalText("missingProperty") + ": " + key);
                }
            }
        }
    }

    protected void validatePreconditions() {
    }

}

