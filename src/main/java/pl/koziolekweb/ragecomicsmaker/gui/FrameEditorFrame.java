package pl.koziolekweb.ragecomicsmaker.gui;

import pl.koziolekweb.ragecomicsmaker.App;
import pl.koziolekweb.ragecomicsmaker.event.ErrorEvent;
import pl.koziolekweb.ragecomicsmaker.event.FrameStateChangeEvent;
import pl.koziolekweb.ragecomicsmaker.model.Frame;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * TODO write JAVADOC!!!
 * User: koziolek
 */
public class FrameEditorFrame extends JFrame {

	private final Frame frame;
	private final GridBagConstraints gbc;
	private final DoubleFormFieldPanel width;
	private final DoubleFormFieldPanel height;
	private final DoubleFormFieldPanel startX;
	private final DoubleFormFieldPanel startY;

	public FrameEditorFrame(Frame frame) {
		super("Set frame cords " + frame.getId());
		this.frame = frame;
		setLayout(new GridBagLayout());
		this.gbc = new GridBagConstraints();
		this.gbc.gridy = 0;

		width = new DoubleFormFieldPanel("Width", frame.getSizeX());
		gbc.gridx = 0;
		gbc.gridy = gbc.gridy + 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(width, gbc);

		height = new DoubleFormFieldPanel("Height", frame.getSizeY());
		gbc.gridx = 0;
		gbc.gridy = gbc.gridy + 1;
		add(height, gbc);

		startX = new DoubleFormFieldPanel("Start X", frame.getStartX());
		gbc.gridx = 0;
		gbc.gridy = gbc.gridy + 1;
		add(startX, gbc);

		startY = new DoubleFormFieldPanel("Start Y", frame.getStartY());
		gbc.gridx = 0;
		gbc.gridy = gbc.gridy + 1;
		add(startY, gbc);

		JButton cancel = new JButton("Cancel");
		cancel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				FrameEditorFrame.this.setVisible(false);
				FrameEditorFrame.this.dispose();
			}
		});
		gbc.gridx = 0;
		gbc.gridy = gbc.gridy + 1;
		add(cancel, gbc);

		JButton accept = new JButton("Accept");
		accept.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				FrameEditorFrame.this.accept();
				FrameEditorFrame.this.setVisible(false);
				FrameEditorFrame.this.dispose();
			}
		});
		gbc.gridx = 1;
		add(accept, gbc);

		setMinimumSize(new Dimension(350, 120));
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void accept() {
		frame.setSizeX(width.getValue());
		frame.setSizeY(height.getValue());
		frame.setStartX(startX.getValue());
		frame.setStartY(startY.getValue());
		App.EVENT_BUS.post(new FrameStateChangeEvent(frame));
	}

	abstract class FromFieldPanel<T> extends JPanel {

		protected final JTextField field;
		protected final JLabel label;

		public FromFieldPanel(String message, T value) {
			setLayout(new GridLayout(1, 2));
			setMinimumSize(new Dimension(150, 50));

			this.label = new JLabel(message);
			this.field = new JTextField(value + "");
			label.setBackground(Color.BLUE);
			add(label);
			add(field);
		}

		public abstract T getValue();
	}

	class DoubleFormFieldPanel extends FromFieldPanel<Double> {

		public DoubleFormFieldPanel(String message, Double value) {
			super(message, value);
		}

		@Override
		public Double getValue() {
			try {
				return Double.valueOf(field.getText());
			} catch (NumberFormatException e) {
				App.EVENT_BUS.post(new ErrorEvent(label.getText() + " not a number", e));
				throw e;
			}
		}
	}
}
