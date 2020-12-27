package pl.koziolekweb.ragecomicsmaker.gui.settings;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import org.controlsfx.control.PropertySheet;

import java.util.Optional;

public class StringProp<P extends StringProperty> implements PropertySheet.Item {
    private String category = null;
    private String description = null;
    private final String name;
    P underlyingProperty;

    public StringProp(String name, P prop) {
        this.name = name;
        this.underlyingProperty = prop;
    }

    public StringProp(String name, P prop, String description) {
        this(name, prop);
        this.description = description;
    }

    public StringProp(String name, P prop, String description, String category) {
        this(name, prop, description);
        this.category = category;
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
    public Optional<ObservableValue<?>> getObservableValue() {
        return Optional.of(underlyingProperty);
    }

    @Override
    public String getCategory() {
        return this.category;
    }
}

