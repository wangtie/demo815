package dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.plugin.AbstractUIPlugin;
public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {
private IWorkbenchWindow window;
private TrayItem trayItem;
private Image trayImage;
private final static String COMMAND_ID = "de.vogella.rcp.intro.traytest.exitCommand";
public ApplicationWorkbenchWindowAdvisor(
IWorkbenchWindowConfigurer configurer) {
super(configurer);}
public ActionBarAdvisor createActionBarAdvisor(
IActionBarConfigurer configurer) {
return new ApplicationActionBarAdvisor(configurer);
}
public void preWindowOpen() {
IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
configurer.setInitialSize(new Point(400, 300));
configurer.setShowCoolBar(false);
configurer.setShowStatusLine(false);
configurer.setTitle("Hello RCP"); //$NON-NLS-1$
}
@Override
public void postWindowOpen() {
super.postWindowOpen();
window = getWindowConfigurer().getWindow();
trayItem = initTaskItem(window);
if (trayItem != null) {
minimizeBehavior();
hookPopupMenu();
}
}
private void minimizeBehavior() {
window.getShell().addShellListener(new ShellAdapter() {
public void shellIconified(ShellEvent e) {
window.getShell().setVisible(false);
}
});
trayItem.addListener(SWT.DefaultSelection, new Listener() {
public void handleEvent(Event event) {
Shell shell = window.getShell();
if (!shell.isVisible()) {
window.getShell().setMinimized(false);
shell.setVisible(true);
}}});}
private void hookPopupMenu() {
trayItem.addListener(SWT.MenuDetect, new Listener() {
public void handleEvent(Event event) {
Menu menu = new Menu(window.getShell(), SWT.POP_UP);
MenuItem exit = new MenuItem(menu, SWT.NONE);
exit.setText("Goodbye!");
exit.addListener(SWT.Selection, new Listener() {
public void handleEvent(Event event) {
IHandlerService handlerService =
(IHandlerService) window.getService(IHandlerService.class);
try {
handlerService.executeCommand(COMMAND_ID, null);
} catch (Exception ex) {
throw new RuntimeException(COMMAND_ID);
}
}
});
menu.setVisible(true);
}
});
}
private TrayItem initTaskItem(IWorkbenchWindow window) {
final Tray tray = window.getShell().getDisplay().getSystemTray();
TrayItem trayItem = new TrayItem(tray, SWT.NONE);
trayImage = AbstractUIPlugin.imageDescriptorFromPlugin(
"dialog", "/icons/alt_about.gif").createImage();
trayItem.setImage(trayImage);
trayItem.setToolTipText("TrayItem");
return trayItem;}
@Override
public void dispose() {
if (trayImage != null) {
trayImage.dispose();
}
if (trayItem != null) {
trayItem.dispose();}}}
