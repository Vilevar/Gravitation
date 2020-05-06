package be.vilevar.gravitation;

import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

public class GSpinner extends Spinner<Integer> {

	public GSpinner(int min, int max, int current, int step) {
		SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, current, step);
		valueFactory.setConverter(new StringConverter<Integer>() {
			public String toString(Integer object) {
				return object.toString();
			}
			public Integer fromString(String string) {
				try {
					return Math.min(max, Math.max(min, Integer.parseInt(string)));
				} catch (Exception e) {
					return valueFactory.getValue();
				}
			}
		});
		this.setValueFactory(valueFactory);
		this.setEditable(true);
		TextField editor = this.getEditor();
		this.focusedProperty().addListener((obs, old, focused) -> {
			if(focused) {
				editor.selectAll();
			} else {
				StringConverter<Integer> converter = valueFactory.getConverter();
				valueFactory.setValue(converter.fromString(editor.getText()));
				editor.setText(converter.toString(valueFactory.getValue()));
			}
		});
	}
}
