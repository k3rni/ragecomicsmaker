package pl.koziolekweb.ragecomicsmaker.gui;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import org.controlsfx.control.PropertySheet;

import javax.annotation.Nullable;
import java.util.Optional;

class StringProp<P extends StringProperty> implements PropertySheet.Item {
    private String description = null;
    private String name;
    P underlyingProperty;

    public StringProp(String name, P prop) {
        this.name = name;
        this.underlyingProperty = prop;
    }

    public StringProp(String name, P prop, String description) {
        this(name, prop);
        this.description = description;
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
        underlyingProperty.set((String) value);
    }

    @Override
    public Class<?> getType() {
        return String.class;
    }

    @Override
    public Optional<ObservableValue<? extends Object>> getObservableValue() {
        return Optional.of(underlyingProperty);
    }

    @Override
    public String getCategory() {
        return null;
    }
}

