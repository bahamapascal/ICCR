package org.iotacontrolcenter.iota.agent.action;

import org.iotacontrolcenter.persistence.PersistenceService;
import org.iotacontrolcenter.properties.locale.Localization;
import org.iotacontrolcenter.properties.source.PropertySource;

public abstract class AbstractAction {

    protected final Localization localization;
    protected final PersistenceService persistenceService;
    private final String[] propNames;
    protected final PropertySource propSource;

    protected AbstractAction(String[] propNames) {
        this.propNames = propNames;
        propSource = PropertySource.getInstance();
        localization = Localization.getInstance();
        persistenceService = PersistenceService.getInstance();
    }

    protected void preExecute() {
        checkRequiredProps();
        validatePreconditions();
    }

    private void checkRequiredProps() {
        if(propNames != null && propNames.length > 0) {
            for(String key : propNames) {
                String val = propSource.getString(key);
                if(val == null || val.isEmpty()) {
                    throw new IllegalStateException(localization.getLocalText("missingProperty") + ": " + key);
                }
            }
        }
    }

    protected void validatePreconditions() {
    }


}
