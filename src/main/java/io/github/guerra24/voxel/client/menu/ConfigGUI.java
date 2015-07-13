/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Guerra24
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.guerra24.voxel.client.menu;

import io.github.guerra24.voxel.client.kernel.KernelConstants;
import io.github.guerra24.voxel.client.kernel.util.Logger;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class ConfigGUI extends JFrame implements ItemListener {

	public boolean ready = false;

	private static final long serialVersionUID = 1L;
	private File configFile = new File("assets/game/settings.conf");
	private String[] waterQualityList = { "Low", "Medium", "High", "Ultra" };
	private Properties configProps;

	private JCheckBox customSeed = new JCheckBox("Custom Seed");
	private JCheckBox vsync = new JCheckBox("Vsync");

	private JComboBox waterQuality = new JComboBox(waterQualityList);

	private JLabel labelWIDTH = new JLabel("Width: ");
	private JLabel labelHEIGHT = new JLabel("Height: ");
	private JLabel labelFOV = new JLabel("FOV: ");
	private JLabel labelFPS = new JLabel("FPS: ");
	private JLabel labelSEED = new JLabel("Seed: ");
	private JLabel labelviewDistance = new JLabel("World Size: ");
	private JLabel labelwaterQuality = new JLabel("Water Quality: ");

	private JTextField textWIDTH = new JTextField(20);
	private JTextField textHEIGHT = new JTextField(20);
	private JTextField textFOV = new JTextField(20);
	private JTextField textFPS = new JTextField(20);
	private JTextField textSEED = new JTextField(20);
	private JTextField textviewDistance = new JTextField(20);

	private JButton buttonSave = new JButton("Save and Continue");

	public ConfigGUI() {
		super("Game Settings");
		Logger.log(Thread.currentThread(), "Starting Settings GUI");
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets = new Insets(10, 10, 5, 10);
		constraints.anchor = GridBagConstraints.WEST;

		add(labelWIDTH, constraints);

		constraints.gridx = 1;
		add(textWIDTH, constraints);

		constraints.gridy = 1;
		constraints.gridx = 0;
		add(labelHEIGHT, constraints);

		constraints.gridx = 1;
		add(textHEIGHT, constraints);

		constraints.gridy = 2;
		constraints.gridx = 0;
		add(labelFOV, constraints);

		constraints.gridx = 1;
		add(textFOV, constraints);

		constraints.gridy = 3;
		constraints.gridx = 0;
		add(labelFPS, constraints);

		constraints.gridx = 1;
		add(textFPS, constraints);

		constraints.gridx = -1;
		vsync.setMnemonic(KeyEvent.VK_C);
		vsync.setSelected(false);
		add(vsync, constraints);
		vsync.addItemListener(this);

		constraints.gridy = 4;
		constraints.gridx = 0;
		add(labelSEED, constraints);

		constraints.gridx = 1;
		add(textSEED, constraints);

		constraints.gridx = -1;
		customSeed.setMnemonic(KeyEvent.VK_G);
		customSeed.setSelected(false);
		add(customSeed, constraints);
		customSeed.addItemListener(this);

		constraints.gridy = 5;
		constraints.gridx = 0;
		add(labelviewDistance, constraints);

		constraints.gridx = 1;
		add(textviewDistance, constraints);

		constraints.gridy = 6;
		constraints.gridx = 0;
		add(labelwaterQuality, constraints);

		constraints.gridx = 1;
		waterQuality.setSelectedIndex(3);
		add(waterQuality, constraints);
		waterQuality.addActionListener(new ActionListener() {
			@SuppressWarnings("rawtypes")
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox) e.getSource();
				String quality = (String) cb.getSelectedItem();
				updateLabel(quality);
			}

			private void updateLabel(String quality) {
				if (quality.equals("Low")) {
					KernelConstants.REFLECTION_WIDTH = 128;
					KernelConstants.REFLECTION_HEIGHT = 64;
				} else if (quality.equals("Medium")) {
					KernelConstants.REFLECTION_WIDTH = 256;
					KernelConstants.REFLECTION_HEIGHT = 128;
				} else if (quality.equals("High")) {
					KernelConstants.REFLECTION_WIDTH = 512;
					KernelConstants.REFLECTION_HEIGHT = 256;
				} else if (quality.equals("Ultra")) {
					KernelConstants.REFLECTION_WIDTH = 1024;
					KernelConstants.REFLECTION_HEIGHT = 512;
				}
			}
		});

		constraints.gridy = 7;
		constraints.gridx = 0;
		constraints.gridwidth = 3;
		constraints.anchor = GridBagConstraints.CENTER;
		add(buttonSave, constraints);

		buttonSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					saveProperties();
					sendProperties();
					JOptionPane.showMessageDialog(ConfigGUI.this,
							"Settings were saved successfully!");
					ready = true;
					dispose();
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(ConfigGUI.this,
							"Error saving settings file: " + ex.getMessage());
				}
			}
		});

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		setResizable(false);

		try {
			loadProperties();
		} catch (IOException ex) {
			JOptionPane
					.showMessageDialog(this,
							"The settings.conf file does not exist, default settings loaded.");
		}
		textWIDTH.setText(configProps.getProperty("WIDTH"));
		textHEIGHT.setText(configProps.getProperty("HEIGHT"));
		textFOV.setText(configProps.getProperty("FOV"));
		textFPS.setText(configProps.getProperty("FPS"));
		textSEED.setText(configProps.getProperty("SEED"));
		textviewDistance.setText(configProps.getProperty("ViewDistance"));
	}

	public void itemStateChanged(ItemEvent e) {
		Object source = e.getItemSelectable();
		if (source == customSeed) {
			KernelConstants.isCustomSeed = true;
		} else if (source == vsync) {
			KernelConstants.VSYNC = true;
		}
		if (e.getStateChange() == ItemEvent.DESELECTED) {
			if (source == customSeed) {
				KernelConstants.isCustomSeed = false;
			} else if (source == vsync) {
				KernelConstants.VSYNC = false;
			}
		}
	}

	private void sendProperties() {
		KernelConstants.WIDTH = Integer.parseInt(configProps
				.getProperty("WIDTH"));
		KernelConstants.HEIGHT = Integer.parseInt(configProps
				.getProperty("HEIGHT"));
		KernelConstants.FOV = Integer.parseInt(configProps.getProperty("FOV"));
		KernelConstants.FPS = Integer.parseInt(configProps.getProperty("FPS"));
		KernelConstants.VSYNC = Boolean.parseBoolean(configProps
				.getProperty("VSYNC"));
		KernelConstants.seed = configProps.getProperty("SEED");
	}

	private void loadProperties() throws IOException {
		Properties defaultProps = new Properties();
		// sets default properties
		defaultProps.setProperty("WIDTH", "1280");
		defaultProps.setProperty("HEIGHT", "720");
		defaultProps.setProperty("FOV", "90");
		defaultProps.setProperty("FPS", "60");
		defaultProps.setProperty("VSYNC", "false");
		defaultProps.setProperty("SEED", "");
		defaultProps.setProperty("ViewDistance", "8");

		configProps = new Properties(defaultProps);

		// loads properties from file
		InputStream inputStream = new FileInputStream(configFile);
		configProps.load(inputStream);
		if (configProps.getProperty("VSYNC").equals("true")) {
			vsync.setSelected(true);
		}
		inputStream.close();
	}

	private void saveProperties() throws IOException {
		configProps.setProperty("WIDTH", textWIDTH.getText());
		configProps.setProperty("HEIGHT", textHEIGHT.getText());
		configProps.setProperty("FOV", textFOV.getText());
		configProps.setProperty("FPS", textFPS.getText());
		configProps.setProperty("VSYNC",
				Boolean.toString(KernelConstants.VSYNC));
		configProps.setProperty("SEED", textSEED.getText());
		configProps.setProperty("ViewDistance", textviewDistance.getText());
		OutputStream outputStream = new FileOutputStream(configFile);
		configProps.store(outputStream, "Game Settings");
		outputStream.close();
	}

}