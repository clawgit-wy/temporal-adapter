package com.yutong.modules.common.utils;



import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

import java.util.Map;

public class SoapNsPrefixMapper extends NamespacePrefixMapper {

    private final Map<String, String> nsMap;

    public SoapNsPrefixMapper(Map<String, String> nsMap) {
        this.nsMap = nsMap;
    }

    @Override
    public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
        return nsMap.getOrDefault(namespaceUri, suggestion);
    }

    @Override
    public String[] getPreDeclaredNamespaceUris() {
        return nsMap.keySet().toArray(new String[0]);
    }
}
