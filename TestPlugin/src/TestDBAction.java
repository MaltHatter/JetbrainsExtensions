import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import dialogs.TestGridDialog;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;


public class TestDBAction extends AnAction {
    private final NotificationGroup MONGO_GROUP = NotificationGroup.logOnlyGroup("Mongo");

    @Override
    public void actionPerformed(AnActionEvent e) {
        //popup dialog
        //Messages.showInfoMessage("Hello World!", "Hello");
       /* DialogWrapper dialogWrapper = new DialogWrapper(PlatformDataKeys.PROJECT.getData(e.getDataContext())) {
            {
                init();
            }

            @Nullable
            @Override
            protected JComponent createCenterPanel() {
                return new TestGridDialog().getPanel();
            }
        };
        dialogWrapper.showAndGet();*/
        Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        MONGO_GROUP.createNotification("[MongoPlugin] " , NotificationType.INFORMATION)
                .notify(project);
        MONGO_GROUP.createNotification("[MongoPlugin]  " , NotificationType.ERROR)
                .notify(project);
        MONGO_GROUP.createNotification("[MongoPlugin]  " , NotificationType.WARNING)
                .notify(project);



    }

    /*@Override
    public void update(AnActionEvent e) {
        super.update(e);
        e.getPresentation().setIcon(AllIcons.Ide.Info_notifications);
    }*/

}
