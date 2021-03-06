package jd.gui.swing.jdgui.views.settings.panels.linkgrabberfilter.editdialog;

import javax.swing.JComponent;

import jd.gui.swing.laf.LookAndFeelController;

import org.appwork.app.gui.MigPanel;
import org.appwork.utils.swing.dialog.Dialog;
import org.appwork.utils.swing.dialog.DialogCanceledException;
import org.appwork.utils.swing.dialog.DialogClosedException;
import org.jdownloader.controlling.filter.LinkgrabberFilterRule;
import org.jdownloader.gui.translate._GUI;

public class ExceptionsRuleDialog extends ConditionDialog<LinkgrabberFilterRule> {

    private LinkgrabberFilterRule rule;

    public ExceptionsRuleDialog(LinkgrabberFilterRule filterRule) {
        super();
        this.rule = filterRule;
        setTitle(_GUI._.ExceptionsRuleDialog_ExceptionsRuleDialog_title_());

    }

    public static void main(String[] args) {
        try {
            LookAndFeelController.getInstance().setUIManager();
            Dialog.getInstance().showDialog(new ExceptionsRuleDialog(new LinkgrabberFilterRule()));
        } catch (DialogClosedException e) {
            e.printStackTrace();
        } catch (DialogCanceledException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected LinkgrabberFilterRule createReturnValue() {
        return rule;
    }

    @Override
    protected void setReturnmask(boolean b) {
        super.setReturnmask(b);
        if (b) {
            save();
        }
    }

    private void save() {
        rule.setFilenameFilter(getFilenameFilter());
        rule.setHosterURLFilter(getHosterFilter());
        rule.setName(getName());
        rule.setFilesizeFilter(getFilersizeFilter());
        rule.setSourceURLFilter(getSourceFilter());
        rule.setFiletypeFilter(getFiletypeFilter());
        rule.setOnlineStatusFilter(getOnlineStatusFilter());
        rule.setPluginStatusFilter(getPluginStatusFilter());
        rule.setAccept(true);
        rule.setIconKey(getIconKey());

    }

    private void updateGUI() {

        setIconKey(rule.getIconKey());
        setFilenameFilter(rule.getFilenameFilter());
        setHosterFilter(rule.getHosterURLFilter());
        setName(rule.getName());

        setFilesizeFilter(rule.getFilesizeFilter());
        setOnlineStatusFilter(rule.getOnlineStatusFilter());
        setPluginStatusFilter(rule.getPluginStatusFilter());
        setSourceFilter(rule.getSourceURLFilter());
        setFiletypeFilter(rule.getFiletypeFilter());
    }

    protected String getIfText() {
        return _GUI._.ExceptionsRuleDialog_getIfText_();
    }

    @Override
    public JComponent layoutDialogContent() {
        MigPanel ret = (MigPanel) super.layoutDialogContent();
        // ret.add(createHeader(_GUI._.ExceptionsRuleDialog_layoutDialogContent_then()),
        // "gaptop 10, spanx,growx,pushx");

        updateGUI();
        return ret;
    }

}
