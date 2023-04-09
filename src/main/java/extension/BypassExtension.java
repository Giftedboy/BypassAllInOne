package extension;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import extension.hanlder.ContextMenus;

public class BypassExtension implements BurpExtension {
    public static MontoyaApi Api;
    private final String ExtensionName = "BypassAllInOne";
    @Override
    public void initialize(MontoyaApi montoyaApi) {
        Api = montoyaApi;
        Api.extension().setName(ExtensionName);
        Api.userInterface().registerContextMenuItemsProvider(new ContextMenus());
    }
}
