package jd.gui.swing.jdgui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import org.appwork.storage.config.JsonConfig;
import org.appwork.storage.config.events.ConfigEventListener;
import org.appwork.storage.config.handler.KeyHandler;
import org.appwork.swing.components.ExtCheckBox;
import org.appwork.swing.components.SizeSpinner;
import org.appwork.utils.formatter.SizeFormatter;
import org.appwork.utils.swing.EDTRunner;
import org.jdownloader.gui.translate._GUI;
import org.jdownloader.images.NewTheme;
import org.jdownloader.settings.GeneralSettings;

public class SpeedlimitEditor extends MenuEditor implements ConfigEventListener, ChangeListener, ActionListener {

    private JLabel          lbl;
    private SizeSpinner     spinner;
    private GeneralSettings config;
    private ExtCheckBox     cb;

    public SpeedlimitEditor() {
        super();
        setLayout(new MigLayout("ins 0", "6[grow,fill][][]", "[grow,fill]"));

        setOpaque(false);
        config = JsonConfig.create(GeneralSettings.class);

        GeneralSettings.DOWNLOAD_SPEED_LIMIT.getEventSender().addListener(this, true);
        GeneralSettings.DOWNLOAD_SPEED_LIMIT_ENABLED.getEventSender().addListener(this, true);

        lbl = getLbl(_GUI._.SpeedlimitEditor_SpeedlimitEditor_(), NewTheme.I().getIcon("speed", 18));
        spinner = new SizeSpinner(1, Long.MAX_VALUE, 1) {
            protected String longToText(long longValue) {

                return _GUI._.SpeedlimitEditor_format(SizeFormatter.formatBytes(longValue));
            }
        };
        spinner.addChangeListener(this);
        spinner.setValue(config.getDownloadSpeedLimit());

        add(lbl);
        add(cb = new ExtCheckBox(lbl, spinner), "width 20!");
        cb.addActionListener(this);
        cb.setSelected(config.isDownloadSpeedLimitEnabled());
        add(spinner, "height 20!,width 100!");

    }

    public void onConfigValidatorError(KeyHandler<?> keyHandler, Throwable validateException) {
    }

    public void onConfigValueModified(final KeyHandler<?> keyHandler, final Object newValue) {
        if (keyHandler == GeneralSettings.DOWNLOAD_SPEED_LIMIT) {
            new EDTRunner() {

                @Override
                protected void runInEDT() {
                    spinner.setValue(newValue);
                }
            };
        } else {
            new EDTRunner() {

                @Override
                protected void runInEDT() {
                    cb.setSelected(config.isDownloadSpeedLimitEnabled());
                }
            };
        }

    }

    public void stateChanged(ChangeEvent e) {
        config.setDownloadSpeedLimit((int) spinner.getBytes());

    }

    public void actionPerformed(ActionEvent e) {
        config.setDownloadSpeedLimitEnabled(cb.isSelected());
    }

}