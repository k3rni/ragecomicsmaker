package pl.koziolekweb.ragecomicsmaker.gui.settings;

import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import org.controlsfx.control.PropertySheet;

import java.util.Optional;

public class BoolProp<P extends BooleanProperty> implements PropertySheet.Item {
    private String category;
    private String description = null;
    private String name;
    P underlyingProperty;

    public BoolProp(String name, P prop) {
        this.name = name;
        this.underlyingProperty = prop;
    }

    public BoolProp(String name, P prop, String description) {
        this(name, prop);
        this.description = description;
    }

    public BoolProp(String name, P prop, String description, String category) {
        this(name, prop, description);
        this.category = category;
    }

    @Override
    public Class<?> getType() {
        return Boolean.class;
    }

    @Override
    public String getCategory() {
        return this.category;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public Object getValue() {
        return underlyingProperty.get();
    }

    @Override
    public void setValue(Object value) {
        underlyingProperty.set((Boolean) value);
    }

    @Override
    public Optional<ObservableValue<? extends Object>> getObservableValue() {
        return Optional.of(underlyingProperty);
    }
}
