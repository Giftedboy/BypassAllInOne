package extension.hanlder;

import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import burp.api.montoya.ui.contextmenu.InvocationType;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;
import extension.BypassExtension;
import extension.config.Type;
import extension.core.Bypass;

import javax.swing.*;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static burp.api.montoya.ui.contextmenu.InvocationType.*;

public class ContextMenus implements ContextMenuItemsProvider {

    @Override
    public List<Component> provideMenuItems(ContextMenuEvent event) {
        List<Component> MenuComponents = new ArrayList<>();
        List<InvocationType> ViewerDisplay = new ArrayList<>(Arrays.asList(
                MESSAGE_EDITOR_REQUEST,
                MESSAGE_EDITOR_RESPONSE,
                MESSAGE_VIEWER_REQUEST,
                MESSAGE_VIEWER_RESPONSE,
                PROXY_HISTORY
        ));
        List<InvocationType> EditorDisplay = new ArrayList<>(Arrays.asList(
                MESSAGE_EDITOR_REQUEST,
                MESSAGE_EDITOR_RESPONSE
        ));

        if(EditorDisplay.contains(event.invocationType())){
            //需要指定标签位置
            JMenuItem BypassSSRF = new JMenuItem("SSRF");
            JMenuItem BypassCSRF = new JMenuItem("CSRF");
            List<HttpRequestResponse> RequestResponses = new ArrayList<HttpRequestResponse>();
            Optional<MessageEditorHttpRequestResponse> EditMessage = event.messageEditorRequestResponse();
            EditMessage.ifPresent(messageEditorHttpRequestResponse -> RequestResponses.add(messageEditorHttpRequestResponse.requestResponse()));

            AddAction(BypassSSRF,RequestResponses,Type.BypassSSRF);
            AddAction(BypassCSRF,RequestResponses,Type.BypassCSRF);

            MenuComponents.add(BypassCSRF.getComponent());
            MenuComponents.add(BypassSSRF.getComponent());
        }

        if(ViewerDisplay.contains(event.invocationType())){
            //
            JMenuItem Bypass40X = new JMenuItem("40X");


            JMenuItem BypassCORS = new JMenuItem("CORS");
            List<HttpRequestResponse> RequestResponses = new ArrayList<HttpRequestResponse>();

            if(event.invocationType() == PROXY_HISTORY){
                RequestResponses = event.selectedRequestResponses();
            }else{
                Optional<MessageEditorHttpRequestResponse> EditMessage = event.messageEditorRequestResponse();
                if(EditMessage.isPresent()){
                    RequestResponses.add(EditMessage.get().requestResponse());
                }

            }

            AddAction(Bypass40X,RequestResponses,Type.Bypass40X);
            AddAction(BypassCORS,RequestResponses,Type.BypassCORS);

            MenuComponents.add(Bypass40X.getComponent());
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
