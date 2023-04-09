package extension.hanlder;

import burp.api.montoya.http.message.HttpRequestResponse;
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
            JMenuItem Bypass40X = new JMenuItem("40X");
            JMenuItem BypassSSRF = new JMenuItem("SSRF");
            JMenuItem BypassCSRF = new JMenuItem("CSRF");
            JMenuItem BypassCORS = new JMenuItem("CORS");
            List<HttpRequestResponse> RequestResponses = event.selectedRequestResponses();
            AddAction(Bypass40X,RequestResponses,Type.Bypass40X);
            AddAction(BypassSSRF,RequestResponses,Type.BypassSSRF);
            AddAction(BypassCSRF,RequestResponses,Type.BypassCSRF);
            AddAction(BypassCORS,RequestResponses,Type.BypassCORS);

            MenuComponents.add(Bypass40X.getComponent());
            MenuComponents.add(BypassCSRF.getComponent());
            MenuComponents.add(BypassSSRF.getComponent());
            MenuComponents.add(BypassCORS.getComponent());

        }
        return MenuComponents;
    }

    private void AddAction(JMenuItem MenuItem,List<HttpRequestResponse> RequestResponses,Type BypassType){
        MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Bypass bypass = new Bypass(RequestResponses, BypassType);
                bypass.Scan();
            }
        });
    }
}
