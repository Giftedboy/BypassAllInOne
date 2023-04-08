package extension.hanlder;

import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import burp.api.montoya.ui.contextmenu.InvocationType;
import extension.config.Type;
import extension.core.Bypass;

import javax.swing.*;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static burp.api.montoya.ui.contextmenu.InvocationType.*;

public class ContextMenus implements ContextMenuItemsProvider {

    @Override
    public List<Component> provideMenuItems(ContextMenuEvent event) {
        List<Component> MenuComponents = new ArrayList<>();
        List<InvocationType> Display = new ArrayList<>(Arrays.asList(
                MESSAGE_EDITOR_REQUEST,
                MESSAGE_EDITOR_RESPONSE,
                MESSAGE_VIEWER_REQUEST,
                MESSAGE_VIEWER_RESPONSE,
                PROXY_HISTORY
        ));
        if(Display.contains(event.invocationType())){
            //
            JMenuItem Bypass40X = new JMenuItem();
            Bypass40X.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Bypass bypass = new Bypass(event.selectedRequestResponses(), Type.Bypass40X);
                            bypass.Scan();
                        }
                    }).start();
                }
            });
            MenuComponents.add(Bypass40X.getComponent());
        }
        return MenuComponents;
    }

}
